import React, { useState, useEffect, useMemo } from 'react';
import {
  Truck, Package, CheckCircle2, Clock, AlertTriangle,
  Search, RefreshCw, Eye, ShieldCheck, MapPin, ArrowUpRight,
  TrendingUp, Calendar, FileText, LogOut
} from 'lucide-react';
import { shipmentAPI } from '../services/api.js';
import { useAuth } from '../hooks/useAuth.js';

export default function SupplierPortal() {
  const { user } = useAuth();
  const [shipments, setShipments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.clear();
    sessionStorage.clear();
    window.location.href = '/login';
  };
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [selectedShipment, setSelectedShipment] = useState(null);

  const fetchShipments = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await shipmentAPI.getAll();
      const raw = res.data?.content ?? res.data;
      const all = Array.isArray(raw) ? raw : [];
      setShipments(all);
    } catch (err) {
      console.error('Failed to load supplier shipments:', err);
      setError('Unable to load shipments at this time.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchShipments();
  }, []);

  const filteredShipments = useMemo(() => {
    return shipments.filter((s) => {
      const matchSearch =
        (s.trackingNumber || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
        (s.origin || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
        (s.destination || '').toLowerCase().includes(searchQuery.toLowerCase());
      const matchStatus = statusFilter === 'ALL' || s.status === statusFilter;
      return matchSearch && matchStatus;
    });
  }, [shipments, searchQuery, statusFilter]);

  const kpis = useMemo(() => {
    const total = shipments.length;
    const inTransit = shipments.filter((s) => s.status === 'IN_TRANSIT').length;
    const delivered = shipments.filter((s) => s.status === 'DELIVERED').length;
    const delayed = shipments.filter((s) => s.status === 'DELAYED' || (s.predictedDelayHours && s.predictedDelayHours > 0)).length;
    const onTimeRate = total > 0 ? Math.round(((total - delayed) / total) * 100) : 98;

    return { total, inTransit, delivered, delayed, onTimeRate };
  }, [shipments]);

  const getStatusBadge = (status) => {
    const colors = {
      DELIVERED: { bg: 'rgba(16,185,129,0.15)', color: '#34d399', label: 'Delivered' },
      IN_TRANSIT: { bg: 'rgba(79,124,255,0.15)', color: '#818cf8', label: 'In Transit' },
      DELAYED: { bg: 'rgba(239,68,68,0.15)', color: '#f87171', label: 'Delayed' },
      PENDING: { bg: 'rgba(245,158,11,0.15)', color: '#fbbf24', label: 'Pending' },
    };
    const s = colors[status] || { bg: 'rgba(148,163,184,0.15)', color: '#94a3b8', label: status || 'Unknown' };
    return (
      <span style={{
        display: 'inline-flex', alignItems: 'center', gap: 6,
        background: s.bg, color: s.color,
        border: `1px solid ${s.color}30`,
        borderRadius: 20, padding: '2px 10px',
        fontSize: 11, fontWeight: 600
      }}>
        <span style={{ width: 6, height: 6, borderRadius: '50%', background: s.color }} />
        {s.label}
      </span>
    );
  };

  return (
    <div style={{ padding: '24px 32px', maxWidth: 1400, margin: '0 auto' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 24, flexWrap: 'wrap', gap: 16 }}>
        <div>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: 6, background: 'rgba(245,158,11,0.15)', color: '#fbbf24', padding: '3px 10px', borderRadius: 20, fontSize: 11, fontWeight: 700, marginBottom: 8 }}>
            <Truck size={12} /> Supplier Portal
          </div>
          <h1 style={{ fontSize: 24, fontWeight: 700, color: 'var(--text-primary)' }}>
            Welcome back, {user?.firstName || 'Partner'}
          </h1>
          <p style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 4 }}>
            Monitor your purchase orders, dispatch tracking, and SLA fulfillment metrics.
          </p>
        </div>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
          <button
            onClick={fetchShipments}
            disabled={loading}
            className="nf-btn nf-btn-secondary"
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
            Sync Orders
          </button>
          <button
            onClick={handleLogout}
            className="nf-btn nf-btn-danger"
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
            id="supplier-portal-logout-btn"
          >
            <LogOut size={14} />
            Sign Out
          </button>
        </div>
      </div>

      {error && (
        <div style={{
          padding: '12px 16px', borderRadius: 8, marginBottom: 20,
          background: 'rgba(239,68,68,0.12)', border: '1px solid rgba(239,68,68,0.3)',
          color: '#f87171', fontSize: 13
        }}>
          {error}
        </div>
      )}

      {/* KPI Cards */}
      <div style={{
        display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
        gap: 16, marginBottom: 24
      }}>
        <div className="nf-card" style={{ padding: '16px 20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>Active In-Transit</span>
            <Truck size={16} color="#818cf8" />
          </div>
          <div style={{ fontSize: 28, fontWeight: 700, color: 'var(--text-primary)', marginTop: 8 }}>{kpis.inTransit}</div>
          <div style={{ fontSize: 11, color: '#818cf8', marginTop: 4 }}>Dispatches on road / air</div>
        </div>

        <div className="nf-card" style={{ padding: '16px 20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>Delivered Orders</span>
            <CheckCircle2 size={16} color="#34d399" />
          </div>
          <div style={{ fontSize: 28, fontWeight: 700, color: '#34d399', marginTop: 8 }}>{kpis.delivered}</div>
          <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 4 }}>Completed successfully</div>
        </div>

        <div className="nf-card" style={{ padding: '16px 20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>On-Time SLA Rate</span>
            <TrendingUp size={16} color="#34d399" />
          </div>
          <div style={{ fontSize: 28, fontWeight: 700, color: '#34d399', marginTop: 8 }}>{kpis.onTimeRate}%</div>
          <div style={{ fontSize: 11, color: '#34d399', marginTop: 4 }}>Target: 95% threshold</div>
        </div>

        <div className="nf-card" style={{ padding: '16px 20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>Quality & Compliance</span>
            <ShieldCheck size={16} color="#fbbf24" />
          </div>
          <div style={{ fontSize: 28, fontWeight: 700, color: '#fbbf24', marginTop: 8 }}>Tier 1</div>
          <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 4 }}>Verified partner status</div>
        </div>
      </div>

      {/* Filter toolbar */}
      <div className="nf-card" style={{ padding: 16, marginBottom: 20, display: 'flex', gap: 16, alignItems: 'center', flexWrap: 'wrap' }}>
        <div style={{ position: 'relative', flex: 1, minWidth: 240 }}>
          <Search size={16} style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
          <input
            type="text"
            className="nf-input"
            style={{ paddingLeft: 36, width: '100%' }}
            placeholder="Search tracking #, origin, or destination..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>Status:</span>
          <select
            className="nf-select"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            style={{ padding: '8px 12px', fontSize: 13 }}
          >
            <option value="ALL">All Statuses</option>
            <option value="IN_TRANSIT">In Transit</option>
            <option value="DELIVERED">Delivered</option>
            <option value="DELAYED">Delayed</option>
            <option value="PENDING">Pending</option>
          </select>
        </div>
      </div>

      {/* Shipments Table */}
      <div className="nf-card" style={{ overflow: 'hidden' }}>
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: 13 }}>
            <thead>
              <tr style={{ background: 'var(--bg-elevated)', borderBottom: '1px solid var(--border-subtle)', color: 'var(--text-muted)' }}>
                <th style={{ padding: '12px 16px', fontWeight: 600 }}>Tracking ID</th>
                <th style={{ padding: '12px 16px', fontWeight: 600 }}>Route</th>
                <th style={{ padding: '12px 16px', fontWeight: 600 }}>Mode</th>
                <th style={{ padding: '12px 16px', fontWeight: 600 }}>Status</th>
                <th style={{ padding: '12px 16px', fontWeight: 600 }}>Est. Delivery</th>
                <th style={{ padding: '12px 16px', fontWeight: 600, textAlign: 'right' }}>Details</th>
              </tr>
            </thead>
            <tbody>
              {loading && shipments.length === 0 ? (
                <tr>
                  <td colSpan={6} style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
                    <RefreshCw size={24} className="animate-spin" style={{ margin: '0 auto 10px' }} />
                    Loading assigned orders...
                  </td>
                </tr>
              ) : filteredShipments.length === 0 ? (
                <tr>
                  <td colSpan={6} style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
                    No shipments found matching filters
                  </td>
                </tr>
              ) : (
                filteredShipments.map((s) => (
                  <tr
                    key={s.id}
                    style={{ borderBottom: '1px solid var(--border-subtle)', cursor: 'pointer' }}
                    className="table-row-hover"
                    onClick={() => setSelectedShipment(s)}
                  >
                    <td style={{ padding: '14px 16px' }}>
                      <div style={{ fontWeight: 600, color: '#818cf8', fontFamily: 'monospace' }}>
                        {s.trackingNumber || `TRK-${s.id}`}
                      </div>
                    </td>
                    <td style={{ padding: '14px 16px' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 6, color: 'var(--text-primary)' }}>
                        <MapPin size={13} color="var(--text-muted)" />
                        <span>{s.origin || 'Depot'}</span>
                        <span style={{ color: 'var(--text-muted)' }}>→</span>
                        <span>{s.destination || 'Destination'}</span>
                      </div>
                    </td>
                    <td style={{ padding: '14px 16px', color: 'var(--text-secondary)' }}>
                      {s.transportMode || 'GROUND'}
                    </td>
                    <td style={{ padding: '14px 16px' }}>
                      {getStatusBadge(s.status)}
                    </td>
                    <td style={{ padding: '14px 16px', color: 'var(--text-muted)' }}>
                      {s.estimatedDelivery ? new Date(s.estimatedDelivery).toLocaleDateString() : 'Pending'}
                    </td>
                    <td style={{ padding: '14px 16px', textAlign: 'right' }}>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          setSelectedShipment(s);
                        }}
                        className="nf-btn nf-btn-secondary"
                        style={{ padding: '6px 12px', fontSize: 12 }}
                      >
                        <Eye size={12} /> View
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Detail Modal */}
      {selectedShipment && (
        <div style={{
          position: 'fixed', inset: 0, zIndex: 60,
          background: 'rgba(0,0,0,0.6)', backdropFilter: 'blur(4px)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 16
        }}>
          <div className="nf-card" style={{ maxWidth: 540, width: '100%', padding: 24, boxShadow: '0 20px 40px rgba(0,0,0,0.5)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 20 }}>
              <div>
                <span style={{ fontSize: 11, color: '#818cf8', fontWeight: 600, fontFamily: 'monospace' }}>
                  {selectedShipment.trackingNumber || `TRK-${selectedShipment.id}`}
                </span>
                <h2 style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)', marginTop: 2 }}>
                  Shipment Order Overview
                </h2>
              </div>
              <div>{getStatusBadge(selectedShipment.status)}</div>
            </div>

            <div className="nf-grid-2-responsive" style={{ gap: 16, marginBottom: 20 }}>
              <div style={{ background: 'var(--bg-elevated)', padding: 12, borderRadius: 8 }}>
                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>Origin</div>
                <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--text-primary)', marginTop: 2 }}>
                  {selectedShipment.origin || 'N/A'}
                </div>
              </div>
              <div style={{ background: 'var(--bg-elevated)', padding: 12, borderRadius: 8 }}>
                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>Destination</div>
                <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--text-primary)', marginTop: 2 }}>
                  {selectedShipment.destination || 'N/A'}
                </div>
              </div>
              <div style={{ background: 'var(--bg-elevated)', padding: 12, borderRadius: 8 }}>
                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>Transport Mode</div>
                <div style={{ fontSize: 14, fontWeight: 600, color: 'var(--text-primary)', marginTop: 2 }}>
                  {selectedShipment.transportMode || 'GROUND'}
                </div>
              </div>
              <div style={{ background: 'var(--bg-elevated)', padding: 12, borderRadius: 8 }}>
                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>Risk Level</div>
                <div style={{ fontSize: 14, fontWeight: 600, color: selectedShipment.riskScore > 60 ? '#f87171' : '#34d399', marginTop: 2 }}>
                  {selectedShipment.riskLevel || (selectedShipment.riskScore > 60 ? 'HIGH' : 'LOW')} ({selectedShipment.riskScore || 15}/100)
                </div>
              </div>
            </div>

            <div style={{ background: 'var(--bg-elevated)', padding: 14, borderRadius: 8, marginBottom: 20 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, color: 'var(--text-muted)', fontSize: 12, marginBottom: 6 }}>
                <Calendar size={14} /> Scheduled Timeline
              </div>
              <div style={{ fontSize: 13, color: 'var(--text-primary)' }}>
                Estimated Delivery: <strong>{selectedShipment.estimatedDelivery ? new Date(selectedShipment.estimatedDelivery).toLocaleString() : 'In Progress'}</strong>
              </div>
              {selectedShipment.actualDelivery && (
                <div style={{ fontSize: 13, color: '#34d399', marginTop: 4 }}>
                  Delivered on: <strong>{new Date(selectedShipment.actualDelivery).toLocaleString()}</strong>
                </div>
              )}
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
              <button
                type="button"
                className="nf-btn nf-btn-primary"
                onClick={() => setSelectedShipment(null)}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
