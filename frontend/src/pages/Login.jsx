import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';
import { Zap, Eye, EyeOff, AlertCircle, TrendingUp, Shield, Globe, Truck, Building2, CheckCircle2 } from 'lucide-react';

const ENTERPRISE_ACCOUNTS = [
  { label: 'Admin', role: 'ADMIN', email: 'admin@nexusflow.com', password: 'admin123', desc: 'Full platform administration' },
  { label: 'Supply Manager', role: 'SUPPLY_MANAGER', email: 'manager@nexusflow.com', password: 'admin123', desc: 'Procurement & vendor oversight' },
  { label: 'Logistics Mgr', role: 'LOGISTICS_MANAGER', email: 'logistics@nexusflow.com', password: 'admin123', desc: 'Dispatches & active routing' },
  { label: 'Analyst', role: 'ANALYST', email: 'analyst@nexusflow.com', password: 'admin123', desc: 'Risk intelligence & analytics' },
];

const SUPPLIER_ACCOUNTS = [
  { label: 'Apex Supplier Partner', role: 'SUPPLIER', email: 'supplier@nexusflow.com', password: 'admin123', desc: 'Direct access to Supplier Portal & orders' },
];

function Login() {
  const [activeTab, setActiveTab] = useState('enterprise'); // 'enterprise' | 'supplier'
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPwd, setShowPwd] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await authAPI.login(email, password);
      const data = response.data;

      if (!data || !data.token) {
        setError('Login failed. Server returned an invalid response.');
        return;
      }

      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify({
        id: data.id,
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        role: data.role
      }));

      // Route based on role
      if (data.role === 'SUPPLIER') {
        navigate('/portal');
      } else {
        navigate('/dashboard');
      }
    } catch (err) {
      const msg = err?.response?.data?.message || err?.message || 'Invalid credentials or login failed. Please try again.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  const fillDemo = (account) => {
    setEmail(account.email);
    setPassword(account.password);
    setError('');
  };

  const switchToSupplier = () => {
    setActiveTab('supplier');
    fillDemo(SUPPLIER_ACCOUNTS[0]);
  };

  const switchToEnterprise = () => {
    setActiveTab('enterprise');
    fillDemo(ENTERPRISE_ACCOUNTS[0]);
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#fafafb',
      backgroundImage: `
        radial-gradient(rgba(220, 38, 38, 0.08) 1.2px, transparent 1.2px),
        linear-gradient(to bottom, #ffffff, #fafafb 500px)
      `,
      backgroundSize: '24px 24px, 100% 100%',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '2rem 1.5rem',
      position: 'relative',
      overflow: 'hidden',
    }}>
      {/* Red pattern atmospheric elements */}
      <div style={{
        position: 'absolute', top: '-10%', left: '5%', zIndex: 0,
        width: 500, height: 500, borderRadius: '50%',
        background: 'radial-gradient(circle, rgba(220, 38, 38, 0.06), transparent 70%)',
        pointerEvents: 'none'
      }} />
      <div style={{
        position: 'absolute', bottom: '-10%', right: '5%', zIndex: 0,
        width: 550, height: 550, borderRadius: '50%',
        background: 'radial-gradient(circle, rgba(185, 28, 28, 0.05), transparent 70%)',
        pointerEvents: 'none'
      }} />

      <div style={{ display: 'flex', gap: '3.5rem', alignItems: 'center', position: 'relative', zIndex: 1, width: '100%', maxWidth: 1040 }}>

        {/* Left branding panel */}
        <div className="hidden lg:flex" style={{ flex: 1, flexDirection: 'column', gap: 28 }}>
          {/* Logo */}
          <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
            <div style={{
              width: 54, height: 54, borderRadius: 14,
              background: 'linear-gradient(135deg, #dc2626, #991b1b)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              boxShadow: '0 8px 24px rgba(220, 38, 38, 0.4)'
            }}>
              <Zap size={28} color="white" strokeWidth={2.5} />
            </div>
            <div>
              <div style={{ fontSize: '2rem', fontWeight: 800, letterSpacing: '-1px' }} className="nf-gradient-text-red">
                NexusFlow
              </div>
              <div style={{ fontSize: 13, color: 'var(--text-muted)', fontWeight: 500 }}>Global Supply Chain & Risk Intelligence</div>
            </div>
          </div>

          <div>
            <h2 style={{ fontSize: '2.1rem', fontWeight: 800, lineHeight: 1.25, color: '#18181b', marginBottom: 12 }}>
              Predict disruptions.<br />
              <span style={{ color: '#dc2626' }}>Automate resilience.</span>
            </h2>
            <p style={{ fontSize: 14.5, color: 'var(--text-secondary)', lineHeight: 1.65 }}>
              Dual-engine AI architecture combining real-time delay probability forecasting with multi-factor supplier, route, weather, and inventory risk scoring.
            </p>
          </div>

          {/* Feature highlights */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            {[
              { icon: Shield, color: '#dc2626', label: 'AI Risk Engine', desc: 'Predictive 4-pillar risk formula & automated threshold alerting' },
              { icon: Truck, color: '#b91c1c', label: 'Dedicated Supplier Portal', desc: 'Direct dispatch tracking, order SLA fulfillment, and PO transparency' },
              { icon: Globe, color: '#991b1b', label: 'Multi-Modal Logistics', desc: 'Live choke-point analysis, transit metrics & warehouse stock balancing' },
            ].map(({ icon: Icon, color, label, desc }) => (
              <div key={label} style={{
                display: 'flex', alignItems: 'flex-start', gap: 14,
                padding: '12px 14px', background: '#ffffff',
                borderRadius: 12, border: '1px solid rgba(220, 38, 38, 0.1)',
                boxShadow: '0 2px 8px rgba(0,0,0,0.02)'
              }}>
                <div style={{
                  width: 36, height: 36, borderRadius: 9, flexShrink: 0,
                  background: '#fee2e2', border: '1px solid #fca5a5',
                  display: 'flex', alignItems: 'center', justifyContent: 'center'
                }}>
                  <Icon size={18} color={color} />
                </div>
                <div>
                  <div style={{ fontSize: 13.5, fontWeight: 700, color: 'var(--text-primary)' }}>{label}</div>
                  <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{desc}</div>
                </div>
              </div>
            ))}
          </div>

          {/* Platform stats */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 14 }}>
            {[
              { val: '4-Pillar', lbl: 'Risk Engine' },
              { val: '48h', lbl: 'Early Warning' },
              { val: '100%', lbl: 'SLA Visibility' },
            ].map(({ val, lbl }) => (
              <div key={lbl} style={{
                background: '#ffffff', border: '1px solid var(--border-subtle)',
                borderRadius: 12, padding: '12px 14px', textAlign: 'center',
                boxShadow: 'var(--shadow-sm)'
              }}>
                <div style={{ fontSize: '1.25rem', fontWeight: 800, color: '#dc2626' }}>{val}</div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 2, fontWeight: 600 }}>{lbl}</div>
              </div>
            ))}
          </div>
        </div>

        {/* Right login card */}
        <div style={{ width: '100%', maxWidth: 460 }}>
          <div style={{
            background: '#ffffff',
            border: '1px solid rgba(220, 38, 38, 0.2)',
            borderRadius: 22,
            padding: '2.25rem',
            boxShadow: '0 12px 40px rgba(220, 38, 38, 0.08), 0 4px 16px rgba(0,0,0,0.04)'
          }}>
            {/* Mobile logo */}
            <div className="flex lg:hidden" style={{ alignItems: 'center', gap: 10, marginBottom: 20 }}>
              <div style={{
                width: 38, height: 38, borderRadius: 10,
                background: 'linear-gradient(135deg, #dc2626, #991b1b)',
                display: 'flex', alignItems: 'center', justifyContent: 'center'
              }}>
                <Zap size={20} color="white" />
              </div>
              <span style={{ fontWeight: 800, fontSize: '1.25rem' }} className="nf-gradient-text-red">NexusFlow</span>
            </div>

            <h2 style={{ margin: '0 0 6px', fontSize: '1.45rem', fontWeight: 800, color: 'var(--text-primary)' }}>
              Sign in to NexusFlow
            </h2>
            <p style={{ margin: '0 0 20px', fontSize: 13, color: 'var(--text-muted)' }}>
              Select your role category to access the control tower or vendor portal
            </p>

            {/* Portal Tab Switcher */}
            <div style={{
              display: 'flex',
              background: '#f4f4f5',
              borderRadius: 12,
              padding: 4,
              marginBottom: 20,
              gap: 4
            }}>
              <button
                type="button"
                onClick={switchToEnterprise}
                style={{
                  flex: 1,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: 7,
                  padding: '8px 12px',
                  borderRadius: 9,
                  border: 'none',
                  fontSize: 12.5,
                  fontWeight: 700,
                  cursor: 'pointer',
                  background: activeTab === 'enterprise' ? '#ffffff' : 'transparent',
                  color: activeTab === 'enterprise' ? '#dc2626' : 'var(--text-secondary)',
                  boxShadow: activeTab === 'enterprise' ? '0 2px 6px rgba(0,0,0,0.06)' : 'none',
                  transition: 'all 0.2s ease'
                }}
              >
                <Building2 size={15} />
                <span>Enterprise Staff</span>
              </button>

              <button
                type="button"
                onClick={switchToSupplier}
                style={{
                  flex: 1,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: 7,
                  padding: '8px 12px',
                  borderRadius: 9,
                  border: 'none',
                  fontSize: 12.5,
                  fontWeight: 700,
                  cursor: 'pointer',
                  background: activeTab === 'supplier' ? '#dc2626' : 'transparent',
                  color: activeTab === 'supplier' ? '#ffffff' : 'var(--text-secondary)',
                  boxShadow: activeTab === 'supplier' ? '0 2px 8px rgba(220,38,38,0.3)' : 'none',
                  transition: 'all 0.2s ease'
                }}
              >
                <Truck size={15} />
                <span>Supplier Portal</span>
              </button>
            </div>

            {/* Demo Quick-Fill Buttons */}
            <div style={{ marginBottom: 20 }}>
              <div style={{ fontSize: 11, fontWeight: 700, color: '#dc2626', textTransform: 'uppercase', letterSpacing: 0.6, marginBottom: 8 }}>
                {activeTab === 'enterprise' ? 'Enterprise Demo Accounts' : 'Supplier Partner Access'}
              </div>
              
              {activeTab === 'enterprise' ? (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 8 }}>
                  {ENTERPRISE_ACCOUNTS.map(acc => (
                    <button
                      key={acc.label}
                      type="button"
                      onClick={() => fillDemo(acc)}
                      style={{
                        background: email === acc.email ? '#fff1f2' : '#ffffff',
                        border: `1px solid ${email === acc.email ? '#dc2626' : 'var(--border-subtle)'}`,
                        borderRadius: 8, padding: '7px 10px', fontSize: 12, fontWeight: 600,
                        color: email === acc.email ? '#dc2626' : 'var(--text-secondary)',
                        cursor: 'pointer', transition: 'all 0.15s', textAlign: 'left',
                        boxShadow: 'var(--shadow-sm)'
                      }}
                    >
                      <div style={{ fontWeight: 700 }}>{acc.label}</div>
                      <div style={{ fontSize: 10, color: 'var(--text-muted)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {acc.email}
                      </div>
                    </button>
                  ))}
                </div>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                  {SUPPLIER_ACCOUNTS.map(acc => (
                    <button
                      key={acc.label}
                      type="button"
                      onClick={() => fillDemo(acc)}
                      style={{
                        background: '#fff9f9',
                        border: '1px solid #dc2626',
                        borderRadius: 10, padding: '10px 14px', fontSize: 12.5, fontWeight: 600,
                        color: '#991b1b', cursor: 'pointer', transition: 'all 0.15s', textAlign: 'left',
                        boxShadow: '0 2px 8px rgba(220,38,38,0.1)'
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <span style={{ fontWeight: 800, fontSize: 13 }}>{acc.label}</span>
                        <span className="nf-badge nf-badge-critical" style={{ fontSize: 9 }}>Supplier Role</span>
                      </div>
                      <div style={{ fontSize: 11.5, color: '#dc2626', marginTop: 3 }}>{acc.email}</div>
                      <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 2 }}>{acc.desc}</div>
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Error banner */}
            {error && (
              <div style={{
                display: 'flex', alignItems: 'center', gap: 8,
                background: '#fee2e2', border: '1px solid #f87171',
                borderRadius: 9, padding: '10px 14px', marginBottom: 16
              }}>
                <AlertCircle size={15} color="#dc2626" />
                <span style={{ fontSize: 12.5, color: '#991b1b', fontWeight: 600 }}>{error}</span>
              </div>
            )}

            <form onSubmit={handleSubmit}>
              {/* Email */}
              <div style={{ marginBottom: 14 }}>
                <label className="nf-label" htmlFor="login-email">Email Address</label>
                <input
                  id="login-email"
                  type="email"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  className="nf-input"
                  placeholder="name@nexusflow.com"
                  required
                  autoComplete="email"
                />
              </div>

              {/* Password */}
              <div style={{ marginBottom: 20 }}>
                <label className="nf-label" htmlFor="login-password">Password</label>
                <div style={{ position: 'relative' }}>
                  <input
                    id="login-password"
                    type={showPwd ? 'text' : 'password'}
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    className="nf-input"
                    placeholder="••••••••"
                    required
                    autoComplete="current-password"
                    style={{ paddingRight: 40 }}
                  />
                  <button
                    type="button"
                    onClick={() => setShowPwd(v => !v)}
                    style={{
                      position: 'absolute', right: 10, top: '50%', transform: 'translateY(-50%)',
                      background: 'none', border: 'none', cursor: 'pointer',
                      color: 'var(--text-muted)', padding: 4
                    }}
                    aria-label={showPwd ? 'Hide password' : 'Show password'}
                  >
                    {showPwd ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className="nf-btn nf-btn-primary"
                style={{ width: '100%', justifyContent: 'center', padding: '0.8rem', fontSize: 14, borderRadius: 10 }}
                id="login-submit-btn"
              >
                {loading ? (
                  <>
                    <span style={{ width: 14, height: 14, border: '2px solid rgba(255,255,255,0.4)', borderTopColor: 'white', borderRadius: '50%', display: 'inline-block', animation: 'spin 0.8s linear infinite' }} />
                    Authenticating...
                  </>
                ) : (
                  activeTab === 'supplier' ? 'Sign In to Supplier Portal' : 'Sign In to Control Tower'
                )}
              </button>
            </form>

            <div style={{ marginTop: 20, padding: '10px 14px', background: '#fff5f5', borderRadius: 9, border: '1px solid rgba(220,38,38,0.15)' }}>
              <div style={{ fontSize: 11.5, color: '#991b1b', lineHeight: 1.6 }}>
                <strong>Default demo password:</strong>{' '}
                <code style={{ background: '#fee2e2', padding: '1px 5px', borderRadius: 4, color: '#b91c1c', fontWeight: 700 }}>admin123</code>
                {' '}for all accounts.
              </div>
            </div>
          </div>
        </div>
      </div>

      <style>{`
        @keyframes spin { to { transform: rotate(360deg); } }
      `}</style>
    </div>
  );
}

export default Login;