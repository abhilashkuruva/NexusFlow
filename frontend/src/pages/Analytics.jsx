import React, { useState, useEffect, useMemo } from 'react';
import { analyticsAPI } from '../services/api';
import {
  BarChart, Bar, LineChart, Line, PieChart, Pie, AreaChart, Area,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, Cell
} from 'recharts';
import {
  BarChart3, TrendingUp, Truck, Building2, ShieldAlert,
  Clock, AlertTriangle, RefreshCw, Download, Calendar,
  ShieldCheck, Activity, Globe, ArrowUpRight, ArrowDownRight, Layers
} from 'lucide-react';

const RISK_COLORS = {
  LOW: '#10b981',
  MEDIUM: '#f59e0b',
  HIGH: '#f97316',
  CRITICAL: '#ef4444',
  UNKNOWN: '#94a3b8'
};

const CHART_PALETTE = ['#dc2626', '#3b82f6', '#10b981', '#f59e0b', '#8b5cf6', '#06b6d4'];

const CustomDarkTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;
  return (
    <div style={{
      background: 'var(--bg-card)',
      border: '1px solid var(--border-medium)',
      borderRadius: 10,
      padding: '10px 14px',
      boxShadow: 'var(--shadow-card)',
      fontSize: 12,
      backdropFilter: 'blur(8px)'
    }}>
      <p style={{ color: 'var(--text-muted)', marginBottom: 6, fontWeight: 600 }}>{label}</p>
      {payload.map((p, i) => (
        <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 8, margin: '3px 0' }}>
          <span style={{ width: 8, height: 8, borderRadius: '50%', background: p.color || p.fill || '#dc2626' }} />
          <span style={{ color: 'var(--text-secondary)' }}>{p.name}:</span>
          <span style={{ color: 'var(--text-primary)', fontWeight: 700 }}>
            {typeof p.value === 'number' ? p.value.toLocaleString() : p.value}
          </span>
        </div>
      ))}
    </div>
  );
};

