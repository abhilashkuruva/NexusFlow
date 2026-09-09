import React, { useEffect, useMemo, useState } from 'react';
import {
  AlertTriangle, BrainCircuit, CheckCircle2, Clock, RefreshCw,
  Route, ShieldAlert, Target, TrendingUp, Zap, Calculator,
  Sliders, Info, HelpCircle, ArrowRight, ShieldCheck, Activity
} from 'lucide-react';
import { intelligenceAPI, riskAPI } from '../services/api';

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

const RiskBadge = ({ level }) => {
  const normalized = level?.toUpperCase?.() || 'LOW';
  return <span className={`nf-badge ${RISK_CLASS[normalized] || 'nf-badge-low'}`}>{normalized}</span>;
};

const StatusBadge = ({ status }) => {
  const normalized = status?.toLowerCase?.() || 'active';
  const map = {
    active: 'medium',
    stale: 'high',
    implemented: 'low',
    dismissed: 'blue',
    open: 'medium',
    closed: 'low',
  };
  return <span className={`nf-badge nf-badge-${map[normalized] || 'blue'}`}>{status || 'ACTIVE'}</span>;
};

const MetricCard = ({ label, value, icon: Icon, tone = 'blue', subtext }) => (
  <div className={`nf-kpi nf-kpi-${tone}`}>
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
      <div className="nf-section-title-icon">
        <Icon size={16} />
      </div>
    </div>
    <div className="kpi-value">{value}</div>
    <div className="kpi-label">{label}</div>
    {subtext && <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 5 }}>{subtext}</div>}
  </div>
);

/**
 * Interactive Risk Calculation Simulator Component
 */
