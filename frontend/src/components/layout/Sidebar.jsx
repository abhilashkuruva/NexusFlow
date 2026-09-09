import React, { useMemo } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard, Package, Building2, ShieldAlert,
  BarChart3, Bell, X, Activity, Boxes, LogOut,
  Zap, Users, UserCircle, Truck
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth.js';

const ALL_NAV_ITEMS = [
  { to: '/dashboard',  label: 'Dashboard',       icon: LayoutDashboard, section: 'OVERVIEW',     roles: null },
  { to: '/shipments',  label: 'Shipments',        icon: Package,         section: 'OPERATIONS',   roles: ['ADMIN','SUPPLY_MANAGER','LOGISTICS_MANAGER','ANALYST'] },
  { to: '/portal',     label: 'My Shipments',     icon: Truck,           section: 'OPERATIONS',   roles: ['SUPPLIER'] },
  { to: '/suppliers',  label: 'Suppliers',        icon: Building2,       section: 'OPERATIONS',   roles: ['ADMIN','SUPPLY_MANAGER','LOGISTICS_MANAGER'] },
  { to: '/inventory',  label: 'Inventory',        icon: Boxes,           section: 'OPERATIONS',   roles: ['ADMIN','SUPPLY_MANAGER','LOGISTICS_MANAGER','ANALYST'] },
  { to: '/risk',       label: 'Risk Intelligence',icon: ShieldAlert,     section: 'INTELLIGENCE', roles: null },
  { to: '/analytics',  label: 'Analytics',        icon: BarChart3,       section: 'INTELLIGENCE', roles: null },
  { to: '/users',      label: 'User Management',  icon: Users,           section: 'ADMIN',        roles: ['ADMIN'] },
  { to: '/notifications',label: 'Notifications',  icon: Bell,            section: 'SYSTEM',       roles: null },
  { to: '/profile',    label: 'Profile',          icon: UserCircle,      section: 'SYSTEM',       roles: null },
];

const ROLE_BADGE_COLORS = {
  ADMIN:            { bg: 'rgba(239,68,68,0.15)',   color: '#f87171',  label: 'Admin' },
  SUPPLY_MANAGER:   { bg: 'rgba(79,124,255,0.15)',  color: '#818cf8',  label: 'Supply Manager' },
  LOGISTICS_MANAGER:{ bg: 'rgba(16,185,129,0.15)', color: '#34d399',  label: 'Logistics Mgr' },
  SUPPLIER:         { bg: 'rgba(245,158,11,0.15)',  color: '#fbbf24',  label: 'Supplier' },
  ANALYST:          { bg: 'rgba(124,58,237,0.15)',  color: '#a78bfa',  label: 'Analyst' },
};

