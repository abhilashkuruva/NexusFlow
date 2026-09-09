import React, { useState, useEffect, useRef } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import { Menu, Bell, Search, ChevronDown, Zap, LogOut, User, Shield, Truck, Settings } from 'lucide-react';
import { notificationAPI } from '../../services/api';

const Topbar = ({ onSidebarToggle }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [unreadCount, setUnreadCount] = useState(0);
  const [searchVal, setSearchVal] = useState('');
  const [menuOpen, setMenuOpen] = useState(false);
  const searchRef = useRef(null);
  const menuRef = useRef(null);

  useEffect(() => {
    try {
      const u = localStorage.getItem('user');
      if (u) {
        const parsed = JSON.parse(u);
        setUser(parsed);
        if (parsed?.id) {
          notificationAPI.getUnreadCount(parsed.id)
            .then(r => setUnreadCount(r?.data?.count ?? r?.data ?? 0))
            .catch(() => {});
        }
      }
    } catch { /* ignore */ }
  }, [location.pathname]);

  // Click outside to close user dropdown
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.clear();
    sessionStorage.clear();
    window.location.href = '/login';
  };

  const pageMap = {
    '/dashboard': { title: 'Control Tower', subtitle: 'Global Supply Chain Intelligence' },
    '/shipments': { title: 'Shipments', subtitle: 'Track & manage all logistics shipments' },
    '/suppliers': { title: 'Suppliers', subtitle: 'Supplier intelligence & risk profiles' },
    '/inventory': { title: 'Inventory', subtitle: 'Warehouse stock & depletion monitoring' },
    '/risk': { title: 'Risk Intelligence', subtitle: 'Predictive risk analytics & formula engine' },
    '/analytics': { title: 'Analytics', subtitle: 'Supply chain performance insights' },
    '/notifications': { title: 'Notifications', subtitle: 'Alerts & real-time telemetry' },
    '/portal': { title: 'Supplier Portal', subtitle: 'Active Vendor Dispatch & SLA Governance' },
    '/profile': { title: 'User Profile', subtitle: 'Account credentials & security settings' },
    '/users': { title: 'User Management', subtitle: 'Role-based access & permissions' },
  };

  const page = pageMap[location.pathname] || { title: 'NexusFlow', subtitle: 'AI Supply Chain Risk Platform' };

  const handleSearch = (e) => {
    if (e.key === 'Enter' && searchVal.trim()) {
      navigate(`/shipments?q=${encodeURIComponent(searchVal.trim())}`);
      setSearchVal('');
    }
  };

  return (
    <header className="nf-topbar">
      {/* Mobile menu button (< 1024px) */}
      <button
        type="button"
        className="nf-mobile-menu-btn"
        onClick={onSidebarToggle}
        aria-label="Open navigation"
      >
        <Menu size={18} />
      </button>

      {/* Logo (mobile only, < 1024px) */}
      <div className="nf-mobile-brand">
        <div style={{
          width: 28, height: 28, borderRadius: 7,
          background: 'linear-gradient(135deg, #dc2626, #991b1b)',
          display: 'flex', alignItems: 'center', justifyContent: 'center'
        }}>
          <Zap size={14} color="white" />
        </div>
        <span className="nf-sidebar-logo" style={{ fontSize: '1rem' }}>NexusFlow</span>
      </div>

      {/* Page title (desktop, >= 1024px) */}
      <div className="nf-desktop-title">
        <h1 style={{ margin: 0, fontSize: '1.0625rem', fontWeight: 700, color: 'var(--text-primary)', lineHeight: 1.2 }}>
          {page.title}
        </h1>
        {page.subtitle && (
          <p style={{ margin: 0, fontSize: 11.5, color: 'var(--text-muted)', lineHeight: 1.3 }}>
            {page.subtitle}
          </p>
        )}
      </div>

      {/* Spacer */}
      <div style={{ flex: 1 }} />

      {/* Search (Tablet & Desktop, >= 768px) */}
      <div className="nf-desktop-search">
        <Search size={14} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)', pointerEvents: 'none' }} />
        <input
          ref={searchRef}
          type="text"
          value={searchVal}
          onChange={e => setSearchVal(e.target.value)}
          onKeyDown={handleSearch}
          placeholder="Search shipments..."
          className="nf-input"
          style={{ paddingLeft: 32, height: 36, fontSize: 13, width: '100%' }}
        />
      </div>

      {/* Notifications */}
      <button
        type="button"
        onClick={() => navigate('/notifications')}
        style={{
          position: 'relative', background: '#ffffff', border: '1px solid var(--border-subtle)',
          borderRadius: 8, padding: '7px 9px', cursor: 'pointer', color: 'var(--text-secondary)',
          flexShrink: 0, display: 'flex', alignItems: 'center', justifyContent: 'center',
          boxShadow: 'var(--shadow-sm)'
        }}
        aria-label="Notifications"
      >
        <Bell size={18} />
        {unreadCount > 0 && (
          <span style={{
            position: 'absolute', top: -4, right: -4, minWidth: 16, height: 16,
            borderRadius: 8, background: '#dc2626', color: 'white',
            fontSize: 10, fontWeight: 700, display: 'flex', alignItems: 'center', justifyContent: 'center',
            padding: '0 3px', border: '2px solid #ffffff'
          }}>
            {unreadCount > 9 ? '9+' : unreadCount}
          </span>
        )}
      </button>

      {/* User profile dropdown pill */}
      <div ref={menuRef} style={{ position: 'relative', flexShrink: 0 }}>
        <div
          onClick={() => setMenuOpen(!menuOpen)}
          style={{
            display: 'flex', alignItems: 'center', gap: 8,
            background: menuOpen ? '#fff1f2' : '#ffffff',
            border: `1px solid ${menuOpen ? 'var(--accent-red)' : 'var(--border-subtle)'}`,
            borderRadius: 10, padding: '5px 10px', cursor: 'pointer',
            boxShadow: 'var(--shadow-sm)', transition: 'all 0.2s ease'
          }}
        >
          <div style={{
            width: 28, height: 28, borderRadius: 8, flexShrink: 0,
            background: 'linear-gradient(135deg, #dc2626, #991b1b)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 12, fontWeight: 700, color: 'white',
            boxShadow: '0 2px 6px rgba(220, 38, 38, 0.3)'
          }}>
            {user ? (user.firstName?.[0] ?? 'U').toUpperCase() : 'A'}
          </div>
          <div className="nf-topbar-username">
            <div style={{ fontSize: 12.5, fontWeight: 700, color: 'var(--text-primary)', lineHeight: 1.1, whiteSpace: 'nowrap' }}>
              {user ? `${user.firstName ?? 'Admin'}` : 'Admin'}
            </div>
            <div style={{ fontSize: 10.5, color: '#dc2626', fontWeight: 600, lineHeight: 1.2, whiteSpace: 'nowrap' }}>
              {user?.role ?? 'ADMIN'}
            </div>
          </div>
          <ChevronDown size={14} color={menuOpen ? '#dc2626' : 'var(--text-muted)'} style={{ flexShrink: 0, transition: 'transform 0.2s', transform: menuOpen ? 'rotate(180deg)' : 'none' }} />
        </div>

        {/* Dropdown Menu */}
        {menuOpen && (
          <div className="nf-dropdown-menu">
            <div style={{ padding: '8px 10px', borderBottom: '1px solid var(--border-subtle)', marginBottom: 6 }}>
              <div style={{ fontWeight: 700, fontSize: 13, color: 'var(--text-primary)' }}>
                {user ? `${user.firstName ?? ''} ${user.lastName ?? ''}`.trim() || 'User Account' : 'Administrator'}
              </div>
              <div style={{ fontSize: 11, color: 'var(--text-muted)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                {user?.email || 'admin@nexusflow.com'}
              </div>
              <div style={{ marginTop: 6 }}>
                <span className="nf-badge nf-badge-critical" style={{ fontSize: 10, padding: '2px 8px' }}>
                  {user?.role || 'ADMIN'}
                </span>
              </div>
            </div>

            <button
              className="nf-dropdown-item"
              onClick={() => { setMenuOpen(false); navigate('/profile'); }}
            >
              <User size={15} color="var(--accent-red)" />
              <span>Profile & Security</span>
            </button>

            {user?.role === 'SUPPLIER' || user?.role === 'ADMIN' ? (
              <button
                className="nf-dropdown-item"
                onClick={() => { setMenuOpen(false); navigate('/portal'); }}
              >
                <Truck size={15} color="#dc2626" />
                <span>Supplier Portal</span>
              </button>
            ) : null}

            {user?.role !== 'SUPPLIER' && (
              <button
                className="nf-dropdown-item"
                onClick={() => { setMenuOpen(false); navigate('/dashboard'); }}
              >
                <Shield size={15} color="#dc2626" />
                <span>Control Tower</span>
              </button>
            )}

            <div style={{ height: 1, background: 'var(--border-subtle)', margin: '6px 0' }} />

            <button
              className="nf-dropdown-item nf-dropdown-item-danger"
              onClick={handleLogout}
              id="topbar-logout-btn"
            >
              <LogOut size={15} />
              <span>Sign Out</span>
            </button>
          </div>
        )}
      </div>
    </header>
  );
};

export default Topbar;
