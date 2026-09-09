import React, { useEffect, useMemo, useState } from 'react';
import {
  AlertTriangle, Building2, CheckCircle2, Globe2, Plus, RefreshCw,
  Search, ShieldAlert, Target, Trash2, TrendingUp, X
} from 'lucide-react';
import { supplierAPI } from '../services/api';
import { useAuth } from '../hooks/useAuth.js';

const RISK_CLASS = {
  CRITICAL: 'nf-badge-critical',
  HIGH: 'nf-badge-high',
  MEDIUM: 'nf-badge-medium',
  LOW: 'nf-badge-low',
};

const toNumber = (value, fallback = 0) => {
  const num = Number(value);
  return Number.isFinite(num) ? num : fallback;
};

const toPercent = (value) => {
  const num = toNumber(value);
  return num <= 5 ? num * 20 : num;
};

const clamp = (value) => Math.max(0, Math.min(100, value));

const getDelayRate = (supplier) => {
  if (supplier?.delayRate != null) return toNumber(supplier.delayRate);
  const total = toNumber(supplier?.totalShipments);
  if (total === 0) return 0;
  return (toNumber(supplier?.delayedShipments) / total) * 100;
};

const getRiskScore = (supplier) => {
  if (supplier?.supplierRiskScore != null) return toNumber(supplier.supplierRiskScore);
  const reliabilityRisk = 100 - clamp(toPercent(supplier?.reliabilityScore));
  const delayRisk = clamp(getDelayRate(supplier) * 1.4);
  const geoRisk = clamp(toNumber(supplier?.geopoliticalExposureScore, reliabilityRisk));
  return Math.round((reliabilityRisk * 0.45) + (delayRisk * 0.35) + (geoRisk * 0.2));
};

const getRiskLevel = (supplier) => {
  const explicit = supplier?.riskLevel?.toUpperCase?.();
  if (explicit) return explicit;
  const score = getRiskScore(supplier);
  if (score >= 85) return 'CRITICAL';
  if (score >= 70) return 'HIGH';
  if (score >= 45) return 'MEDIUM';
  return 'LOW';
};

const formatScore = (value, suffix = '%') => {
  if (value == null || value === '') return 'N/A';
  const num = toNumber(value);
  return `${num.toFixed(0)}${suffix}`;
};

const MetricCard = ({ label, value, icon: Icon, tone = 'blue', subtext }) => (
  <div className={`nf-kpi nf-kpi-${tone}`}>
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
      <div className="nf-section-title-icon" style={{ background: 'rgba(79,124,255,0.12)' }}>
        <Icon size={16} color="var(--accent-blue)" />
      </div>
    </div>
    <div className="kpi-value">{value}</div>
    <div className="kpi-label">{label}</div>
    {subtext && <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 5 }}>{subtext}</div>}
  </div>
);

const ScoreBar = ({ label, value, inverse = false }) => {
  const normalized = clamp(toPercent(value));
  const display = value == null || value === '' ? 'N/A' : `${Math.round(normalized)}%`;
  const good = inverse ? normalized <= 35 : normalized >= 75;
  const warn = inverse ? normalized <= 65 : normalized >= 50;
  const color = good ? '#10b981' : warn ? '#f59e0b' : '#ef4444';

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, marginBottom: 6 }}>
        <span style={{ fontSize: 12, color: 'var(--text-secondary)' }}>{label}</span>
        <span style={{ fontSize: 12, fontWeight: 700, color }}>{display}</span>
      </div>
      <div className="nf-progress">
        <div className="nf-progress-bar" style={{ width: `${normalized}%`, background: color }} />
      </div>
    </div>
  );
};

