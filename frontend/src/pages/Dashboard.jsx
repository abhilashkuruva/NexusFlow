import React, { useEffect, useMemo, useState } from 'react';
import {
  BarChart, Bar, LineChart, Line, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer
} from 'recharts';
import {
  Activity, AlertTriangle, ArrowDownRight, ArrowUpRight, BarChart3, ChevronRight,
  Clock, Globe, Package, ShieldAlert, TrendingUp, Truck, Zap, Users, Building2
} from 'lucide-react';
import { Link } from 'react-router-dom';
import {
  analyticsAPI, inventoryAPI, intelligenceAPI, notificationAPI, riskAPI, shipmentAPI
} from '../services/api';
import { useAuth } from '../hooks/useAuth.js';

const RISK_COLORS = { LOW: '#10b981', MEDIUM: '#f59e0b', HIGH: '#f97316', CRITICAL: '#ef4444' };
const CHART_COLORS = ['#4f7cff', '#10b981', '#f59e0b', '#ef4444', '#7c3aed'];

const DarkTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;
  return (
    <div style={{
      background: 'var(--bg-elevated)',
      border: '1px solid var(--border-medium)',
      borderRadius: 8,
      padding: '8px 12px',
      fontSize: 12
    }}>
      <p style={{ color: 'var(--text-muted)', marginBottom: 4 }}>{label}</p>
      {payload.map((p, i) => (
        <p key={i} style={{ color: p.color || 'var(--text-primary)', fontWeight: 600, margin: 0 }}>
          {p.name}: {p.value}
        </p>
      ))}
    </div>
  );
};

const KPICard = ({ label, value, icon: Icon, color, change, changeDir, subtitle }) => (
  <div className={`nf-kpi nf-kpi-${color} nf-fade-in`}>
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
      <div style={{
        width: 40,
        height: 40,
        borderRadius: 10,
        background: `var(--accent-${color === 'blue' ? 'blue' : color === 'green' ? 'green' : color === 'red' ? 'red' : color === 'orange' ? 'orange' : 'purple'})18`,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        border: `1px solid var(--accent-${color === 'blue' ? 'blue' : color === 'green' ? 'green' : color === 'red' ? 'red' : color === 'orange' ? 'orange' : 'purple'})30`
      }}>
        <Icon size={18} color={`var(--accent-${color === 'blue' ? 'blue' : color === 'green' ? 'green' : color === 'red' ? 'red' : color === 'orange' ? 'orange' : 'purple'})`} />
      </div>
      {change && (
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: 3,
          fontSize: 11.5,
          fontWeight: 600,
          color: changeDir === 'up' ? '#34d399' : '#f87171'
        }}>
          {changeDir === 'up' ? <ArrowUpRight size={12} /> : <ArrowDownRight size={12} />}
          {change}
        </div>
      )}
    </div>
    <div className="kpi-value">{value}</div>
    <div className="kpi-label">{label}</div>
    {subtitle && <div style={{ fontSize: 11.5, color: 'var(--text-muted)', marginTop: 4 }}>{subtitle}</div>}
  </div>
);

const HealthMeter = ({ label, score, color }) => (
  <div style={{ marginBottom: 12 }}>
    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
      <span style={{ fontSize: 13, color: 'var(--text-secondary)' }}>{label}</span>
      <span style={{ fontSize: 13, fontWeight: 700, color }}>{score}%</span>
    </div>
    <div className="nf-progress">
      <div className="nf-progress-bar" style={{ width: `${score}%`, background: `linear-gradient(90deg, ${color}, ${color}aa)` }} />
    </div>
  </div>
);

const StatusBadge = ({ status }) => {
  const normalized = status?.toLowerCase()?.replace(/\s/g, '_') || 'pending';
  const map = {
    active: 'medium',
    stale: 'high',
    implemented: 'low',
    dismissed: 'blue',
    pending: 'pending',
    in_transit: 'in_transit',
    delayed: 'delayed',
    delivered: 'delivered',
    cancelled: 'cancelled',
    created: 'created',
  };
  const cls = map[normalized] || normalized;
  return <span className={`nf-badge nf-badge-${cls}`}>{status || 'PENDING'}</span>;
};

