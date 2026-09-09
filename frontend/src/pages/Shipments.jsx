import React, { useEffect, useMemo, useState } from 'react';
import {
  AlertTriangle, CheckCircle, Clock, Filter, MapPin, Package, Plus,
  RefreshCw, Search, ShieldAlert, ShipWheel, Truck, X, XCircle, Zap
} from 'lucide-react';
import { riskAPI, shipmentAPI, supplierAPI } from '../services/api';
import { useAuth } from '../hooks/useAuth.js';

const STATUS_CONFIG = {
  PENDING: { label: 'Pending', cls: 'nf-badge-pending', icon: Clock },
  IN_TRANSIT: { label: 'In Transit', cls: 'nf-badge-in_transit', icon: Truck },
  DELAYED: { label: 'Delayed', cls: 'nf-badge-delayed', icon: AlertTriangle },
  DELIVERED: { label: 'Delivered', cls: 'nf-badge-delivered', icon: CheckCircle },
  CANCELLED: { label: 'Cancelled', cls: 'nf-badge-cancelled', icon: XCircle },
  CREATED: { label: 'Created', cls: 'nf-badge-created', icon: Package },
};

const PRIORITY_COLORS = {
  LOW: '#6b7280',
  MEDIUM: '#f59e0b',
  HIGH: '#f97316',
  URGENT: '#ef4444',
};

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
  return num <= 1 ? num * 100 : num;
};

const clamp = (value) => Math.max(0, Math.min(100, value));

const getRiskLevelFromScore = (score) => {
  if (score >= 85) return 'CRITICAL';
  if (score >= 70) return 'HIGH';
  if (score >= 45) return 'MEDIUM';
  return 'LOW';
};

const StatusBadge = ({ status }) => {
  const cfg = STATUS_CONFIG[status] ?? { label: status || 'Pending', cls: 'nf-badge-blue' };
  return <span className={`nf-badge ${cfg.cls}`}>{cfg.label}</span>;
};

const RiskBadge = ({ level }) => (
  <span className={`nf-badge ${RISK_CLASS[level] || 'nf-badge-low'}`}>{level || 'LOW'}</span>
);

const formatCurrency = (value) =>
  new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 0,
  }).format(toNumber(value));

const formatDate = (value) => value || 'N/A';