const SidebarContent = ({ onClose }) => {
  const navigate = useNavigate();
  const { user, role } = useAuth();

  const items = useMemo(() => {
    return ALL_NAV_ITEMS.filter(item => !item.roles || item.roles.includes(role));
  }, [role]);

  const sections = useMemo(() => {
    const map = {};
    items.forEach(item => {
      if (!map[item.section]) map[item.section] = [];
      map[item.section].push(item);
    });
    return map;
  }, [items]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.clear();
    sessionStorage.clear();
    window.location.href = '/login';
  };

  const badgeStyle = ROLE_BADGE_COLORS[role] ?? ROLE_BADGE_COLORS.ANALYST;

  return (
    <div className="flex h-full flex-col" style={{ background: 'var(--bg-secondary)' }}>
      {/* Logo */}
      <div style={{ padding: '20px 20px 16px', borderBottom: '1px solid var(--border-subtle)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <div style={{
            width: 36, height: 36, borderRadius: 10,
            background: 'linear-gradient(135deg, #dc2626, #991b1b)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            boxShadow: '0 4px 12px rgba(220,38,38,0.35)'
          }}>
            <Zap size={18} color="white" strokeWidth={2.5} />
          </div>
          <div>
            <div className="nf-sidebar-logo">NexusFlow</div>
            <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 1 }}>Supply Chain Intelligence</div>
          </div>
        </div>
      </div>

      {/* System status */}
      <div style={{ padding: '12px 16px', borderBottom: '1px solid var(--border-subtle)' }}>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 8,
          background: 'rgba(16,185,129,0.08)',
          border: '1px solid rgba(16,185,129,0.2)',
          borderRadius: 8, padding: '6px 10px'
        }}>
          <div className="risk-dot risk-dot-low animate-pulse-dot" />
          <span style={{ fontSize: 11, color: '#34d399', fontWeight: 600 }}>All Systems Operational</span>
          <Activity size={11} color="#34d399" style={{ marginLeft: 'auto' }} />
        </div>
      </div>

      {/* Role badge */}
      <div style={{ padding: '10px 16px', borderBottom: '1px solid var(--border-subtle)' }}>
        <div style={{
          display: 'inline-flex', alignItems: 'center', gap: 6,
          background: badgeStyle.bg,
          color: badgeStyle.color,
          border: `1px solid ${badgeStyle.color}40`,
          borderRadius: 20, padding: '3px 10px',
          fontSize: 11, fontWeight: 700, letterSpacing: 0.4
        }}>
          <div style={{ width: 6, height: 6, borderRadius: '50%', background: badgeStyle.color }} />
          {badgeStyle.label}
        </div>
      </div>

      {/* Nav */}
      <nav style={{ flex: 1, padding: '12px 12px', overflowY: 'auto' }}>
        {Object.entries(sections).map(([section, sectionItems]) => (
          <div key={section} style={{ marginBottom: 20 }}>
            <div style={{
              fontSize: 10, fontWeight: 700, letterSpacing: 1.2,
              color: 'var(--text-muted)', textTransform: 'uppercase',
              padding: '0 8px', marginBottom: 6
            }}>
              {section}
            </div>
            {sectionItems.map(({ to, label, icon: Icon }) => (
              <NavLink
                key={to}
                to={to}
                end={to === '/dashboard'}
                onClick={onClose}
                className={({ isActive }) => `nf-nav-item ${isActive ? 'active' : ''}`}
                style={{ display: 'flex', marginBottom: 4 }}
              >
                <Icon size={16} strokeWidth={2} style={{ flexShrink: 0 }} />
                <span>{label}</span>
              </NavLink>
            ))}
          </div>
        ))}
      </nav>

      {/* User profile */}
      <div style={{ padding: '12px 16px', borderTop: '1px solid var(--border-subtle)' }}>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 10,
          padding: '10px 12px', borderRadius: 10,
          background: 'var(--bg-elevated)',
          border: '1px solid var(--border-subtle)',
          marginBottom: 8
        }}>
          <div style={{
            width: 32, height: 32, borderRadius: 8,
            background: 'linear-gradient(135deg, #dc2626, #991b1b)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 13, fontWeight: 700, color: 'white', flexShrink: 0,
            boxShadow: '0 2px 6px rgba(220,38,38,0.25)'
          }}>
            {user ? (user.firstName?.[0] ?? 'U').toUpperCase() : 'A'}
          </div>
          <div style={{ minWidth: 0 }}>
            <div style={{ fontSize: 13, fontWeight: 700, color: 'var(--text-primary)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
              {user ? `${user.firstName ?? ''} ${user.lastName ?? ''}`.trim() || 'User' : 'User'}
            </div>
            <div style={{ fontSize: 11, color: 'var(--text-muted)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
              {user?.email ?? ''}
            </div>
          </div>
        </div>
        <button
          className="nf-btn nf-btn-secondary"
          style={{ width: '100%', justifyContent: 'center', borderColor: 'var(--border-subtle)' }}
          onClick={handleLogout}
          id="sidebar-logout-btn"
        >
          <LogOut size={14} color="#dc2626" />
          <span>Sign Out</span>
        </button>
      </div>
    </div>
  );
};

const Sidebar = ({ mobileOpen, onMobileClose }) => {
  return (
    <>
      {/* Mobile overlay */}
      {mobileOpen && (
        <div
          className="nf-sidebar-mobile-overlay"
          onClick={onMobileClose}
          aria-hidden="true"
        />
      )}

      {/* Mobile drawer (Tablet & Mobile only) */}
      <div
        className="nf-sidebar-mobile-drawer"
        style={{
          transform: mobileOpen ? 'translateX(0)' : 'translateX(-100%)',
        }}
        role="dialog"
        aria-modal="true"
        aria-label="Navigation"
      >
        <div style={{ height: '100%', position: 'relative' }}>
          <button
            type="button"
            onClick={onMobileClose}
            style={{
              position: 'absolute', top: 14, right: 14, zIndex: 10,
              background: 'var(--bg-elevated)', border: '1px solid var(--border-subtle)',
              borderRadius: 8, padding: 6, cursor: 'pointer', color: 'var(--text-secondary)'
            }}
            aria-label="Close sidebar"
          >
            <X size={16} />
          </button>
          <SidebarContent onClose={onMobileClose} />
        </div>
      </div>

      {/* Desktop sidebar (>= 1024px only) */}
      <aside className="nf-sidebar-desktop">
        <SidebarContent onClose={() => {}} />
      </aside>
    </>
  );
};

export default Sidebar;