const RiskBadge = ({ level }) => {
  const map = {
    CRITICAL: 'critical',
    HIGH: 'high',
    MEDIUM: 'medium',
    LOW: 'low',
    critical: 'critical',
    high: 'high',
    medium: 'medium',
    low: 'low',
  };
  const cls = map[level] || 'low';
  return <span className={`nf-badge nf-badge-${cls}`}>{level || 'LOW'}</span>;
};

const formatDate = (value) => {
  if (!value) return 'Unknown';
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? 'Unknown' : date.toLocaleString();
};

const toPercent = (value) => {
  if (value == null || value === '') return 0;
  const num = Number(value);
  if (!Number.isFinite(num)) return 0;
  return num <= 1 ? num * 100 : num;
};

const toFivePointPercent = (value) => {
  if (value == null || value === '') return 0;
  const num = Number(value);
  if (!Number.isFinite(num)) return 0;
  return num <= 5 ? num * 20 : num;
};

const clamp = (value) => Math.max(0, Math.min(100, value));

export default function Dashboard() {
  const { user, role } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [shipments, setShipments] = useState([]);
  const [predictedDelays, setPredictedDelays] = useState([]);
  const [criticalRisk, setCriticalRisk] = useState([]);
  const [riskStats, setRiskStats] = useState(null);
  const [recentEvents, setRecentEvents] = useState([]);
  const [activeRecommendations, setActiveRecommendations] = useState([]);
  const [topPredictions, setTopPredictions] = useState([]);
  const [recentNotifications, setRecentNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [inventoryStats, setInventoryStats] = useState(null);
  const [lowStockItems, setLowStockItems] = useState([]);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      try {
        setLoading(true);
        setError(null);

        const userId = (() => {
          try {
            return JSON.parse(localStorage.getItem('user'))?.id ?? 1;
          } catch {
            return 1;
          }
        })();

        const results = await Promise.allSettled([
          analyticsAPI.getDashboard(),
          shipmentAPI.getAll(),
          riskAPI.getPredictedDelays(),
          riskAPI.getCriticalRisk(),
          riskAPI.getStats(),
          intelligenceAPI.recentEvents(5),
          intelligenceAPI.activeRecommendations(5),
          intelligenceAPI.topPredictions(6),
          notificationAPI.getRecent(userId, 5),
          notificationAPI.getUnreadCount(userId),
          inventoryAPI.getStats(),
          inventoryAPI.getLowStock(),
        ]);

        if (cancelled) return;

        const [
          dashRes,
          shipmentsRes,
          delaysRes,
          criticalRes,
          riskRes,
          eventsRes,
          recsRes,
          predsRes,
          notifRes,
          unreadRes,
          inventoryStatsRes,
          lowStockRes,
        ] = results;

        if (dashRes.status === 'fulfilled') setAnalytics(dashRes.value?.data ?? null);
        if (shipmentsRes.status === 'fulfilled') {
          const list = Array.isArray(shipmentsRes.value?.data) ? shipmentsRes.value.data : [];
          setShipments(list.sort((a, b) => new Date(b?.createdAt ?? 0) - new Date(a?.createdAt ?? 0)).slice(0, 10));
        }
        if (delaysRes.status === 'fulfilled') setPredictedDelays((Array.isArray(delaysRes.value?.data) ? delaysRes.value.data : []).slice(0, 6));
        if (criticalRes.status === 'fulfilled') setCriticalRisk(Array.isArray(criticalRes.value?.data) ? criticalRes.value.data : []);
        if (riskRes.status === 'fulfilled') setRiskStats(riskRes.value?.data ?? null);
        if (eventsRes.status === 'fulfilled') setRecentEvents(Array.isArray(eventsRes.value?.data) ? eventsRes.value.data : []);
        if (recsRes.status === 'fulfilled') setActiveRecommendations(Array.isArray(recsRes.value?.data) ? recsRes.value.data : []);
        if (predsRes.status === 'fulfilled') setTopPredictions(Array.isArray(predsRes.value?.data) ? predsRes.value.data : []);
        if (notifRes.status === 'fulfilled') setRecentNotifications(Array.isArray(notifRes.value?.data) ? notifRes.value.data : []);
        if (unreadRes.status === 'fulfilled') {
          const raw = unreadRes.value?.data;
          setUnreadCount(
            raw && typeof raw === 'object' && 'unreadCount' in raw
              ? Number(raw.unreadCount ?? 0)
              : Number(raw ?? 0)
          );
        }
        if (inventoryStatsRes.status === 'fulfilled') setInventoryStats(inventoryStatsRes.value?.data ?? null);
        if (lowStockRes.status === 'fulfilled') setLowStockItems(Array.isArray(lowStockRes.value?.data) ? lowStockRes.value.data : []);
      } catch (e) {
        if (!cancelled) setError(e?.message ?? 'Failed to load dashboard');
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, []);

  const statusCounts = useMemo(() => {
    const counts = analytics?.shipmentByStatus ?? [];
    return counts.reduce((acc, item) => {
      acc[item.status] = Number(item.count ?? 0);
      return acc;
    }, {});
  }, [analytics]);

  const kpis = useMemo(() => {
    const totalShipments = Number(analytics?.totalShipments ?? shipments.length ?? 0);
    const delayedShipments = Number(analytics?.delayedShipments ?? predictedDelays.length ?? 0);
    const totalSuppliers = Number(analytics?.totalSuppliers ?? 0);
    const avgRisk = Number(riskStats?.averageRiskScore ?? analytics?.averageRiskScore ?? 0);
    const critical = Number(riskStats?.criticalRisk ?? analytics?.criticalRiskCount ?? criticalRisk.length ?? 0);
    const lowStock = Number(inventoryStats?.lowStockItems ?? lowStockItems.length ?? 0);

    return {
      totalShipments,
      delayedShipments,
      totalSuppliers,
      avgRisk,
      critical,
      lowStock,
    };
  }, [analytics, criticalRisk.length, inventoryStats, lowStockItems.length, predictedDelays.length, riskStats, shipments.length]);

  const shipmentTrendData = analytics?.shipmentByMonth ?? [
    { month: 'Jan', count: 85 },
    { month: 'Feb', count: 92 },
    { month: 'Mar', count: 110 },
    { month: 'Apr', count: 98 },
    { month: 'May', count: 125 },
    { month: 'Jun', count: 118 },
  ];

  const riskDistData = analytics?.riskByLevel ?? [
    { level: 'LOW', count: 45 },
    { level: 'MEDIUM', count: 28 },
    { level: 'HIGH', count: 18 },
    { level: 'CRITICAL', count: 9 },
  ];

  const supplierCountryData = (analytics?.suppliersByCountry ?? []).slice(0, 7);

  const totalShipmentCount = Math.max(1, kpis.totalShipments);
  const delayedRate = clamp((kpis.delayedShipments / totalShipmentCount) * 100);
  const deliveredRate = clamp(((Number(analytics?.deliveredShipments ?? 0)) / totalShipmentCount) * 100);
  const lowStockRate = clamp(
    (Number(inventoryStats?.lowStockItems ?? 0) / Math.max(1, Number(inventoryStats?.totalItems ?? 0))) * 100
  );
  const supplierHealth = clamp(toFivePointPercent(analytics?.averageReliabilityScore || 0));
  const logisticsHealth = clamp(100 - delayedRate);
  const inventoryHealth = clamp(100 - lowStockRate);
  const riskContainment = clamp(
    100 - ((kpis.critical + Number(riskStats?.highRisk ?? analytics?.highRiskShipments ?? 0)) / totalShipmentCount) * 100
  );
  const overallHealth = Math.round((supplierHealth + logisticsHealth + inventoryHealth + riskContainment) / 4);

  const operatingPulse = [
    { label: 'Shipment on-time rate', value: `${Math.round(deliveredRate)}%`, tone: deliveredRate >= 90 ? 'green' : 'orange' },
    { label: 'Delay exposure', value: `${Math.round(delayedRate)}%`, tone: delayedRate >= 20 ? 'red' : 'orange' },
    { label: 'Inventory coverage', value: `${Math.round(inventoryHealth)}%`, tone: inventoryHealth >= 80 ? 'green' : 'orange' },
    { label: 'Unread alerts', value: unreadCount.toString(), tone: unreadCount > 0 ? 'red' : 'green' },
  ];

  const topDelayPredictions = [...predictedDelays]
    .sort((a, b) => toPercent(b?.delayProbability) - toPercent(a?.delayProbability))
    .slice(0, 5);

  const topActionItems = activeRecommendations.slice(0, 4);
  const topAlerts = [...recentEvents, ...recentNotifications]
    .slice(0, 5);

  if (loading) {
    return (
      <div className="nf-page">
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 16, marginBottom: 24 }}>
          {[...Array(5)].map((_, i) => (
            <div key={i} className="nf-kpi nf-skeleton" style={{ height: 130 }} />
          ))}
        </div>
        <div className="nf-grid-2-responsive">
          {[...Array(4)].map((_, i) => (
            <div key={i} className="nf-card nf-skeleton" style={{ height: 280 }} />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      <div className="nf-fade-in" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 12 }}>
        <div>
          <div style={{ fontSize: 11, fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: 1 }}>
            Live Intelligence
          </div>
          <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 800 }}>
            Global Supply Chain <span className="nf-gradient-text">Control Tower</span>
          </h2>
          <div style={{ marginTop: 6, color: 'var(--text-muted)', fontSize: 13 }}>
            {kpis.critical > 0
              ? `${kpis.critical} critical shipments need attention`
              : 'No critical shipments currently flagged'}
          </div>
        </div>
        <div style={{ display: 'flex', gap: 8, alignItems: 'center', flexWrap: 'wrap' }}>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: 6,
            background: 'rgba(16,185,129,0.08)',
            border: '1px solid rgba(16,185,129,0.2)',
            borderRadius: 8,
            padding: '6px 12px'
          }}>
            <div className="risk-dot risk-dot-low animate-pulse-dot" />
            <span style={{ fontSize: 12, color: '#34d399', fontWeight: 600 }}>Live Monitoring</span>
          </div>
          <div style={{
            background: 'rgba(239,68,68,0.1)',
            border: '1px solid rgba(239,68,68,0.2)',
            borderRadius: 8,
            padding: '6px 12px',
            fontSize: 12,
            fontWeight: 600,
            color: '#f87171'
          }}>
            {kpis.critical} Critical Risks
          </div>
        </div>
      </div>

      {/* Role Perspective Banner */}
      <div className="nf-card nf-fade-in" style={{
        padding: '16px 20px',
        background: role === 'ADMIN'
          ? 'linear-gradient(135deg, rgba(239,68,68,0.1), rgba(79,124,255,0.06))'
          : role === 'SUPPLY_MANAGER'
          ? 'linear-gradient(135deg, rgba(79,124,255,0.12), rgba(0,212,255,0.06))'
          : role === 'LOGISTICS_MANAGER'
          ? 'linear-gradient(135deg, rgba(16,185,129,0.12), rgba(79,124,255,0.06))'
          : 'linear-gradient(135deg, rgba(124,58,237,0.12), rgba(79,124,255,0.06))',
        border: '1px solid var(--border-subtle)',
        display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 14
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
          <div style={{
            width: 42, height: 42, borderRadius: 10,
            background: 'var(--bg-elevated)', border: '1px solid var(--border-subtle)',
            display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0
          }}>
            {role === 'ADMIN' && <ShieldAlert size={20} color="#f87171" />}
            {role === 'SUPPLY_MANAGER' && <Building2 size={20} color="#818cf8" />}
            {role === 'LOGISTICS_MANAGER' && <Truck size={20} color="#34d399" />}
            {role === 'ANALYST' && <BarChart3 size={20} color="#a78bfa" />}
            {role === 'SUPPLIER' && <Package size={20} color="#fbbf24" />}
          </div>
          <div>
            <div style={{ fontSize: 11, fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: 0.8 }}>
              {role === 'ADMIN' && 'Enterprise Administration & Governance Tower'}
              {role === 'SUPPLY_MANAGER' && 'Procurement & Supplier Governance Tower'}
              {role === 'LOGISTICS_MANAGER' && 'Fleet & Dispatch Logistics Tower'}
              {role === 'ANALYST' && 'Predictive AI & Disruption Analytics Tower'}
              {role === 'SUPPLIER' && 'Supplier Partner Order Tower'}
            </div>
            <div style={{ fontSize: 13, color: 'var(--text-primary)', marginTop: 2, fontWeight: 500 }}>
              {role === 'ADMIN' && `Logged in as Administrator (${user?.email || 'admin'}). Complete enterprise oversight, user governance, and security tier control.`}
              {role === 'SUPPLY_MANAGER' && `Logged in as Supply Chain Manager (${user?.email || 'manager'}). Prioritizing vendor resilience, stock availability, and reorder signals.`}
              {role === 'LOGISTICS_MANAGER' && `Logged in as Logistics Manager (${user?.email || 'logistics'}). Monitoring in-transit route vectors, delay mitigation, and carrier dispatches.`}
              {role === 'ANALYST' && `Logged in as Analytics Specialist (${user?.email || 'analyst'}). Telemetry mode: full access to predictive AI, risk models, and chart exports.`}
              {role === 'SUPPLIER' && `Logged in as Supplier Partner (${user?.email || 'supplier'}). Viewing purchase orders and dispatch status.`}
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', gap: 8 }}>
          {role === 'ADMIN' && (
            <Link to="/users" className="nf-btn nf-btn-primary" style={{ fontSize: 12, padding: '8px 14px', textDecoration: 'none' }}>
              <Users size={14} /> Manage Users
            </Link>
          )}
          {role === 'SUPPLY_MANAGER' && (
            <Link to="/suppliers" className="nf-btn nf-btn-primary" style={{ fontSize: 12, padding: '8px 14px', textDecoration: 'none' }}>
              <Building2 size={14} /> Suppliers
            </Link>
          )}
          {role === 'LOGISTICS_MANAGER' && (
            <Link to="/shipments" className="nf-btn nf-btn-primary" style={{ fontSize: 12, padding: '8px 14px', textDecoration: 'none' }}>
              <Truck size={14} /> Dispatches
            </Link>
          )}
          {role === 'ANALYST' && (
            <Link to="/risk" className="nf-btn nf-btn-primary" style={{ fontSize: 12, padding: '8px 14px', textDecoration: 'none' }}>
              <ShieldAlert size={14} /> Risk Models
            </Link>
          )}
        </div>
      </div>

      {error && (
        <div style={{
          background: 'rgba(245,158,11,0.08)',
          border: '1px solid rgba(245,158,11,0.2)',
          borderRadius: 10,
          padding: '10px 14px',
          fontSize: 13,
          color: '#fbbf24'
        }}>
          Warning: {error}. Showing available data.
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(185px, 1fr))', gap: 14 }}>
        <KPICard label="Total Shipments" value={kpis.totalShipments.toLocaleString()} icon={Package} color="blue" change="+8%" changeDir="up" />
        <KPICard label="Active Suppliers" value={kpis.totalSuppliers.toLocaleString()} icon={TrendingUp} color="green" change="+3" changeDir="up" />
        <KPICard label="Predicted Delays" value={kpis.delayedShipments.toLocaleString()} icon={Clock} color="orange" change="+2" changeDir="up" />
        <KPICard label="Critical Risks" value={kpis.critical.toLocaleString()} icon={ShieldAlert} color="red" subtitle="Requires attention" />
        <KPICard label="Low Stock Items" value={kpis.lowStock.toLocaleString()} icon={Activity} color="purple" subtitle="Inventory pressure" />
      </div>

      <div className="nf-grid-4-responsive">
        {operatingPulse.map((pulse) => (
          <div key={pulse.label} className="nf-card" style={{ padding: '1rem 1.1rem' }}>
            <div style={{ fontSize: 11, textTransform: 'uppercase', letterSpacing: 0.8, color: 'var(--text-muted)' }}>
              {pulse.label}
            </div>
            <div style={{
              marginTop: 8,
              fontSize: 24,
              fontWeight: 800,
              color: pulse.tone === 'red' ? '#f87171' : pulse.tone === 'orange' ? '#f59e0b' : '#34d399'
            }}>
              {pulse.value}
            </div>
          </div>
        ))}
      </div>

      <div className="nf-dashboard-charts-grid">
        <div className="nf-card nf-fade-in-delay-1">
          <div className="nf-section-header">
            <div className="nf-section-title">
              <div className="nf-section-title-icon" style={{ background: 'rgba(79,124,255,0.15)' }}>
                <BarChart3 size={15} color="var(--accent-blue)" />
              </div>
              Monthly Shipment Trends
            </div>
          </div>
          <ResponsiveContainer width="100%" height={220}>
            <LineChart data={shipmentTrendData}>
              <defs>
                <linearGradient id="shipGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#4f7cff" stopOpacity={0.3} />
                  <stop offset="100%" stopColor="#4f7cff" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(99,130,255,0.08)" />
              <XAxis dataKey="month" tick={{ fill: 'var(--text-muted)', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: 'var(--text-muted)', fontSize: 11 }} axisLine={false} tickLine={false} />
              <Tooltip content={<DarkTooltip />} />
              <Line type="monotone" dataKey="count" stroke="#4f7cff" strokeWidth={2.5} dot={{ fill: '#4f7cff', r: 3 }} name="Shipments" />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="nf-card nf-fade-in-delay-1">
          <div className="nf-section-header">
            <div className="nf-section-title">
              <div className="nf-section-title-icon" style={{ background: 'rgba(239,68,68,0.15)' }}>
                <ShieldAlert size={15} color="var(--accent-red)" />
              </div>
              Risk Distribution
            </div>
          </div>
          <ResponsiveContainer width="100%" height={160}>
            <PieChart>
              <Pie data={riskDistData} cx="50%" cy="50%" outerRadius={70} dataKey="count" nameKey="level">
                {riskDistData.map((entry, i) => (
                  <Cell key={i} fill={RISK_COLORS[entry.level] ?? CHART_COLORS[i % CHART_COLORS.length]} />
                ))}
              </Pie>
              <Tooltip content={<DarkTooltip />} />
            </PieChart>
          </ResponsiveContainer>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 8 }}>
            {riskDistData.map((entry) => (
              <div key={entry.level} style={{ display: 'flex', alignItems: 'center', gap: 5, fontSize: 12 }}>
                <div style={{ width: 8, height: 8, borderRadius: '50%', background: RISK_COLORS[entry.level] }} />
                <span style={{ color: 'var(--text-secondary)' }}>
                  {entry.level}: <strong style={{ color: 'var(--text-primary)' }}>{entry.count}</strong>
                </span>
              </div>
            ))}
          </div>
        </div>

        <div className="nf-card nf-fade-in-delay-1">
          <div className="nf-section-header">
            <div className="nf-section-title">
              <div className="nf-section-title-icon" style={{ background: 'rgba(124,58,237,0.15)' }}>
                <Globe size={15} color="var(--accent-purple)" />
              </div>
              Suppliers by Country
            </div>
          </div>
          {supplierCountryData.length > 0 ? (
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={supplierCountryData} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(99,130,255,0.08)" horizontal={false} />
                <XAxis type="number" tick={{ fill: 'var(--text-muted)', fontSize: 10 }} axisLine={false} tickLine={false} />
                <YAxis type="category" dataKey="country" tick={{ fill: 'var(--text-secondary)', fontSize: 11 }} axisLine={false} tickLine={false} width={70} />
                <Tooltip content={<DarkTooltip />} />
                <Bar dataKey="count" fill="#7c3aed" radius={[0, 4, 4, 0]} name="Suppliers" />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <div style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '2rem', fontSize: 13 }}>
              No supplier data available
            </div>
          )}
        </div>
      </div>

      <div className="nf-dashboard-split-grid">
        <div className="nf-card nf-fade-in-delay-2" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '1.25rem 1.5rem', borderBottom: '1px solid var(--border-subtle)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div className="nf-section-title">
              <div className="nf-section-title-icon" style={{ background: 'rgba(0,212,255,0.15)' }}>
                <Truck size={15} color="var(--accent-cyan)" />
              </div>
              Recent Shipments
            </div>
            <a href="/shipments" style={{ fontSize: 12, color: 'var(--accent-blue)', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: 3 }}>
              View all <ChevronRight size={12} />
            </a>
          </div>
          <div style={{ overflowX: 'auto' }}>
            <table className="nf-table">
              <thead>
                <tr>
                  <th>Tracking #</th>
                  <th>Route</th>
                  <th>Supplier</th>
                  <th>Status</th>
                  <th>ETA</th>
                </tr>
              </thead>
              <tbody>
                {shipments.length === 0 ? (
                  <tr>
                    <td colSpan={5} style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '2rem' }}>
                      No shipment data available
                    </td>
                  </tr>
                ) : (
                  shipments.slice(0, 8).map((s) => (
                    <tr key={s.id}>
                      <td style={{ fontWeight: 600, fontFamily: 'monospace', fontSize: 13, color: 'var(--accent-cyan)' }}>
                        {s.trackingNumber}
                      </td>
                      <td style={{ color: 'var(--text-secondary)' }}>
                        <div>{s.originCity}{" -> "}{s.destinationCity}</div>
                        <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                          {s.currentLocation ?? 'In transit'}
                        </div>
                      </td>
                      <td style={{ color: 'var(--text-secondary)', maxWidth: 140, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {s.supplierName ?? 'Unknown supplier'}
                      </td>
                      <td><StatusBadge status={s.status} /></td>
                      <td style={{ color: 'var(--text-muted)', fontSize: 12 }}>
                        {formatDate(s.estimatedDeliveryDate)}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div className="nf-card nf-fade-in-delay-2">
            <div className="nf-section-title" style={{ marginBottom: 16 }}>
              <div className="nf-section-title-icon" style={{ background: 'rgba(16,185,129,0.15)' }}>
                <Activity size={15} color="var(--accent-green)" />
              </div>
              Chain Health
            </div>
            <HealthMeter label="Supplier Health" score={Math.round(supplierHealth)} color="#10b981" />
            <HealthMeter label="Logistics Health" score={Math.round(logisticsHealth)} color="#4f7cff" />
            <HealthMeter label="Inventory Health" score={Math.round(inventoryHealth)} color="#00d4ff" />
            <HealthMeter label="Risk Containment" score={Math.round(riskContainment)} color="#f59e0b" />
            <HealthMeter label="Overall Score" score={overallHealth} color="#f59e0b" />
          </div>

          <div className="nf-card nf-fade-in-delay-2">
            <div className="nf-section-title" style={{ marginBottom: 12 }}>
              <div className="nf-section-title-icon" style={{ background: 'rgba(245,158,11,0.15)' }}>
                <AlertTriangle size={15} color="var(--accent-orange)" />
              </div>
              Operational Pulse
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
              {topAlerts.length === 0 ? (
                <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No active alerts or notifications.</div>
              ) : (
                topAlerts.map((item, index) => {
                  const text = item?.riskType || item?.title || item?.message || 'Signal';
                  const body = item?.impactAssessment || item?.recommendedAction || item?.message || 'Review required';
                  const level = item?.severity || item?.type || 'LOW';
                  const isCritical = String(level).toUpperCase() === 'CRITICAL' || String(level).toUpperCase() === 'HIGH';

                  return (
                    <div key={item?.id ?? index} className={`nf-alert-item ${isCritical ? 'nf-alert-critical' : 'nf-alert-warning'}`}>
                      <div style={{ flexShrink: 0, marginTop: 2 }}>
                        <div className={`risk-dot risk-dot-${isCritical ? 'critical' : 'medium'} animate-pulse-dot`} />
                      </div>
                      <div style={{ minWidth: 0 }}>
                        <p style={{ margin: 0, fontSize: 12.5, fontWeight: 600, color: 'var(--text-primary)', lineHeight: 1.3 }}>
                          {text}
                        </p>
                        <p style={{ margin: '4px 0 0', fontSize: 11.5, color: 'var(--text-muted)', lineHeight: 1.5 }}>
                          {body}
                        </p>
                      </div>
                    </div>
                  );
                })
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="nf-grid-3-responsive">
        <div className="nf-card nf-fade-in-delay-3" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-subtle)', display: 'flex', alignItems: 'center', gap: 8 }}>
            <div style={{
              background: 'linear-gradient(135deg, rgba(79,124,255,0.2), rgba(0,212,255,0.1))',
              border: '1px solid rgba(79,124,255,0.25)',
              borderRadius: 8,
              padding: '4px 10px',
              display: 'flex',
              alignItems: 'center',
              gap: 6
            }}>
              <Zap size={12} color="var(--accent-cyan)" />
              <span style={{ fontSize: 11, fontWeight: 700, color: 'var(--accent-cyan)', textTransform: 'uppercase', letterSpacing: 0.5 }}>
                AI Insights
              </span>
            </div>
            <span className="nf-section-title" style={{ fontSize: '0.875rem' }}>Delay Forecasts</span>
          </div>
          <div style={{ padding: '0.75rem' }}>
            {topDelayPredictions.length === 0 ? (
              <div style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '1.5rem', fontSize: 13 }}>
                No delay predictions
              </div>
            ) : (
              topDelayPredictions.map((p, i) => {
                const prob = Math.round(toPercent(p.delayProbability));
                const confidence = Math.round(toPercent(p.confidenceScore));
                const color = prob >= 70 ? '#ef4444' : prob >= 50 ? '#f59e0b' : '#10b981';
                return (
                  <div key={p.id ?? i} className="nf-ai-recommendation">
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                      <span style={{ fontSize: 12.5, fontWeight: 600, color: 'var(--accent-cyan)', fontFamily: 'monospace' }}>
                        {p.trackingNumber ?? `Prediction ${i + 1}`}
                      </span>
                      <span style={{ fontSize: 12, fontWeight: 700, color }}>{prob}% delay</span>
                    </div>
                    <div className="nf-progress" style={{ height: 4 }}>
                      <div className="nf-progress-bar" style={{ width: `${prob}%`, background: `linear-gradient(90deg, ${color}, ${color}88)` }} />
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 6, fontSize: 11, color: 'var(--text-muted)' }}>
                      <span>Confidence {confidence}%</span>
                      <span>{p.predictionClass ?? 'Delay risk'}</span>
                    </div>
                    {p.predictionReason && (
                      <p style={{ margin: '6px 0 0', fontSize: 11, color: 'var(--text-muted)', lineHeight: 1.5 }}>
                        {p.predictionReason}
                      </p>
                    )}
                  </div>
                );
              })
            )}
          </div>
        </div>

        <div className="nf-card nf-fade-in-delay-3" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-subtle)' }}>
            <div className="nf-section-title">
              <div className="nf-section-title-icon" style={{ background: 'rgba(239,68,68,0.15)' }}>
                <AlertTriangle size={15} color="var(--accent-red)" />
              </div>
              Active Recommendations
            </div>
          </div>
          <div style={{ padding: '0.75rem' }}>
            {topActionItems.length === 0 ? (
              <div style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '1.5rem', fontSize: 13 }}>
                No active recommendations
              </div>
            ) : (
              topActionItems.map((rec, i) => (
                <div key={rec.id ?? i} className="nf-ai-recommendation">
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6, gap: 8 }}>
                    <span style={{ fontSize: 12.5, fontWeight: 600, color: 'var(--text-primary)' }}>
                      {rec.recommendationType ?? 'Action'}
                    </span>
                    <StatusBadge status={rec.status} />
                  </div>
                  <p style={{ margin: 0, fontSize: 11.5, color: 'var(--text-muted)', lineHeight: 1.5 }}>
                    {rec.recommendationText ?? 'Review and follow up on this recommendation.'}
                  </p>
                </div>
              ))
            )}
          </div>
        </div>

        <div className="nf-card nf-fade-in-delay-3">
          <div className="nf-section-header" style={{ marginBottom: 12 }}>
            <div className="nf-section-title">
              <div className="nf-section-title-icon" style={{ background: 'rgba(124,58,237,0.15)' }}>
                <Package size={15} color="var(--accent-purple)" />
              </div>
              Inventory Hotspots
            </div>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {lowStockItems.length === 0 ? (
              <div style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '1.5rem', fontSize: 13 }}>
                No low stock items
              </div>
            ) : (
              lowStockItems.slice(0, 5).map((item) => (
                <div key={item.id} className="nf-ai-recommendation">
                  <div style={{ display: 'flex', justifyContent: 'space-between', gap: 8 }}>
                    <div>
                      <div style={{ fontSize: 12.5, fontWeight: 600, color: 'var(--text-primary)' }}>
                        {item.productName ?? 'Unspecified product'}
                      </div>
                      <div style={{ fontSize: 11.5, color: 'var(--text-muted)' }}>
                        {item.warehouseName ?? 'Unknown warehouse'}
                      </div>
                    </div>
                    <span className="nf-badge nf-badge-critical">
                      {Number(item.availableQuantity ?? 0)} left
                    </span>
                  </div>
                  <div style={{ marginTop: 6, fontSize: 11, color: 'var(--text-muted)' }}>
                    Reorder point: {item.reorderPoint ?? 'N/A'} | Shortage risk: {item.shortageRiskLevel ?? 'N/A'}
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