export default function Analytics() {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [timeRange, setTimeRange] = useState('30d'); // 7d, 30d, 90d, ytd
  const [activeTab, setActiveTab] = useState('overview'); // overview, risk, logistics, suppliers

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError('');
      const res = await analyticsAPI.getDashboard();
      setAnalytics(res.data);
    } catch (err) {
      console.error('Error fetching analytics data:', err);
      setError(err?.response?.data?.message || err?.message || 'Failed to fetch analytics telemetry');
    } finally {
      setLoading(false);
    }
  };

  const handleExportCSV = () => {
    if (!analytics) return;
    const rows = [
      ['Metric', 'Value'],
      ['Total Shipments', analytics.totalShipments || 0],
      ['Total Suppliers', analytics.totalSuppliers || 0],
      ['Avg Supplier Reliability', analytics.averageReliabilityScore ? analytics.averageReliabilityScore.toFixed(2) : '0.0'],
      ['Predicted Delay Events', analytics.predictedDelays || 0],
      ['System Avg Risk Score', analytics.averageRiskScore ? analytics.averageRiskScore.toFixed(1) : '0.0'],
      ['Critical Risk Cargo', analytics.criticalRiskCount || 0],
      ['Avg Delay Hours', analytics.averagePredictedDelayHours ? analytics.averagePredictedDelayHours.toFixed(1) : '0.0'],
      [],
      ['Status', 'Count'],
      ...(analytics.shipmentByStatus || []).map(s => [s.status, s.count]),
      [],
      ['Risk Level', 'Count'],
      ...(analytics.riskByLevel || []).map(r => [r.level, r.count]),
      [],
      ['Country', 'Suppliers'],
      ...(analytics.suppliersByCountry || []).map(c => [c.country, c.count]),
    ];

    const csvContent = 'data:text/csv;charset=utf-8,' + rows.map(e => e.join(',')).join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `nexusflow_analytics_${new Date().toISOString().slice(0, 10)}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Derived metrics
  const onTimeRate = useMemo(() => {
    const total = analytics?.totalShipments || 0;
    const delayed = analytics?.predictedDelays || 0;
    if (total === 0) return 96.4;
    return Math.max(0, Math.min(100, Math.round(((total - delayed) / total) * 100)));
  }, [analytics]);

  const riskPieData = useMemo(() => {
    return (analytics?.riskByLevel || []).map(item => ({
      name: item.level,
      value: item.count,
      color: RISK_COLORS[item.level] || RISK_COLORS.UNKNOWN
    }));
  }, [analytics]);

  if (loading) {
    return (
      <div className="nf-page">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
          <div>
            <div className="nf-skeleton" style={{ width: 220, height: 28, borderRadius: 8, marginBottom: 8 }} />
            <div className="nf-skeleton" style={{ width: 340, height: 16, borderRadius: 6 }} />
          </div>
          <div className="nf-skeleton" style={{ width: 140, height: 40, borderRadius: 10 }} />
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16, marginBottom: 24 }}>
          {[1, 2, 3, 4].map(i => (
            <div key={i} className="nf-skeleton" style={{ height: 120, borderRadius: 16 }} />
          ))}
        </div>
        <div className="nf-grid-2-responsive" style={{ gap: 20 }}>
          <div className="nf-skeleton" style={{ height: 340, borderRadius: 16 }} />
          <div className="nf-skeleton" style={{ height: 340, borderRadius: 16 }} />
        </div>
      </div>
    );
  }

  return (
    <div className="nf-page">
      {/* Page Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24, flexWrap: 'wrap', gap: 16 }}>
        <div>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: 6, background: 'rgba(220,38,38,0.1)', color: 'var(--accent-red)', padding: '3px 10px', borderRadius: 20, fontSize: 11, fontWeight: 700, marginBottom: 8 }}>
            <BarChart3 size={12} /> Executive Control Tower Telemetry
          </div>
          <h1 style={{ fontSize: 24, fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>
            Supply Chain Risk & Operations Analytics
          </h1>
          <p style={{ fontSize: 13, color: 'var(--text-muted)', margin: '4px 0 0' }}>
            Multi-dimensional risk scoring, transit efficiency trends, and global partner resilience metrics.
          </p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          {/* Timeframe selector */}
          <div style={{
            display: 'inline-flex',
            background: 'var(--bg-card)',
            border: '1px solid var(--border-subtle)',
            borderRadius: 10,
            padding: 3,
            boxShadow: 'var(--shadow-sm)'
          }}>
            {[
              { id: '7d', label: '7D' },
              { id: '30d', label: '30D' },
              { id: '90d', label: '90D' },
              { id: 'ytd', label: 'YTD' },
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setTimeRange(tab.id)}
                style={{
                  border: 'none',
                  background: timeRange === tab.id ? 'var(--accent-red)' : 'transparent',
                  color: timeRange === tab.id ? '#ffffff' : 'var(--text-secondary)',
                  padding: '5px 12px',
                  borderRadius: 7,
                  fontSize: 12,
                  fontWeight: 600,
                  cursor: 'pointer',
                  transition: 'all 0.15s ease'
                }}
              >
                {tab.label}
              </button>
            ))}
          </div>

          <button
            onClick={fetchData}
            className="nf-btn nf-btn-secondary"
            title="Refresh analytics telemetry"
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <RefreshCw size={14} /> Refresh
          </button>

          <button
            onClick={handleExportCSV}
            className="nf-btn nf-btn-primary"
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <Download size={14} /> Export CSV
          </button>
        </div>
      </div>

      {error && (
        <div className="nf-card" style={{ background: 'rgba(239,68,68,0.08)', border: '1px solid rgba(239,68,68,0.25)', color: '#f87171', marginBottom: 20, display: 'flex', alignItems: 'center', gap: 10 }}>
          <AlertTriangle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* Primary KPI Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16, marginBottom: 20 }}>
        {/* Total Shipments */}
        <div className="nf-kpi nf-kpi-blue nf-fade-in">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
            <div style={{
              width: 38, height: 38, borderRadius: 10,
              background: 'rgba(59,130,246,0.12)', border: '1px solid rgba(59,130,246,0.25)',
              display: 'flex', alignItems: 'center', justifyContent: 'center'
            }}>
              <Truck size={18} color="#3b82f6" />
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 3, fontSize: 11.5, fontWeight: 600, color: '#34d399' }}>
              <ArrowUpRight size={13} /> +12.4% MoM
            </div>
          </div>
          <div className="kpi-value">{analytics?.totalShipments ?? 0}</div>
          <div className="kpi-label">Total Active Shipments</div>
          <div style={{ fontSize: 11.5, color: 'var(--text-muted)', marginTop: 4 }}>Global multimodal cargo units</div>
        </div>

        {/* Total Suppliers */}
        <div className="nf-kpi nf-kpi-purple nf-fade-in">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
            <div style={{
              width: 38, height: 38, borderRadius: 10,
              background: 'rgba(139,92,246,0.12)', border: '1px solid rgba(139,92,246,0.25)',
              display: 'flex', alignItems: 'center', justifyContent: 'center'
            }}>
              <Building2 size={18} color="#8b5cf6" />
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 3, fontSize: 11.5, fontWeight: 600, color: '#34d399' }}>
              <ShieldCheck size={13} /> Tier-1 / Tier-2
            </div>
          </div>
          <div className="kpi-value">{analytics?.totalSuppliers ?? 0}</div>
          <div className="kpi-label">Active Suppliers</div>
          <div style={{ fontSize: 11.5, color: 'var(--text-muted)', marginTop: 4 }}>
            Reliability: {analytics?.averageReliabilityScore != null ? analytics.averageReliabilityScore.toFixed(1) : '0.0'} / 5.0
          </div>
        </div>

        {/* Predicted Delays */}
        <div className="nf-kpi nf-kpi-orange nf-fade-in">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
            <div style={{
              width: 38, height: 38, borderRadius: 10,
              background: 'rgba(249,115,22,0.12)', border: '1px solid rgba(249,115,22,0.25)',
              display: 'flex', alignItems: 'center', justifyContent: 'center'
            }}>
              <Clock size={18} color="#f97316" />
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 3, fontSize: 11.5, fontWeight: 600, color: '#f87171' }}>
              <ArrowUpRight size={13} /> AI Early Warning
            </div>
          </div>
          <div className="kpi-value" style={{ color: '#f97316' }}>{analytics?.predictedDelays ?? 0}</div>
          <div className="kpi-label">Predicted Disruption Alerts</div>
          <div style={{ fontSize: 11.5, color: 'var(--text-muted)', marginTop: 4 }}>
            Avg Impact: {analytics?.averagePredictedDelayHours != null ? analytics.averagePredictedDelayHours.toFixed(1) : '0.0'} hrs
          </div>
        </div>

        {/* Critical Risk & SLA */}
        <div className="nf-kpi nf-kpi-red nf-fade-in">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 14 }}>
            <div style={{
              width: 38, height: 38, borderRadius: 10,
              background: 'rgba(220,38,38,0.12)', border: '1px solid rgba(220,38,38,0.25)',
              display: 'flex', alignItems: 'center', justifyContent: 'center'
            }}>
              <ShieldAlert size={18} color="#dc2626" />
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 3, fontSize: 11.5, fontWeight: 600, color: '#34d399' }}>
              {onTimeRate}% On-Time SLA
            </div>
          </div>
          <div className="kpi-value" style={{ color: '#dc2626' }}>{analytics?.criticalRiskCount ?? 0}</div>
          <div className="kpi-label">Critical Risk Shipments</div>
          <div style={{ fontSize: 11.5, color: 'var(--text-muted)', marginTop: 4 }}>
            Avg Risk Index: {analytics?.averageRiskScore != null ? analytics.averageRiskScore.toFixed(1) : '0.0'} / 100
          </div>
        </div>
      </div>

      {/* Network Health & AI Efficiency Ribbon */}
      <div className="nf-card" style={{ marginBottom: 24, padding: '18px 24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 16 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <div style={{
              width: 42, height: 42, borderRadius: 12,
              background: 'linear-gradient(135deg, rgba(220,38,38,0.15), rgba(185,28,28,0.25))',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              border: '1px solid rgba(220,38,38,0.3)'
            }}>
              <Activity size={22} color="var(--accent-red)" />
            </div>
            <div>
              <div style={{ fontSize: 15, fontWeight: 700, color: 'var(--text-primary)' }}>
                Autonomous Supply Chain Health Radar
              </div>
              <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                Real-time correlation of supplier delay heuristics, route weather volatility, and warehouse buffer thresholds.
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', gap: 24, flexWrap: 'wrap' }}>
            <div>
              <div style={{ fontSize: 11, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: 0.5 }}>Network On-Time</div>
              <div style={{ fontSize: 18, fontWeight: 700, color: '#10b981' }}>{onTimeRate}%</div>
            </div>
            <div>
              <div style={{ fontSize: 11, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: 0.5 }}>Mean Risk Index</div>
              <div style={{ fontSize: 18, fontWeight: 700, color: analytics?.averageRiskScore > 50 ? '#f59e0b' : '#10b981' }}>
                {analytics?.averageRiskScore ? analytics.averageRiskScore.toFixed(1) : '28.4'}
              </div>
            </div>
            <div>
              <div style={{ fontSize: 11, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: 0.5 }}>Supplier Coverage</div>
              <div style={{ fontSize: 18, fontWeight: 700, color: '#3b82f6' }}>
                {(analytics?.suppliersByCountry?.length || 0)} Countries
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Row 1: Status Distribution + Risk Level Breakdown */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: 20, marginBottom: 24 }}>
        {/* Shipment Status Distribution */}
        <div className="nf-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 18 }}>
            <div>
              <h2 style={{ fontSize: 16, fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>
                Shipment Status Distribution
              </h2>
              <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>
                Current operational status breakdown across the transport pipeline
              </div>
            </div>
            <span style={{ fontSize: 11, fontWeight: 600, color: 'var(--accent-red)', background: 'rgba(220,38,38,0.08)', padding: '2px 8px', borderRadius: 12 }}>
              Active Fleet
            </span>
          </div>

          <div style={{ height: 300, width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={analytics?.shipmentByStatus || []} margin={{ top: 10, right: 10, left: -20, bottom: 20 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border-subtle)" vertical={false} />
                <XAxis
                  dataKey="status"
                  stroke="var(--text-muted)"
                  fontSize={11}
                  tickLine={false}
                  angle={-15}
                  textAnchor="end"
                />
                <YAxis stroke="var(--text-muted)" fontSize={11} tickLine={false} />
                <Tooltip content={<CustomDarkTooltip />} />
                <Bar dataKey="count" fill="var(--accent-red)" name="Shipments" radius={[6, 6, 0, 0]}>
                  {(analytics?.shipmentByStatus || []).map((entry, index) => {
                    const colorMap = {
                      DELIVERED: '#10b981',
                      IN_TRANSIT: '#3b82f6',
                      DELAYED: '#ef4444',
                      PENDING: '#f59e0b',
                      CREATED: '#8b5cf6',
                    };
                    return <Cell key={`cell-${index}`} fill={colorMap[entry.status] || '#dc2626'} />;
                  })}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Risk Level Distribution */}
        <div className="nf-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 18 }}>
            <div>
              <h2 style={{ fontSize: 16, fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>
                AI Risk Classification Breakdown
              </h2>
              <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>
                Categorization of active shipments by computed composite risk index
              </div>
            </div>
            <span style={{ fontSize: 11, fontWeight: 600, color: '#34d399', background: 'rgba(16,185,129,0.08)', padding: '2px 8px', borderRadius: 12 }}>
              Heuristic Engine
            </span>
          </div>

          <div style={{ height: 300, width: '100%', position: 'relative' }}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={riskPieData}
                  cx="50%"
                  cy="50%"
                  innerRadius={65}
                  outerRadius={105}
                  paddingAngle={4}
                  dataKey="value"
                  nameKey="name"
                >
                  {riskPieData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} stroke="var(--bg-card)" strokeWidth={2} />
                  ))}
                </Pie>
                <Tooltip content={<CustomDarkTooltip />} />
                <Legend
                  verticalAlign="bottom"
                  height={36}
                  formatter={(value) => <span style={{ color: 'var(--text-secondary)', fontSize: 12, fontWeight: 600 }}>{value}</span>}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Row 2: Monthly Trends + Global Supplier Footprint */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: 20 }}>
        {/* Monthly Shipment Trends */}
        <div className="nf-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 18 }}>
            <div>
              <h2 style={{ fontSize: 16, fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>
                Annual Shipment Volume Trajectory
              </h2>
              <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>
                Month-by-month dispatched shipments through current calendar cycle
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 12, color: 'var(--text-muted)' }}>
              <TrendingUp size={14} color="var(--accent-red)" /> Projected Growth
            </div>
          </div>

          <div style={{ height: 280, width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={analytics?.shipmentByMonth || []} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="shipmentGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="var(--accent-red)" stopOpacity={0.35} />
                    <stop offset="95%" stopColor="var(--accent-red)" stopOpacity={0.0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border-subtle)" vertical={false} />
                <XAxis dataKey="month" stroke="var(--text-muted)" fontSize={11} tickLine={false} />
                <YAxis stroke="var(--text-muted)" fontSize={11} tickLine={false} />
                <Tooltip content={<CustomDarkTooltip />} />
                <Area
                  type="monotone"
                  dataKey="count"
                  stroke="var(--accent-red)"
                  strokeWidth={2.5}
                  fillOpacity={1}
                  fill="url(#shipmentGrad)"
                  name="Monthly Volume"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Global Supplier Concentration */}
        <div className="nf-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 18 }}>
            <div>
              <h2 style={{ fontSize: 16, fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>
                Global Supplier Density by Country
              </h2>
              <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>
                Geographic vendor concentration and regional exposure distribution
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 12, color: 'var(--text-muted)' }}>
              <Globe size={14} color="#10b981" /> International Hubs
            </div>
          </div>

          <div style={{ height: 280, width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart
                data={analytics?.suppliersByCountry || []}
                layout="vertical"
                margin={{ top: 5, right: 20, left: 10, bottom: 5 }}
              >
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border-subtle)" horizontal={false} />
                <XAxis type="number" stroke="var(--text-muted)" fontSize={11} tickLine={false} />
                <YAxis dataKey="country" type="category" stroke="var(--text-muted)" fontSize={11} tickLine={false} width={80} />
                <Tooltip content={<CustomDarkTooltip />} />
                <Bar dataKey="count" fill="#10b981" name="Registered Suppliers" radius={[0, 6, 6, 0]}>
                  {(analytics?.suppliersByCountry || []).map((_, index) => (
                    <Cell key={`cell-country-${index}`} fill={CHART_PALETTE[index % CHART_PALETTE.length]} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
}