const RiskEngineSimulator = () => {
  const [delayRate, setDelayRate] = useState(25); // 0-100%
  const [reliability, setReliability] = useState(4.2); // 1.0 - 5.0
  const [isDelayed, setIsDelayed] = useState(false);
  const [isUrgent, setIsUrgent] = useState(false);
  const [weatherScore, setWeatherScore] = useState(3.5); // 0-10
  const [routeScore, setRouteScore] = useState(4.0); // 0-10
  const [inventoryScore, setInventoryScore] = useState(2.0); // 0-10

  // Calculate live using exact backend formula
  const calculation = useMemo(() => {
    // 1. Supplier Risk Factor (0-100)
    // Formula: (Delay Rate * 40) + (Late Delivery * 30) + ((1.0 - perfHistory) * 30)
    const delayRateFactor = delayRate / 100.0;
    const lateDeliveryFactor = isDelayed ? 1.0 : 0.0;
    const perfHistory = reliability / 5.0;
    const supplierRisk = Math.min(100, Math.max(0, (delayRateFactor * 40) + (lateDeliveryFactor * 30) + ((1.0 - perfHistory) * 30)));

    // 2. Overall Risk Score (0-100)
    // Formula: (Supplier Risk * 0.40) + (Weather * 2.0) + (Route * 2.0) + (Inventory * 2.0)
    const supplierPart = supplierRisk * 0.40;
    const weatherPart = weatherScore * 2.0;
    const routePart = routeScore * 2.0;
    const inventoryPart = inventoryScore * 2.0;
    const overallScore = Math.min(100, Math.max(0, supplierPart + weatherPart + routePart + inventoryPart));

    // 3. Delay Probability (AI Module 1)
    // (5 - reliability) * 10 + delayRate * 30% + weather * 2 + delayed (25) + urgent (10)
    let prob = (5.0 - reliability) * 10;
    prob += delayRateFactor * 30;
    prob += weatherScore * 2.0;
    if (isDelayed) prob += 25.0;
    if (isUrgent) prob += 10.0;
    const delayProb = Math.min(100, Math.max(0, prob));

    // Determine category
    let category = 'LOW';
    if (overallScore >= 85 || delayProb >= 85) category = 'CRITICAL';
    else if (overallScore >= 65 || delayProb >= 65) category = 'HIGH';
    else if (overallScore >= 31 || delayProb >= 31) category = 'MEDIUM';

    return {
      supplierRisk: supplierRisk.toFixed(1),
      supplierPart: supplierPart.toFixed(1),
      weatherPart: weatherPart.toFixed(1),
      routePart: routePart.toFixed(1),
      inventoryPart: inventoryPart.toFixed(1),
      overallScore: overallScore.toFixed(1),
      delayProb: delayProb.toFixed(1),
      category,
      autoAlert: category === 'CRITICAL' || category === 'HIGH'
    };
  }, [delayRate, reliability, isDelayed, isUrgent, weatherScore, routeScore, inventoryScore]);

  return (
    <div className="nf-card" style={{ border: '1px solid rgba(220, 38, 38, 0.25)', background: '#ffffff' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16, flexWrap: 'wrap', gap: 12 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div className="nf-section-title-icon" style={{ background: '#fee2e2', color: '#dc2626' }}>
            <Calculator size={18} />
          </div>
          <div>
            <h3 style={{ margin: 0, fontSize: 16, fontWeight: 800, color: '#18181b' }}>
              Live Risk Engine Simulator & Mathematical Transparency
            </h3>
            <p style={{ margin: 0, fontSize: 12, color: 'var(--text-muted)' }}>
              Explore how changes in supplier reliability, weather storms, and route complexity affect the overall composite score.
            </p>
          </div>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <span style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600 }}>Active Category:</span>
          <RiskBadge level={calculation.category} />
        </div>
      </div>

      {/* Live Formula Banner */}
      <div style={{
        background: '#fff5f5',
        border: '1px solid rgba(220, 38, 38, 0.18)',
        borderRadius: 12,
        padding: '12px 16px',
        marginBottom: 20,
        fontFamily: 'monospace',
        fontSize: 12.5,
        color: '#991b1b',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        flexWrap: 'wrap',
        gap: 10
      }}>
        <div>
          <strong>Backend Formula:</strong> Overall Risk = (Supplier Risk × 0.40) + (Weather × 2.0) + (Route × 2.0) + (Inventory × 2.0)
        </div>
        <div style={{ fontWeight: 700, background: '#fee2e2', padding: '3px 10px', borderRadius: 6 }}>
          = {calculation.supplierPart} + {calculation.weatherPart} + {calculation.routePart} + {calculation.inventoryPart} = <strong>{calculation.overallScore} / 100</strong>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: 24 }}>
        {/* Sliders Area */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          {/* Supplier Delay Rate */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12.5, fontWeight: 700, marginBottom: 4 }}>
              <span>Supplier Historical Delay Rate:</span>
              <span style={{ color: '#dc2626' }}>{delayRate}%</span>
            </div>
            <input
              type="range"
              min="0"
              max="100"
              value={delayRate}
              onChange={(e) => setDelayRate(Number(e.target.value))}
              style={{ width: '100%', accentColor: '#dc2626' }}
            />
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 10.5, color: 'var(--text-muted)' }}>
              <span>0% (Always On-Time)</span>
              <span>100% (High Delays)</span>
            </div>
          </div>

          {/* Supplier Reliability */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12.5, fontWeight: 700, marginBottom: 4 }}>
              <span>Supplier Reliability Rating:</span>
              <span style={{ color: '#dc2626' }}>{reliability.toFixed(1)} / 5.0 ★</span>
            </div>
            <input
              type="range"
              min="1.0"
              max="5.0"
              step="0.1"
              value={reliability}
              onChange={(e) => setReliability(Number(e.target.value))}
              style={{ width: '100%', accentColor: '#dc2626' }}
            />
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 10.5, color: 'var(--text-muted)' }}>
              <span>1.0 (Unreliable)</span>
              <span>5.0 (Flawless SLA)</span>
            </div>
          </div>

          {/* Environmental factors */}
          <div className="nf-grid-3-responsive" style={{ gap: 12 }}>
            <div>
              <div style={{ fontSize: 11, fontWeight: 700, marginBottom: 4 }}>Weather (0-10)</div>
              <input
                type="number"
                min="0"
                max="10"
                step="0.5"
                value={weatherScore}
                onChange={(e) => setWeatherScore(Math.max(0, Math.min(10, Number(e.target.value))))}
                className="nf-input"
                style={{ padding: '4px 8px', fontSize: 13 }}
              />
              <span style={{ fontSize: 10, color: 'var(--text-muted)' }}>× 2.0 = +{calculation.weatherPart}</span>
            </div>

            <div>
              <div style={{ fontSize: 11, fontWeight: 700, marginBottom: 4 }}>Route Risk (0-10)</div>
              <input
                type="number"
                min="0"
                max="10"
                step="0.5"
                value={routeScore}
                onChange={(e) => setRouteScore(Math.max(0, Math.min(10, Number(e.target.value))))}
                className="nf-input"
                style={{ padding: '4px 8px', fontSize: 13 }}
              />
              <span style={{ fontSize: 10, color: 'var(--text-muted)' }}>× 2.0 = +{calculation.routePart}</span>
            </div>

            <div>
              <div style={{ fontSize: 11, fontWeight: 700, marginBottom: 4 }}>Inventory (0-10)</div>
              <input
                type="number"
                min="0"
                max="10"
                step="0.5"
                value={inventoryScore}
                onChange={(e) => setInventoryScore(Math.max(0, Math.min(10, Number(e.target.value))))}
                className="nf-input"
                style={{ padding: '4px 8px', fontSize: 13 }}
              />
              <span style={{ fontSize: 10, color: 'var(--text-muted)' }}>× 2.0 = +{calculation.inventoryPart}</span>
            </div>
          </div>

          {/* Toggles */}
          <div style={{ display: 'flex', gap: 16 }}>
            <label style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 12, fontWeight: 600, cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={isDelayed}
                onChange={(e) => setIsDelayed(e.target.checked)}
                style={{ accentColor: '#dc2626' }}
              />
              <span>Shipment Already Delayed (+30 factor)</span>
            </label>

            <label style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 12, fontWeight: 600, cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={isUrgent}
                onChange={(e) => setIsUrgent(e.target.checked)}
                style={{ accentColor: '#dc2626' }}
              />
              <span>Urgent Priority (+10 buffer)</span>
            </label>
          </div>
        </div>

        {/* Live Calculation Output Card */}
        <div style={{
          background: '#fafafc',
          border: '1px solid var(--border-subtle)',
          borderRadius: 14,
          padding: '1.25rem',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'space-between'
        }}>
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
              <span style={{ fontSize: 12, fontWeight: 700, textTransform: 'uppercase', color: 'var(--text-secondary)' }}>
                Live Derived Metrics
              </span>
              <RiskBadge level={calculation.category} />
            </div>

            {/* Overall Score Gauge */}
            <div style={{ textAlign: 'center', padding: '12px 0' }}>
              <div style={{ fontSize: '3rem', fontWeight: 900, color: '#dc2626', lineHeight: 1 }}>
                {calculation.overallScore}
              </div>
              <div style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600, marginTop: 4 }}>
                COMPOSITE OVERALL RISK (SCALE 0 - 100)
              </div>
            </div>

            <div className="nf-progress" style={{ height: 8, marginBottom: 16 }}>
              <div
                className="nf-progress-bar"
                style={{
                  width: `${calculation.overallScore}%`,
                  background: calculation.overallScore >= 85 ? '#dc2626' : calculation.overallScore >= 65 ? '#ea580c' : calculation.overallScore >= 31 ? '#d97706' : '#16a34a'
                }}
              />
            </div>

            {/* Sub-factors breakdown */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8, fontSize: 12 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-subtle)', paddingBottom: 4 }}>
                <span style={{ color: 'var(--text-secondary)' }}>Supplier Risk Index (40% weight):</span>
                <strong>{calculation.supplierRisk} / 100</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-subtle)', paddingBottom: 4 }}>
                <span style={{ color: 'var(--text-secondary)' }}>AI Delay Probability (Module 1):</span>
                <strong style={{ color: calculation.delayProb >= 65 ? '#dc2626' : '#16a34a' }}>{calculation.delayProb}%</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-subtle)', paddingBottom: 4 }}>
                <span style={{ color: 'var(--text-secondary)' }}>Automated Alert Trigger:</span>
                <strong style={{ color: calculation.autoAlert ? '#dc2626' : 'var(--text-muted)' }}>
                  {calculation.autoAlert ? '⚡ Dispatched to Admins & Logistics' : 'Normal Operations'}
                </strong>
              </div>
            </div>
          </div>

          <div style={{ marginTop: 16, padding: '8px 12px', background: '#ffffff', borderRadius: 8, border: '1px solid var(--border-subtle)', fontSize: 11, color: 'var(--text-muted)' }}>
            💡 <strong>Algorithm Insight:</strong> When risk transitions to HIGH (&ge;65) or CRITICAL (&ge;85), Spring Boot invokes NotificationService automatically.
          </div>
        </div>
      </div>
    </div>
  );
};