function Shipments() {
  const { canManageShipments, role } = useAuth();
  const [shipments, setShipments] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [delayPredictions, setDelayPredictions] = useState([]);
  const [riskSignals, setRiskSignals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [selectedId, setSelectedId] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    supplierId: '',
    originCity: '',
    originCountry: '',
    destinationCity: '',
    destinationCountry: '',
    shipmentDate: '',
    estimatedDeliveryDate: '',
    cargoType: '',
    weightKg: '',
    valueUsd: '',
    priority: 'MEDIUM',
    notes: '',
  });

  const fetchData = async () => {
    try {
      setLoading(true);
      setError('');
      const [shipmentsRes, suppliersRes, delaysRes, highRiskRes, criticalRiskRes] = await Promise.all([
        shipmentAPI.getAll(),
        supplierAPI.getAll(),
        riskAPI.getPredictedDelays(),
        riskAPI.getHighRisk(),
        riskAPI.getCriticalRisk(),
      ]);

      const rawShipments = shipmentsRes.data?.content ?? shipmentsRes.data;
      const shipmentList = Array.isArray(rawShipments) ? rawShipments : [];
      setShipments(shipmentList);

      const rawSuppliers = suppliersRes.data?.content ?? suppliersRes.data;
      setSuppliers(Array.isArray(rawSuppliers) ? rawSuppliers : []);

      const rawDelays = delaysRes.data?.content ?? delaysRes.data;
      setDelayPredictions(Array.isArray(rawDelays) ? rawDelays : []);

      const rawHigh = highRiskRes.data?.content ?? highRiskRes.data;
      const rawCrit = criticalRiskRes.data?.content ?? criticalRiskRes.data;
      setRiskSignals([
        ...(Array.isArray(rawHigh) ? rawHigh : []),
        ...(Array.isArray(rawCrit) ? rawCrit : []),
      ]);
      setSelectedId((current) => current ?? shipmentList[0]?.id ?? null);
    } catch (e) {
      setError(e?.response?.data?.message ?? e?.message ?? 'Failed to load shipment intelligence');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const delayByShipment = useMemo(() => {
    const map = new Map();
    delayPredictions.forEach((prediction) => {
      if (prediction?.shipmentId != null) map.set(prediction.shipmentId, prediction);
      if (prediction?.trackingNumber) map.set(prediction.trackingNumber, prediction);
    });
    return map;
  }, [delayPredictions]);

  const riskByShipment = useMemo(() => {
    const map = new Map();
    riskSignals.forEach((risk) => {
      if (risk?.shipmentId != null) map.set(risk.shipmentId, risk);
      if (risk?.trackingNumber) map.set(risk.trackingNumber, risk);
    });
    return map;
  }, [riskSignals]);

  const getShipmentInsight = (shipment) => {
    const risk = riskByShipment.get(shipment.id) ?? riskByShipment.get(shipment.trackingNumber);
    const prediction = delayByShipment.get(shipment.id) ?? delayByShipment.get(shipment.trackingNumber);
    const rawRiskScore = risk?.riskScore ?? shipment?.routeComplexityScore ?? shipment?.weatherImpactScore ?? 0;
    const score = clamp(toNumber(rawRiskScore));
    const delayProbability = risk?.delayProbability != null
      ? clamp(toPercent(risk.delayProbability))
      : prediction?.isDelayed
        ? clamp(toPercent(prediction.confidenceScore || 0))
        : 0;
    const riskLevel = risk?.riskLevel ?? getRiskLevelFromScore(Math.max(score, delayProbability));

    return {
      risk,
      prediction,
      score: Math.max(score, delayProbability),
      delayProbability,
      riskLevel,
    };
  };

  const handleSearch = async () => {
    const q = searchQuery.trim();
    if (!q) {
      await fetchData();
      return;
    }
    try {
      setLoading(true);
      setError('');
      const res = await shipmentAPI.search(q);
      const results = Array.isArray(res.data) ? res.data : [];
      setShipments(results);
      setSelectedId(results[0]?.id ?? null);
    } catch (e) {
      setError(e?.message ?? 'Search failed');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setSubmitting(true);
      setError('');
      await shipmentAPI.create(formData);
      setShowForm(false);
      setFormData({
        supplierId: '',
        originCity: '',
        originCountry: '',
        destinationCity: '',
        destinationCountry: '',
        shipmentDate: '',
        estimatedDeliveryDate: '',
        cargoType: '',
        weightKg: '',
        valueUsd: '',
        priority: 'MEDIUM',
        notes: '',
      });
      await fetchData();
    } catch (e2) {
      setError(e2?.response?.data?.message ?? e2?.message ?? 'Failed to create shipment');
    } finally {
      setSubmitting(false);
    }
  };

  const handleStatusUpdate = async (id, status) => {
    try {
      await shipmentAPI.updateStatus(id, status);
      setShipments((prev) => prev.map((shipment) => shipment.id === id ? { ...shipment, status } : shipment));
    } catch (e) {
      setError(e?.message ?? 'Failed to update status');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this shipment?')) return;
    try {
      await shipmentAPI.delete(id);
      setShipments((prev) => prev.filter((shipment) => shipment.id !== id));
      setSelectedId((current) => current === id ? null : current);
    } catch (e) {
      setError(e?.message ?? 'Failed to delete shipment');
    }
  };

  const filtered = useMemo(() => {
    return statusFilter ? shipments.filter((shipment) => shipment.status === statusFilter) : shipments;
  }, [shipments, statusFilter]);

  const selectedShipment = useMemo(() => {
    return shipments.find((shipment) => shipment.id === selectedId) ?? filtered[0] ?? null;
  }, [filtered, selectedId, shipments]);

  const rankedRiskShipments = useMemo(() => {
    return [...shipments]
      .map((shipment) => ({ shipment, insight: getShipmentInsight(shipment) }))
      .sort((a, b) => b.insight.score - a.insight.score)
      .slice(0, 5);
  }, [delayByShipment, riskByShipment, shipments]);

  const routeExposure = useMemo(() => {
    const map = new Map();
    shipments.forEach((shipment) => {
      const key = `${shipment.originCountry || 'Unknown'} -> ${shipment.destinationCountry || 'Unknown'}`;
      const current = map.get(key) ?? { route: key, count: 0, delayed: 0, risk: 0 };
      const insight = getShipmentInsight(shipment);
      current.count += 1;
      current.delayed += shipment.status === 'DELAYED' || insight.delayProbability >= 50 ? 1 : 0;
      current.risk += insight.score;
      map.set(key, current);
    });
    return [...map.values()]
      .map((route) => ({ ...route, averageRisk: route.count ? route.risk / route.count : 0 }))
      .sort((a, b) => b.averageRisk - a.averageRisk)
      .slice(0, 5);
  }, [delayByShipment, riskByShipment, shipments]);

  const stats = useMemo(() => {
    const total = shipments.length;
    const inTransit = shipments.filter((shipment) => shipment.status === 'IN_TRANSIT').length;
    const delayed = shipments.filter((shipment) => shipment.status === 'DELAYED').length;
    const delivered = shipments.filter((shipment) => shipment.status === 'DELIVERED').length;
    const atRisk = shipments.filter((shipment) => ['HIGH', 'CRITICAL'].includes(getShipmentInsight(shipment).riskLevel)).length;
    const totalValue = shipments.reduce((sum, shipment) => sum + toNumber(shipment.valueUsd), 0);

    return { total, inTransit, delayed, delivered, atRisk, totalValue };
  }, [delayByShipment, riskByShipment, shipments]);

  const selectedInsight = selectedShipment ? getShipmentInsight(selectedShipment) : null;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      <div className="nf-fade-in" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 12 }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.375rem', fontWeight: 800 }}>Shipment Intelligence</h2>
          <p style={{ margin: '4px 0 0', fontSize: 13, color: 'var(--text-muted)' }}>
            Monitor routes, delivery status, delay forecasts, and active logistics risk.
          </p>
        </div>
        <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'center' }}>
          <button type="button" onClick={fetchData} className="nf-btn nf-btn-secondary">
            <RefreshCw size={14} /> Refresh
          </button>
          {canManageShipments ? (
            <button type="button" onClick={() => setShowForm((value) => !value)} className="nf-btn nf-btn-primary">
              {showForm ? <X size={14} /> : <Plus size={14} />}
              {showForm ? 'Cancel' : 'New Shipment'}
            </button>
          ) : (
            <span style={{ fontSize: 11, color: 'var(--text-muted)', background: 'var(--bg-card)', padding: '6px 12px', borderRadius: 8, border: '1px solid var(--border-subtle)' }}>
              Read-Only View ({role})
            </span>
          )}
        </div>
      </div>

      <div className="nf-fade-in" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(170px, 1fr))', gap: 12 }}>
        {[
          { label: 'Total Shipments', value: stats.total, color: '#4f7cff', filter: '', icon: Package },
          { label: 'In Transit', value: stats.inTransit, color: '#00d4ff', filter: 'IN_TRANSIT', icon: Truck },
          { label: 'Delayed', value: stats.delayed, color: '#ef4444', filter: 'DELAYED', icon: AlertTriangle },
          { label: 'Delivered', value: stats.delivered, color: '#10b981', filter: 'DELIVERED', icon: CheckCircle },
          { label: 'At Risk', value: stats.atRisk, color: '#f97316', filter: '', icon: ShieldAlert },
        ].map(({ label, value, color, filter, icon: Icon }) => (
          <button
            key={label}
            type="button"
            onClick={() => setStatusFilter(filter && statusFilter !== filter ? filter : '')}
            style={{
              background: statusFilter === filter && filter ? `${color}15` : 'var(--bg-card)',
              border: `1px solid ${statusFilter === filter && filter ? color + '40' : 'var(--border-subtle)'}`,
              borderRadius: 12,
              padding: '12px 16px',
              cursor: 'pointer',
              textAlign: 'left',
              transition: 'all 0.2s',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 10 }}>
              <div style={{ fontSize: '1.5rem', fontWeight: 800, color }}>{loading ? '...' : value}</div>
              <Icon size={17} color={color} />
            </div>
            <div style={{ fontSize: 11.5, color: 'var(--text-muted)', marginTop: 3, fontWeight: 500 }}>{label}</div>
          </button>
        ))}
      </div>

      {error && (
        <div style={{
          background: 'rgba(239,68,68,0.08)',
          border: '1px solid rgba(239,68,68,0.25)',
          borderRadius: 10,
          padding: '10px 14px',
          fontSize: 13,
          color: '#f87171',
          display: 'flex',
          alignItems: 'center',
          gap: 8,
        }}>
          <AlertTriangle size={14} />
          {error}
        </div>
      )}

      {showForm && (
        <div className="nf-card nf-fade-in" style={{ background: 'var(--bg-elevated)' }}>
          <h3 style={{ margin: '0 0 16px', fontSize: '1rem', fontWeight: 700 }}>Create Shipment</h3>
          <form onSubmit={handleSubmit}>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(210px, 1fr))', gap: 14 }}>
              <div>
                <label className="nf-label">Supplier *</label>
                <select name="supplierId" value={formData.supplierId} onChange={(e) => setFormData((prev) => ({ ...prev, supplierId: e.target.value }))} className="nf-select" required>
                  <option value="">Select supplier</option>
                  {suppliers.map((supplier) => <option key={supplier.id} value={supplier.id}>{supplier.name}</option>)}
                </select>
              </div>
              <div>
                <label className="nf-label">Origin City *</label>
                <input value={formData.originCity} onChange={(e) => setFormData((prev) => ({ ...prev, originCity: e.target.value }))} className="nf-input" placeholder="Shanghai" required />
              </div>
              <div>
                <label className="nf-label">Origin Country *</label>
                <input value={formData.originCountry} onChange={(e) => setFormData((prev) => ({ ...prev, originCountry: e.target.value }))} className="nf-input" placeholder="China" required />
              </div>
              <div>
                <label className="nf-label">Destination City *</label>
                <input value={formData.destinationCity} onChange={(e) => setFormData((prev) => ({ ...prev, destinationCity: e.target.value }))} className="nf-input" placeholder="Mumbai" required />
              </div>
              <div>
                <label className="nf-label">Destination Country *</label>
                <input value={formData.destinationCountry} onChange={(e) => setFormData((prev) => ({ ...prev, destinationCountry: e.target.value }))} className="nf-input" placeholder="India" required />
              </div>
              <div>
                <label className="nf-label">Cargo Type</label>
                <input value={formData.cargoType} onChange={(e) => setFormData((prev) => ({ ...prev, cargoType: e.target.value }))} className="nf-input" placeholder="Electronics" />
              </div>
              <div>
                <label className="nf-label">Shipment Date *</label>
                <input type="date" value={formData.shipmentDate} onChange={(e) => setFormData((prev) => ({ ...prev, shipmentDate: e.target.value }))} className="nf-input" required />
              </div>
              <div>
                <label className="nf-label">Estimated Delivery *</label>
                <input type="date" value={formData.estimatedDeliveryDate} onChange={(e) => setFormData((prev) => ({ ...prev, estimatedDeliveryDate: e.target.value }))} className="nf-input" required />
              </div>
              <div>
                <label className="nf-label">Priority</label>
                <select value={formData.priority} onChange={(e) => setFormData((prev) => ({ ...prev, priority: e.target.value }))} className="nf-select">
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                  <option value="URGENT">Urgent</option>
                </select>
              </div>
              <div>
                <label className="nf-label">Weight (kg)</label>
                <input type="number" value={formData.weightKg} onChange={(e) => setFormData((prev) => ({ ...prev, weightKg: e.target.value }))} className="nf-input" placeholder="500" />
              </div>
              <div>
                <label className="nf-label">Value (USD)</label>
                <input type="number" value={formData.valueUsd} onChange={(e) => setFormData((prev) => ({ ...prev, valueUsd: e.target.value }))} className="nf-input" placeholder="25000" />
              </div>
              <div style={{ gridColumn: '1 / -1' }}>
                <label className="nf-label">Notes</label>
                <textarea value={formData.notes} onChange={(e) => setFormData((prev) => ({ ...prev, notes: e.target.value }))} className="nf-input" rows={2} placeholder="Operational notes..." style={{ resize: 'vertical' }} />
              </div>
              <div style={{ gridColumn: '1 / -1' }}>
                <button type="submit" disabled={submitting} className="nf-btn nf-btn-primary">
                  <Plus size={14} />
                  {submitting ? 'Creating...' : 'Create Shipment'}
                </button>
              </div>
            </div>
          </form>
        </div>
      )}

      <div className="nf-card" style={{ padding: '12px 16px' }}>
        <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
          <div style={{ flex: 1, minWidth: 220, position: 'relative' }}>
            <Search size={14} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            <input
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              placeholder="Search tracking number, route, or city..."
              className="nf-input"
              style={{ paddingLeft: 32 }}
            />
          </div>
          <button type="button" onClick={handleSearch} className="nf-btn nf-btn-primary">
            <Search size={14} /> Search
          </button>
          {statusFilter && (
            <button type="button" onClick={() => setStatusFilter('')} className="nf-btn nf-btn-secondary">
              <Filter size={14} /> Clear
            </button>
          )}
        </div>
      </div>

      <div className="nf-grid-detail-layout">
        <div className="nf-card" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-subtle)', display: 'flex', justifyContent: 'space-between', gap: 12 }}>
            <div>
              <div className="nf-section-title">
                <ShipWheel size={16} color="var(--accent-blue)" /> Route Monitor
              </div>
              <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4 }}>
                Showing {filtered.length} of {shipments.length} shipments. Exposure value {formatCurrency(stats.totalValue)}.
              </div>
            </div>
            {statusFilter && <StatusBadge status={statusFilter} />}
          </div>

          <div style={{ overflowX: 'auto' }}>
            <table className="nf-table">
              <thead>
                <tr>
                  <th>Tracking #</th>
                  <th>Route</th>
                  <th>Supplier / Cargo</th>
                  <th>ETA</th>
                  <th>Risk Forecast</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  [...Array(6)].map((_, i) => (
                    <tr key={i}>
                      {[...Array(7)].map((__, j) => (
                        <td key={j}><div className="nf-skeleton" style={{ height: 18, borderRadius: 4, width: j === 0 ? 100 : '80%' }} /></td>
                      ))}
                    </tr>
                  ))
                ) : filtered.length === 0 ? (
                  <tr>
                    <td colSpan={7} style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
                      <Package size={32} style={{ marginBottom: 8, opacity: 0.3 }} />
                      <div>No shipments found</div>
                    </td>
                  </tr>
                ) : (
                  filtered.map((shipment) => {
                    const insight = getShipmentInsight(shipment);
                    const priorityColor = PRIORITY_COLORS[shipment.priority] ?? 'var(--text-muted)';
                    const isSelected = selectedShipment?.id === shipment.id;

                    return (
                      <tr
                        key={shipment.id}
                        onClick={() => setSelectedId(shipment.id)}
                        style={{ cursor: 'pointer', background: isSelected ? 'var(--bg-elevated)' : undefined }}
                      >
                        <td style={{ fontWeight: 700, fontFamily: 'monospace', fontSize: 12.5, color: 'var(--accent-cyan)' }}>
                          {shipment.trackingNumber}
                        </td>
                        <td>
                          <div style={{ color: 'var(--text-secondary)', fontSize: 12.5 }}>
                            {shipment.originCity} {'->'} {shipment.destinationCity}
                          </div>
                          <div style={{ display: 'flex', alignItems: 'center', gap: 5, marginTop: 4, fontSize: 12, color: 'var(--text-muted)' }}>
                            <MapPin size={12} />
                            {shipment.currentLocation || `${shipment.originCountry || 'Origin'} to ${shipment.destinationCountry || 'Destination'}`}
                          </div>
                        </td>
                        <td>
                          <div style={{ color: 'var(--text-primary)', fontWeight: 600 }}>{shipment.supplierName ?? 'Unknown supplier'}</div>
                          <div style={{ color: 'var(--text-muted)', fontSize: 12 }}>
                            {shipment.cargoType || 'General cargo'} | <span style={{ color: priorityColor, fontWeight: 700 }}>{shipment.priority || 'MEDIUM'}</span>
                          </div>
                        </td>
                        <td style={{ color: 'var(--text-muted)', fontSize: 12 }}>{formatDate(shipment.estimatedDeliveryDate)}</td>
                        <td>
                          <div style={{ display: 'flex', flexDirection: 'column', gap: 5, minWidth: 130 }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', gap: 8 }}>
                              <RiskBadge level={insight.riskLevel} />
                              <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>{Math.round(insight.score)}%</span>
                            </div>
                            <div className="nf-progress" style={{ height: 4 }}>
                              <div
                                className="nf-progress-bar"
                                style={{
                                  width: `${clamp(insight.score)}%`,
                                  background: insight.score >= 70 ? '#ef4444' : insight.score >= 45 ? '#f59e0b' : '#10b981',
                                }}
                              />
                            </div>
                          </div>
                        </td>
                        <td><StatusBadge status={shipment.status} /></td>
                        <td>
                          {canManageShipments ? (
                            <div style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
                              <select
                                value={shipment.status}
                                onClick={(event) => event.stopPropagation()}
                                onChange={(event) => handleStatusUpdate(shipment.id, event.target.value)}
                                className="nf-select"
                                style={{ padding: '3px 28px 3px 8px', fontSize: 11.5, width: 120 }}
                              >
                                <option value="PENDING">Pending</option>
                                <option value="IN_TRANSIT">In Transit</option>
                                <option value="DELAYED">Delayed</option>
                                <option value="DELIVERED">Delivered</option>
                                <option value="CANCELLED">Cancelled</option>
                              </select>
                              <button
                                type="button"
                                onClick={(event) => {
                                  event.stopPropagation();
                                  handleDelete(shipment.id);
                                }}
                                className="nf-btn nf-btn-danger nf-btn-sm"
                                aria-label={`Delete shipment ${shipment.trackingNumber}`}
                              >
                                <X size={12} />
                              </button>
                            </div>
                          ) : (
                            <span style={{ fontSize: 11, color: 'var(--text-muted)' }}>
                              Read-only
                            </span>
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
            {selectedShipment && selectedInsight ? (
              <>
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, marginBottom: 16 }}>
                  <div>
                    <h3 style={{ margin: 0, fontSize: '1rem', color: 'var(--text-primary)' }}>{selectedShipment.trackingNumber}</h3>
                    <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4 }}>
                      {selectedShipment.originCountry || 'Origin'} to {selectedShipment.destinationCountry || 'Destination'}
                    </div>
                  </div>
                  <RiskBadge level={selectedInsight.riskLevel} />
                </div>

                <div className="nf-grid-2-responsive" style={{ gap: 10, marginBottom: 16 }}>
                  <div className="nf-ai-recommendation" style={{ marginBottom: 0 }}>
                    <div style={{ fontSize: 11, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Delay Probability</div>
                    <div style={{ fontSize: 24, fontWeight: 800, color: 'var(--text-primary)' }}>{Math.round(selectedInsight.delayProbability)}%</div>
                  </div>
                  <div className="nf-ai-recommendation" style={{ marginBottom: 0 }}>
                    <div style={{ fontSize: 11, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Risk Score</div>
                    <div style={{ fontSize: 24, fontWeight: 800, color: 'var(--text-primary)' }}>{Math.round(selectedInsight.score)}</div>
                  </div>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                  <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                      <span style={{ fontSize: 12, color: 'var(--text-secondary)' }}>Route Complexity</span>
                      <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>{Math.round(clamp(toNumber(selectedShipment.routeComplexityScore)))}%</span>
                    </div>
                    <div className="nf-progress"><div className="nf-progress-bar" style={{ width: `${clamp(toNumber(selectedShipment.routeComplexityScore))}%`, background: '#4f7cff' }} /></div>
                  </div>
                  <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                      <span style={{ fontSize: 12, color: 'var(--text-secondary)' }}>Weather Impact</span>
                      <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>{Math.round(clamp(toNumber(selectedShipment.weatherImpactScore)))}%</span>
                    </div>
                    <div className="nf-progress"><div className="nf-progress-bar" style={{ width: `${clamp(toNumber(selectedShipment.weatherImpactScore))}%`, background: '#f59e0b' }} /></div>
                  </div>
                  <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                      <span style={{ fontSize: 12, color: 'var(--text-secondary)' }}>Inventory Exposure</span>
                      <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>{Math.round(clamp(toNumber(selectedShipment.inventoryRiskScore)))}%</span>
                    </div>
                    <div className="nf-progress"><div className="nf-progress-bar" style={{ width: `${clamp(toNumber(selectedShipment.inventoryRiskScore))}%`, background: '#7c3aed' }} /></div>
                  </div>
                </div>

                <div className="nf-ai-recommendation" style={{ marginTop: 16, marginBottom: 0 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 6 }}>
                    <Zap size={13} color="var(--accent-cyan)" />
                    <span style={{ fontSize: 12, fontWeight: 700, color: 'var(--text-primary)' }}>Forecast Reason</span>
                  </div>
                  <div style={{ fontSize: 12, color: 'var(--text-muted)', lineHeight: 1.5 }}>
                    {selectedInsight.prediction?.predictionReason || selectedInsight.risk?.factors || selectedShipment.notes || 'No model explanation available for this route yet.'}
                  </div>
                </div>
              </>
            ) : (
              <div style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '2rem 0' }}>
                Select a shipment to inspect route risk.
              </div>
            )}
          </div>

          <div className="nf-card">
            <div className="nf-section-title" style={{ marginBottom: 12 }}>
              <ShieldAlert size={16} color="var(--accent-red)" /> Highest Risk
            </div>
            {rankedRiskShipments.length === 0 ? (
              <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No shipment risk signals available.</div>
            ) : (
              rankedRiskShipments.map(({ shipment, insight }) => (
                <button
                  key={shipment.id}
                  type="button"
                  onClick={() => setSelectedId(shipment.id)}
                  className="nf-ai-recommendation"
                  style={{ width: '100%', textAlign: 'left', cursor: 'pointer' }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12 }}>
                    <div>
                      <div style={{ fontWeight: 700, color: 'var(--text-primary)', fontFamily: 'monospace', fontSize: 12 }}>
                        {shipment.trackingNumber}
                      </div>
                      <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                        {shipment.originCity} {'->'} {shipment.destinationCity}
                      </div>
                    </div>
                    <RiskBadge level={insight.riskLevel} />
                  </div>
                </button>
              ))
            )}
          </div>

          <div className="nf-card">
            <div className="nf-section-title" style={{ marginBottom: 12 }}>
              <MapPin size={16} color="var(--accent-cyan)" /> Route Exposure
            </div>
            {routeExposure.length === 0 ? (
              <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No route exposure data available.</div>
            ) : (
              routeExposure.map((route) => (
                <div key={route.route} style={{ marginBottom: 12 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, marginBottom: 6 }}>
                    <span style={{ fontSize: 12, color: 'var(--text-secondary)' }}>{route.route}</span>
                    <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>{Math.round(route.averageRisk)}%</span>
                  </div>
                  <div className="nf-progress">
                    <div
                      className="nf-progress-bar"
                      style={{
                        width: `${clamp(route.averageRisk)}%`,
                        background: route.averageRisk >= 70 ? '#ef4444' : route.averageRisk >= 45 ? '#f59e0b' : '#10b981',
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

export default Shipments;