function Suppliers() {
  const { canManageSuppliers, role } = useAuth();
  const [suppliers, setSuppliers] = useState([]);
  const [countryStats, setCountryStats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedId, setSelectedId] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    contactPerson: '',
    email: '',
    phone: '',
    address: '',
    country: '',
  });

  const fetchData = async () => {
    try {
      setLoading(true);
      setError('');
      const [supplierRes, countryRes] = await Promise.all([
        supplierAPI.getAll(),
        supplierAPI.getByCountryStats(),
      ]);
      const rawSuppliers = supplierRes.data?.content ?? supplierRes.data;
      const supplierList = Array.isArray(rawSuppliers) ? rawSuppliers : [];
      setSuppliers(supplierList);
      const rawCountries = countryRes.data?.content ?? countryRes.data;
      setCountryStats(Array.isArray(rawCountries) ? rawCountries : []);
      setSelectedId((current) => current ?? supplierList[0]?.id ?? null);
    } catch (e) {
      setError(e?.message || 'Failed to load supplier intelligence');
      console.error('Error fetching suppliers:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSearch = async () => {
    const q = searchQuery.trim();
    try {
      setLoading(true);
      setError('');
      if (!q) {
        await fetchData();
        return;
      }
      const res = await supplierAPI.search(q);
      const results = Array.isArray(res.data) ? res.data : [];
      setSuppliers(results);
      setSelectedId(results[0]?.id ?? null);
    } catch (e) {
      setError(e?.message || 'Search failed');
      console.error('Search error:', e);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setError('');
      await supplierAPI.create(formData);
      setShowForm(false);
      setFormData({ name: '', contactPerson: '', email: '', phone: '', address: '', country: '' });
      await fetchData();
    } catch (e2) {
      setError(e2?.response?.data?.message || e2?.message || 'Failed to create supplier');
      console.error('Error creating supplier:', e2);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this supplier?')) return;
    try {
      setError('');
      await supplierAPI.delete(id);
      setSuppliers((prev) => prev.filter((supplier) => supplier.id !== id));
      setSelectedId((current) => current === id ? null : current);
    } catch (e) {
      setError(e?.message || 'Failed to delete supplier');
      console.error('Error deleting supplier:', e);
    }
  };

  const rankedSuppliers = useMemo(() => {
    return [...suppliers].sort((a, b) => getRiskScore(b) - getRiskScore(a));
  }, [suppliers]);

  const selectedSupplier = useMemo(() => {
    return suppliers.find((supplier) => supplier.id === selectedId) ?? rankedSuppliers[0] ?? null;
  }, [rankedSuppliers, selectedId, suppliers]);

  const alternatives = useMemo(() => {
    if (!selectedSupplier) return [];
    return suppliers
      .filter((supplier) => supplier.id !== selectedSupplier.id)
      .sort((a, b) => {
        const countryBoost = a.country === selectedSupplier.country ? 8 : 0;
        const bCountryBoost = b.country === selectedSupplier.country ? 8 : 0;
        const aScore = clamp(toPercent(a.reliabilityScore)) - getRiskScore(a) + countryBoost;
        const bScore = clamp(toPercent(b.reliabilityScore)) - getRiskScore(b) + bCountryBoost;
        return bScore - aScore;
      })
      .slice(0, 3);
  }, [selectedSupplier, suppliers]);

  const metrics = useMemo(() => {
    const total = suppliers.length;
    const highRisk = suppliers.filter((supplier) => ['HIGH', 'CRITICAL'].includes(getRiskLevel(supplier))).length;
    const avgReliability = total
      ? suppliers.reduce((sum, supplier) => sum + clamp(toPercent(supplier.reliabilityScore)), 0) / total
      : 0;
    const avgDelay = total
      ? suppliers.reduce((sum, supplier) => sum + getDelayRate(supplier), 0) / total
      : 0;
    const countries = new Set(suppliers.map((supplier) => supplier.country).filter(Boolean)).size;

    return {
      total,
      highRisk,
      avgReliability,
      avgDelay,
      countries,
    };
  }, [suppliers]);

  const selectedRiskLevel = selectedSupplier ? getRiskLevel(selectedSupplier) : 'LOW';
  const selectedRiskScore = selectedSupplier ? getRiskScore(selectedSupplier) : 0;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      <div className="nf-fade-in" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 12 }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.375rem', fontWeight: 800 }}>
            Supplier Intelligence
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: 13, color: 'var(--text-muted)' }}>
            Rank suppliers by resilience, disruption exposure, and backup readiness.
          </p>
        </div>
        <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'center' }}>
          <button type="button" onClick={fetchData} className="nf-btn nf-btn-secondary">
            <RefreshCw size={14} /> Refresh
          </button>
          {canManageSuppliers ? (
            <button type="button" onClick={() => setShowForm((value) => !value)} className="nf-btn nf-btn-primary">
              {showForm ? <X size={14} /> : <Plus size={14} />}
              {showForm ? 'Cancel' : 'Add Supplier'}
            </button>
          ) : (
            <span style={{ fontSize: 11, color: 'var(--text-muted)', background: 'var(--bg-card)', padding: '6px 12px', borderRadius: 8, border: '1px solid var(--border-subtle)' }}>
              Read-Only View ({role})
            </span>
          )}
        </div>
      </div>

      {error && (
        <div className="nf-card" style={{ background: 'rgba(239,68,68,0.08)', border: '1px solid rgba(239,68,68,0.25)', color: '#f87171' }}>
          <AlertTriangle size={14} style={{ marginRight: 8, display: 'inline' }} />
          {error}
        </div>
      )}

      {showForm && (
        <div className="nf-card nf-fade-in">
          <h3 className="text-lg font-semibold mb-4" style={{ color: 'var(--text-primary)' }}>Create Supplier Profile</h3>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <input name="name" placeholder="Supplier name" value={formData.name} onChange={handleInputChange} className="nf-input" required />
            <input name="contactPerson" placeholder="Contact person" value={formData.contactPerson} onChange={handleInputChange} className="nf-input" required />
            <input name="email" type="email" placeholder="Email" value={formData.email} onChange={handleInputChange} className="nf-input" required />
            <input name="phone" placeholder="Phone" value={formData.phone} onChange={handleInputChange} className="nf-input" />
            <input name="address" placeholder="Address" value={formData.address} onChange={handleInputChange} className="nf-input" />
            <input name="country" placeholder="Country" value={formData.country} onChange={handleInputChange} className="nf-input" required />
            <button type="submit" className="nf-btn nf-btn-primary md:col-span-3">
              <Plus size={14} /> Create Supplier
            </button>
          </form>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(190px, 1fr))', gap: 14 }}>
        <MetricCard label="Active Suppliers" value={metrics.total.toLocaleString()} icon={Building2} tone="blue" />
        <MetricCard label="High Risk Suppliers" value={metrics.highRisk.toLocaleString()} icon={ShieldAlert} tone="red" />
        <MetricCard label="Avg Reliability" value={`${metrics.avgReliability.toFixed(0)}%`} icon={TrendingUp} tone="green" />
        <MetricCard label="Avg Delay Rate" value={`${metrics.avgDelay.toFixed(1)}%`} icon={AlertTriangle} tone="orange" />
        <MetricCard label="Countries Covered" value={metrics.countries.toLocaleString()} icon={Globe2} tone="purple" />
      </div>

      <div className="nf-card" style={{ padding: '12px 16px' }}>
        <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap', alignItems: 'end' }}>
          <div style={{ flex: 1, minWidth: 220, position: 'relative' }}>
            <label className="nf-label" htmlFor="supplier-search">Search suppliers</label>
            <Search size={14} style={{ position: 'absolute', left: 10, bottom: 10, color: 'var(--text-muted)' }} />
            <input
              id="supplier-search"
              type="text"
              placeholder="Search by supplier name..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              className="nf-input"
              style={{ paddingLeft: 32 }}
            />
          </div>
          <button type="button" onClick={handleSearch} className="nf-btn nf-btn-primary">
            <Search size={14} /> Search
          </button>
          <button type="button" onClick={fetchData} className="nf-btn nf-btn-secondary">
            <RefreshCw size={14} /> Reset
          </button>
        </div>
      </div>

      <div className="nf-grid-detail-layout">
        <div className="nf-card" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-subtle)', display: 'flex', justifyContent: 'space-between', gap: 12 }}>
            <div>
              <div className="nf-section-title">
                <Target size={16} color="var(--accent-blue)" /> Supplier Risk Ranking
              </div>
              <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4 }}>
                Ordered by risk exposure and delivery resilience.
              </div>
            </div>
            <span className="nf-badge nf-badge-blue">{rankedSuppliers.length} profiles</span>
          </div>

          <div style={{ overflowX: 'auto' }}>
            <table className="nf-table">
              <thead>
                <tr>
                  <th>Supplier</th>
                  <th>Country</th>
                  <th>Reliability</th>
                  <th>Delay Rate</th>
                  <th>Risk</th>
                  <th>Signals</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  Array.from({ length: 6 }).map((_, i) => (
                    <tr key={i}>
                      {Array.from({ length: 7 }).map((__, j) => (
                        <td key={j}><div className="nf-skeleton" style={{ height: 18, borderRadius: 4, width: j === 0 ? '120px' : '80%' }} /></td>
                      ))}
                    </tr>
                  ))
                ) : rankedSuppliers.length === 0 ? (
                  <tr>
                    <td colSpan={7} style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
                      No suppliers found.
                    </td>
                  </tr>
                ) : (
                  rankedSuppliers.map((supplier) => {
                    const riskLevel = getRiskLevel(supplier);
                    const reliability = clamp(toPercent(supplier.reliabilityScore));
                    const delayRate = getDelayRate(supplier);
                    const isSelected = selectedSupplier?.id === supplier.id;

                    return (
                      <tr
                        key={supplier.id}
                        onClick={() => setSelectedId(supplier.id)}
                        style={{ cursor: 'pointer', background: isSelected ? 'var(--bg-elevated)' : undefined }}
                      >
                        <td>
                          <div style={{ color: 'var(--text-primary)', fontWeight: 700 }}>{supplier.name}</div>
                          <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{supplier.industry || supplier.companyName || supplier.contactPerson || 'Supplier profile'}</div>
                        </td>
                        <td>{supplier.country || 'N/A'}</td>
                        <td>
                          <div style={{ display: 'flex', flexDirection: 'column', gap: 5, minWidth: 110 }}>
                            <span style={{ color: reliability >= 75 ? '#34d399' : reliability >= 50 ? '#fbbf24' : '#f87171', fontWeight: 700 }}>
                              {reliability.toFixed(0)}%
                            </span>
                            <div className="nf-progress" style={{ height: 4 }}>
                              <div className="nf-progress-bar" style={{ width: `${reliability}%`, background: reliability >= 75 ? '#10b981' : reliability >= 50 ? '#f59e0b' : '#ef4444' }} />
                            </div>
                          </div>
                        </td>
                        <td>{delayRate.toFixed(1)}%</td>
                        <td>
                          <div style={{ display: 'flex', flexDirection: 'column', gap: 5 }}>
                            <span className={`nf-badge ${RISK_CLASS[riskLevel] || 'nf-badge-low'}`}>{riskLevel}</span>
                            <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>{getRiskScore(supplier).toFixed(0)}/100</span>
                          </div>
                        </td>
                        <td style={{ minWidth: 180 }}>
                          <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                            {toNumber(supplier.totalShipments).toLocaleString()} shipments,
                            {' '}
                            {toNumber(supplier.delayedShipments).toLocaleString()} delayed
                          </div>
                          <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                            Geo: {formatScore(supplier.geopoliticalExposureScore)}
                          </div>
                        </td>
                        <td>
                          {canManageSuppliers ? (
                            <button
                              type="button"
                              onClick={(event) => {
                                event.stopPropagation();
                                handleDelete(supplier.id);
                              }}
                              className="nf-btn nf-btn-danger nf-btn-sm"
                              aria-label={`Delete ${supplier.name}`}
                            >
                              <Trash2 size={12} />
                            </button>
                          ) : (
                            <span style={{ fontSize: 11, color: 'var(--text-muted)' }}>-</span>
                          )}
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div className="nf-card">
            {selectedSupplier ? (
              <>
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, marginBottom: 16 }}>
                  <div>
                    <h3 style={{ margin: 0, fontSize: '1rem', color: 'var(--text-primary)' }}>
                      {selectedSupplier.name}
                    </h3>
                    <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4 }}>
                      {selectedSupplier.country || 'Unknown country'} | {selectedSupplier.industry || 'General supplier'}
                    </div>
                  </div>
                  <span className={`nf-badge ${RISK_CLASS[selectedRiskLevel] || 'nf-badge-low'}`}>{selectedRiskLevel}</span>
                </div>

                <div className="nf-grid-2-responsive" style={{ gap: 10, marginBottom: 18 }}>
                  <div className="nf-ai-recommendation" style={{ marginBottom: 0 }}>
                    <div style={{ fontSize: 11, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Risk Score</div>
                    <div style={{ fontSize: 24, fontWeight: 800, color: 'var(--text-primary)' }}>{selectedRiskScore.toFixed(0)}</div>
                  </div>
                  <div className="nf-ai-recommendation" style={{ marginBottom: 0 }}>
                    <div style={{ fontSize: 11, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Delay Rate</div>
                    <div style={{ fontSize: 24, fontWeight: 800, color: 'var(--text-primary)' }}>{getDelayRate(selectedSupplier).toFixed(1)}%</div>
                  </div>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                  <ScoreBar label="Delivery Performance" value={selectedSupplier.deliveryPerformanceScore ?? selectedSupplier.reliabilityScore} />
                  <ScoreBar label="Quality" value={selectedSupplier.qualityScore ?? selectedSupplier.reliabilityScore} />
                  <ScoreBar label="Financial Stability" value={selectedSupplier.financialStabilityScore ?? selectedSupplier.reliabilityScore} />
                  <ScoreBar label="Geopolitical Exposure" value={selectedSupplier.geopoliticalExposureScore ?? selectedRiskScore} inverse />
                  <ScoreBar label="Historical Reliability" value={selectedSupplier.historicalReliabilityScore ?? selectedSupplier.reliabilityScore} />
                </div>
              </>
            ) : (
              <div style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '2rem 0' }}>
                Select a supplier to inspect resilience signals.
              </div>
            )}
          </div>

          <div className="nf-card">
            <div className="nf-section-title" style={{ marginBottom: 12 }}>
              <CheckCircle2 size={16} color="var(--accent-green)" /> Backup Options
            </div>
            {alternatives.length === 0 ? (
              <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No alternate suppliers available.</div>
            ) : (
              alternatives.map((supplier) => {
                const riskLevel = getRiskLevel(supplier);
                return (
                  <div key={supplier.id} className="nf-ai-recommendation">
                    <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12 }}>
                      <div>
                        <div style={{ fontWeight: 700, color: 'var(--text-primary)' }}>{supplier.name}</div>
                        <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{supplier.country || 'N/A'}</div>
                      </div>
                      <span className={`nf-badge ${RISK_CLASS[riskLevel] || 'nf-badge-low'}`}>{riskLevel}</span>
                    </div>
                    <div style={{ marginTop: 8, fontSize: 12, color: 'var(--text-muted)' }}>
                      Reliability {clamp(toPercent(supplier.reliabilityScore)).toFixed(0)}% | Delay {getDelayRate(supplier).toFixed(1)}%
                    </div>
                  </div>
                );
              })
            )}
          </div>

          <div className="nf-card">
            <div className="nf-section-title" style={{ marginBottom: 12 }}>
              <Globe2 size={16} color="var(--accent-cyan)" /> Country Exposure
            </div>
            {countryStats.length === 0 ? (
              <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No country distribution available.</div>
            ) : (
              countryStats.slice(0, 6).map((country) => (
                <div key={country.country} style={{ marginBottom: 12 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                    <span style={{ fontSize: 12, color: 'var(--text-secondary)' }}>{country.country || 'Unknown'}</span>
                    <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>{country.count}</span>
                  </div>
                  <div className="nf-progress">
                    <div
                      className="nf-progress-bar"
                      style={{
                        width: `${Math.min(100, (toNumber(country.count) / Math.max(1, metrics.total)) * 100)}%`,
                        background: 'linear-gradient(90deg, var(--accent-blue), var(--accent-cyan))',
                      }}
                    />
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Suppliers;