function Risk() {
  const [riskStats, setRiskStats] = useState(null);
  const [highRiskShipments, setHighRiskShipments] = useState([]);
  const [criticalRiskShipments, setCriticalRiskShipments] = useState([]);
  const [predictedDelays, setPredictedDelays] = useState([]);
  const [recentEvents, setRecentEvents] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [topPredictions, setTopPredictions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchData = async () => {
    try {
      setLoading(true);
      setError('');
      const [
        statsRes,
        highRiskRes,
        criticalRes,
        delaysRes,
        eventsRes,
        recsRes,
        predictionsRes,
      ] = await Promise.all([
        riskAPI.getStats(),
        riskAPI.getHighRisk(),
        riskAPI.getCriticalRisk(),
        riskAPI.getPredictedDelays(),
        intelligenceAPI.recentEvents(12),
        intelligenceAPI.activeRecommendations(8),
        intelligenceAPI.topPredictions(8),
      ]);

      setRiskStats(statsRes.data ?? null);
      setHighRiskShipments(Array.isArray(highRiskRes.data) ? highRiskRes.data : []);
      setCriticalRiskShipments(Array.isArray(criticalRes.data) ? criticalRes.data : []);
      setPredictedDelays(Array.isArray(delaysRes.data) ? delaysRes.data : []);
      setRecentEvents(Array.isArray(eventsRes.data) ? eventsRes.data : []);
      setRecommendations(Array.isArray(recsRes.data) ? recsRes.data : []);
      setTopPredictions(Array.isArray(predictionsRes.data) ? predictionsRes.data : []);
    } catch (err) {
      console.error('Error fetching risk data:', err);
      setError(err?.message || 'Failed to load risk intelligence data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const highAndCritical = useMemo(() => {
    const byShipment = new Map();
    [...criticalRiskShipments, ...highRiskShipments].forEach((item) => {
      byShipment.set(item.shipmentId ?? item.trackingNumber ?? item.id, item);
    });
    return [...byShipment.values()].sort((a, b) => toNumber(b.riskScore) - toNumber(a.riskScore));
  }, [criticalRiskShipments, highRiskShipments]);

  const disruptionPipeline = useMemo(() => {
    return [...recentEvents]
      .sort((a, b) => {
        const priority = { CRITICAL: 4, HIGH: 3, MEDIUM: 2, LOW: 1 };
        return (priority[b.severity] || 0) - (priority[a.severity] || 0);
      })
      .slice(0, 8);
  }, [recentEvents]);

  const metrics = useMemo(() => {
    const avgRisk = toNumber(riskStats?.averageRiskScore);
    const critical = toNumber(riskStats?.criticalRisk);
    const high = toNumber(riskStats?.highRisk);
    const delays = toNumber(riskStats?.predictedDelays, predictedDelays.length);
    const openEvents = recentEvents.filter((event) => (event.status || '').toUpperCase() !== 'CLOSED').length;

    return { avgRisk, critical, high, delays, openEvents };
  }, [predictedDelays.length, recentEvents, riskStats]);

  if (loading) {
    return (
      <div className="nf-card" style={{ padding: '2rem', textAlign: 'center' }}>
        <div style={{ width: 36, height: 36, border: '3px solid #fee2e2', borderTopColor: '#dc2626', borderRadius: '50%', margin: '0 auto 12px', animation: 'spin 0.8s linear infinite' }} />
        <div style={{ fontSize: 14, color: 'var(--text-muted)' }}>Loading risk intelligence telemetry & engine...</div>
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      {/* Header */}
      <div className="nf-fade-in" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 12 }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.45rem', fontWeight: 800, color: '#18181b' }}>
            Risk Intelligence & Predictive Analytics
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: 13, color: 'var(--text-muted)' }}>
            Four-pillar composite risk scoring, AI delay forecasting, and automated disruption containment.
          </p>
        </div>
        <button type="button" onClick={fetchData} className="nf-btn nf-btn-secondary" style={{ borderColor: 'var(--border-subtle)' }}>
          <RefreshCw size={14} color="#dc2626" /> Refresh Telemetry
        </button>
      </div>

      {error && (
        <div className="nf-card" style={{ background: '#fee2e2', border: '1px solid #f87171', color: '#991b1b' }}>
          <AlertTriangle size={14} style={{ marginRight: 8, display: 'inline' }} />
          {error}
        </div>
      )}

      {/* KPI Stats */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(190px, 1fr))', gap: 14 }}>
        <MetricCard label="Average Risk" value={metrics.avgRisk.toFixed(1)} icon={TrendingUp} tone="red" subtext="Across active network" />
        <MetricCard label="Critical Risks" value={metrics.critical.toLocaleString()} icon={ShieldAlert} tone="red" subtext="Requires immediate intervention" />
        <MetricCard label="High Risks" value={metrics.high.toLocaleString()} icon={AlertTriangle} tone="orange" subtext="Under active observation" />
        <MetricCard label="Predicted Delays" value={metrics.delays.toLocaleString()} icon={Clock} tone="purple" subtext="AI 48h early warning" />
        <MetricCard label="Open Events" value={metrics.openEvents.toLocaleString()} icon={Zap} tone="blue" subtext="Active risk tickets" />
      </div>

      {/* Interactive Risk Simulator & Formula Transparency */}
      <RiskEngineSimulator />

      {/* Main Grid: Shipments & Recommendations */}
      <div className="nf-grid-detail-layout">
        <div className="nf-card" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-subtle)', background: '#fff9f9' }}>
            <div className="nf-section-title">
              <ShieldAlert size={16} color="var(--accent-red)" /> Priority High & Critical Shipments
            </div>
            <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4 }}>
              Active dispatches with composite score exceeding automated safety thresholds.
            </div>
          </div>

          <div style={{ overflowX: 'auto' }}>
            <table className="nf-table">
              <thead>
                <tr>
                  <th>Shipment</th>
                  <th>Origin &rarr; Dest</th>
                  <th>Risk Score</th>
                  <th>Level</th>
                  <th>Weather</th>
                  <th>Route</th>
                </tr>
              </thead>
              <tbody>
                {highAndCritical.length === 0 ? (
                  <tr>
                    <td colSpan={6} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                      No shipments currently flagged as high or critical risk.
                    </td>
                  </tr>
                ) : (
                  highAndCritical.slice(0, 10).map((s) => (
                    <tr key={s.id || s.shipmentId}>
                      <td style={{ fontWeight: 700, fontFamily: 'monospace', color: '#dc2626' }}>
                        {s.trackingNumber || `SHP-${s.shipmentId}`}
                      </td>
                      <td>
                        <span style={{ fontSize: 12.5 }}>{s.originCity || 'Origin'} &rarr; {s.destinationCity || 'Dest'}</span>
                      </td>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                          <span style={{ fontWeight: 800 }}>{Number(s.riskScore || s.overallScore || 0).toFixed(1)}</span>
                          <div className="nf-progress" style={{ width: 60, height: 5 }}>
                            <div className="nf-progress-bar" style={{ width: `${Math.min(100, s.riskScore || s.overallScore || 0)}%` }} />
                          </div>
                        </div>
                      </td>
                      <td>
                        <RiskBadge level={s.riskLevel || 'HIGH'} />
                      </td>
                      <td style={{ fontSize: 12.5 }}>
                        {s.weatherRisk != null ? `${Number(s.weatherRisk).toFixed(1)}/10` : 'Normal'}
                      </td>
                      <td style={{ fontSize: 12.5 }}>
                        {s.routeRisk != null ? `${Number(s.routeRisk).toFixed(1)}/10` : 'Normal'}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* AI Delay Predictions */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div className="nf-card">
            <div className="nf-section-title" style={{ marginBottom: 12 }}>
              <BrainCircuit size={16} color="var(--accent-red)" /> AI Delay Probability Models
            </div>
            {topPredictions.length === 0 ? (
              <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No active ML delay predictions.</div>
            ) : (
              topPredictions.slice(0, 5).map((prediction) => {
                const probability = clamp(toPercent(prediction.confidenceScore ?? prediction.delayProbability ?? 50));
                return (
                  <div key={prediction.id} className="nf-ai-recommendation">
                    <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, marginBottom: 6 }}>
                      <div style={{ fontWeight: 700, color: 'var(--text-primary)' }}>
                        {prediction.predictionClass || prediction.trackingNumber || 'Delay Prediction'}
                      </div>
                      <span style={{ fontSize: 12, color: '#dc2626', fontWeight: 800 }}>{Math.round(probability)}%</span>
                    </div>
                    <div className="nf-progress" style={{ height: 5, marginBottom: 8 }}>
                      <div className="nf-progress-bar" style={{ width: `${probability}%`, background: probability >= 70 ? '#dc2626' : probability >= 45 ? '#ea580c' : '#16a34a' }} />
                    </div>
                    <div style={{ fontSize: 12, color: 'var(--text-muted)', lineHeight: 1.5 }}>
                      {prediction.explanation || prediction.predictionReason || `Predicted delay: ${prediction.predictedDelayHours ?? 'N/A'} hours`}
                    </div>
                  </div>
                );
              })
            )}
          </div>

          <div className="nf-card">
            <div className="nf-section-title" style={{ marginBottom: 12 }}>
              <Target size={16} color="#16a34a" /> Automated Containment Actions
            </div>
            {recommendations.length === 0 ? (
              <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No active recommendations.</div>
            ) : (
              recommendations.slice(0, 4).map((rec) => (
                <div key={rec.id} className="nf-ai-recommendation">
                  <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, marginBottom: 6 }}>
                    <div style={{ fontWeight: 700, color: 'var(--text-primary)' }}>{rec.recommendationType || 'Action'}</div>
                    <StatusBadge status={rec.status} />
                  </div>
                  <div style={{ fontSize: 12, color: 'var(--text-muted)', lineHeight: 1.5 }}>
                    {rec.recommendationText}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      {/* Disruption Feed & Watchlist */}
      <div className="nf-grid-2-responsive">
        <div className="nf-card">
          <div className="nf-section-title" style={{ marginBottom: 12 }}>
            <Route size={16} color="var(--accent-red)" /> Disruption Event Telemetry
          </div>
          {disruptionPipeline.length === 0 ? (
            <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No recent risk events recorded.</div>
          ) : (
            disruptionPipeline.map((event) => {
              const isCritical = ['CRITICAL', 'HIGH'].includes(event.severity);
              return (
                <div key={event.id} className={`nf-alert-item ${isCritical ? 'nf-alert-critical' : 'nf-alert-warning'}`} style={{ marginBottom: 10 }}>
                  <div className={`risk-dot risk-dot-${isCritical ? 'critical' : 'medium'}`} style={{ marginTop: 5 }} />
                  <div style={{ minWidth: 0 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
                      <span style={{ fontWeight: 700, color: 'var(--text-primary)' }}>{event.riskType || 'Risk Event'}</span>
                      <RiskBadge level={event.severity} />
                    </div>
                    <div style={{ fontSize: 12, color: 'var(--text-muted)', lineHeight: 1.5, marginTop: 5 }}>
                      {event.impactAssessment || event.recommendedAction || 'No impact assessment recorded.'}
                    </div>
                  </div>
                </div>
              );
            })
          )}
        </div>

        <div className="nf-card">
          <div className="nf-section-title" style={{ marginBottom: 12 }}>
            <CheckCircle2 size={16} color="#16a34a" /> Early Warning Watchlist
          </div>
          {predictedDelays.length === 0 ? (
            <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No delay predictions generated.</div>
          ) : (
            predictedDelays.slice(0, 6).map((prediction) => {
              const confidence = clamp(toPercent(prediction.confidenceScore));
              return (
                <div key={prediction.id} className="nf-ai-recommendation">
                  <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, marginBottom: 6 }}>
                    <span style={{ fontWeight: 700, color: 'var(--text-primary)', fontFamily: 'monospace' }}>{prediction.trackingNumber}</span>
                    <span className={`nf-badge ${prediction.isDelayed ? 'nf-badge-high' : 'nf-badge-low'}`}>
                      {prediction.isDelayed ? 'DELAY LIKELY' : 'ON TRACK'}
                    </span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12, color: 'var(--text-muted)', marginBottom: 6 }}>
                    <span>{prediction.predictedDelayHours ?? 0} hrs predicted</span>
                    <span>{Math.round(confidence)}% confidence</span>
                  </div>
                  <div className="nf-progress" style={{ height: 5 }}>
                    <div className="nf-progress-bar" style={{ width: `${confidence}%`, background: confidence >= 70 ? '#dc2626' : '#ea580c' }} />
                  </div>
                </div>
              );
            })
          )}
        </div>
      </div>
    </div>
  );
}

export default Risk;
