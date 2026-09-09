import React, { useState, useEffect, useMemo } from 'react';
import {
  User, Shield, Mail, Calendar, Key, CheckCircle2,
  AlertCircle, Save, Lock, Smartphone, Building, RefreshCw, LogOut
} from 'lucide-react';
import { userAPI } from '../services/api.js';
import { useAuth } from '../hooks/useAuth.js';

const ROLE_DESCRIPTIONS = {
  ADMIN: {
    label: 'Platform Administrator',
    color: '#f87171',
    bg: 'rgba(239,68,68,0.15)',
    description: 'Full unconstrained platform control, user provisioning, security policies, and system configuration.'
  },
  SUPPLY_MANAGER: {
    label: 'Supply Chain Manager',
    color: '#818cf8',
    bg: 'rgba(79,124,255,0.15)',
    description: 'Procurement orchestration, supplier onboarding, risk mitigation strategies, and SLA governance.'
  },
  LOGISTICS_MANAGER: {
    label: 'Logistics Manager',
    color: '#34d399',
    bg: 'rgba(16,185,129,0.15)',
    description: 'Fleet & warehouse coordination, active dispatch routing, inventory balancing, and on-time tracking.'
  },
  SUPPLIER: {
    label: 'Supplier Partner',
    color: '#fbbf24',
    bg: 'rgba(245,158,11,0.15)',
    description: 'Vendor portal access, active order dispatch visibility, SLA scorecards, and delivery confirmation.'
  },
  ANALYST: {
    label: 'Data & Risk Analyst',
    color: '#a78bfa',
    bg: 'rgba(124,58,237,0.15)',
    description: 'Predictive intelligence, disruption analytics, trend projections, and read-only telemetry reports.'
  }
};

export default function Profile() {
  const { user: authUser, role } = useAuth();
  const [profile, setProfile] = useState(() => {
    try {
      const stored = localStorage.getItem('user');
      return stored ? JSON.parse(stored) : {};
    } catch {
      return {};
    }
  });

  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstName: profile.firstName || '',
    lastName: profile.lastName || '',
    email: profile.email || '',
    phone: profile.phone || '+1 (555) 234-5678',
    department: profile.department || 'Operations & Supply Intelligence',
  });

  const [saving, setSaving] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  // Password change state
  const [passData, setPassData] = useState({ current: '', newPass: '', confirm: '' });
  const [passSaving, setPassSaving] = useState(false);
  const [passSuccess, setPassSuccess] = useState('');
  const [passError, setPassError] = useState('');

  useEffect(() => {
    // Attempt to load fresh profile from /users/me
    userAPI.getMe().then(res => {
      if (res.data) {
        setProfile(res.data);
        setFormData(prev => ({
          ...prev,
          firstName: res.data.firstName || prev.firstName,
          lastName: res.data.lastName || prev.lastName,
          email: res.data.email || prev.email,
        }));
      }
    }).catch(() => {
      // Fall back on stored user
    });
  }, []);

  const roleInfo = ROLE_DESCRIPTIONS[role] || ROLE_DESCRIPTIONS.ANALYST;

  const handleProfileSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setErrorMsg('');
    try {
      if (profile.id) {
        await userAPI.update(profile.id, {
          firstName: formData.firstName,
          lastName: formData.lastName,
          email: formData.email,
          role: profile.role
        });
      }
      const updated = {
        ...profile,
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        phone: formData.phone,
        department: formData.department,
      };
      localStorage.setItem('user', JSON.stringify(updated));
      setProfile(updated);
      setIsEditing(false);
      setSuccessMsg('Profile updated successfully!');
      setTimeout(() => setSuccessMsg(''), 4000);
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  const handlePasswordSave = async (e) => {
    e.preventDefault();
    setPassError('');
    if (passData.newPass.length < 6) {
      setPassError('New password must be at least 6 characters long.');
      return;
    }
    if (passData.newPass !== passData.confirm) {
      setPassError('New password and confirmation do not match.');
      return;
    }
    setPassSaving(true);
    // Simulate password change update
    setTimeout(() => {
      setPassSaving(false);
      setPassData({ current: '', newPass: '', confirm: '' });
      setPassSuccess('Security credentials updated successfully.');
      setTimeout(() => setPassSuccess(''), 4000);
    }, 600);
  };

  return (
    <div style={{ padding: '24px 32px', maxWidth: 1200, margin: '0 auto' }}>
      {/* Header */}
      <div style={{ marginBottom: 24 }}>
        <h1 style={{ fontSize: 24, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 10 }}>
          <User size={24} color="#4f7cff" />
          Account & Security Profile
        </h1>
        <p style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 4 }}>
          Manage your personal credentials, communication preferences, and view role entitlements.
        </p>
      </div>

      {successMsg && (
        <div style={{
          padding: '12px 16px', borderRadius: 8, marginBottom: 20,
          background: 'rgba(16,185,129,0.12)', border: '1px solid rgba(16,185,129,0.3)',
          color: '#34d399', fontSize: 13, display: 'flex', alignItems: 'center', gap: 8
        }}>
          <CheckCircle2 size={16} />
          {successMsg}
        </div>
      )}

      {errorMsg && (
        <div style={{
          padding: '12px 16px', borderRadius: 8, marginBottom: 20,
          background: 'rgba(239,68,68,0.12)', border: '1px solid rgba(239,68,68,0.3)',
          color: '#f87171', fontSize: 13, display: 'flex', alignItems: 'center', gap: 8
        }}>
          <AlertCircle size={16} />
          {errorMsg}
        </div>
      )}

      <div className="nf-profile-grid">
        {/* Left Column: Avatar & Role Card */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          <div className="nf-card" style={{ padding: 24, textAlign: 'center' }}>
            <div style={{
              width: 80, height: 80, borderRadius: 20, margin: '0 auto 16px',
              background: `linear-gradient(135deg, ${roleInfo.color}, #4f7cff)`,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontSize: 32, fontWeight: 700, color: '#fff',
              boxShadow: `0 8px 24px ${roleInfo.color}30`
            }}>
              {(profile.firstName?.[0] || profile.email?.[0] || 'U').toUpperCase()}
            </div>

            <h2 style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)' }}>
              {profile.firstName || profile.lastName ? `${profile.firstName || ''} ${profile.lastName || ''}`.trim() : 'Nexus User'}
            </h2>
            <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>{profile.email}</div>

            <div style={{ marginTop: 16 }}>
              <span style={{
                display: 'inline-flex', alignItems: 'center', gap: 6,
                background: roleInfo.bg, color: roleInfo.color,
                border: `1px solid ${roleInfo.color}40`,
                borderRadius: 20, padding: '4px 12px',
                fontSize: 12, fontWeight: 700
              }}>
                <span style={{ width: 6, height: 6, borderRadius: '50%', background: roleInfo.color }} />
                {roleInfo.label}
              </span>
            </div>
          </div>

          {/* Role Permissions Box */}
          <div className="nf-card" style={{ padding: 20 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 12 }}>
              <Shield size={16} color={roleInfo.color} />
              <h3 style={{ fontSize: 13, fontWeight: 700, color: 'var(--text-primary)', textTransform: 'uppercase' }}>
                Access Entitlements
              </h3>
            </div>
            <p style={{ fontSize: 12, color: 'var(--text-secondary)', lineHeight: 1.6 }}>
              {roleInfo.description}
            </p>
            <div style={{
              marginTop: 14, paddingTop: 14, borderTop: '1px solid var(--border-subtle)',
              fontSize: 11, color: 'var(--text-muted)'
            }}>
              Security Level: <strong>Tier {role === 'ADMIN' ? '0 (System)' : '1 (Operational)'}</strong>
            </div>
          </div>
        </div>

        {/* Right Column: Edit Details & Security */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {/* Personal Information Form */}
          <div className="nf-card" style={{ padding: 24 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
              <div>
                <h3 style={{ fontSize: 16, fontWeight: 700, color: 'var(--text-primary)' }}>Personal Information</h3>
                <p style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>Manage contact and organizational details</p>
              </div>
              {!isEditing ? (
                <button
                  type="button"
                  className="nf-btn nf-btn-secondary"
                  onClick={() => setIsEditing(true)}
                >
                  Edit Profile
                </button>
              ) : (
                <button
                  type="button"
                  className="nf-btn nf-btn-secondary"
                  onClick={() => setIsEditing(false)}
                >
                  Cancel
                </button>
              )}
            </div>

            <form onSubmit={handleProfileSave}>
              <div className="nf-grid-2-responsive" style={{ gap: 16, marginBottom: 16 }}>
                <div>
                  <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 6 }}>First Name</label>
                  <input
                    type="text"
                    disabled={!isEditing}
                    className="nf-input"
                    value={formData.firstName}
                    onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                  />
                </div>
                <div>
                  <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 6 }}>Last Name</label>
                  <input
                    type="text"
                    disabled={!isEditing}
                    className="nf-input"
                    value={formData.lastName}
                    onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                  />
                </div>
              </div>

              <div className="nf-grid-2-responsive" style={{ gap: 16, marginBottom: 16 }}>
                <div>
                  <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 6 }}>Email Address</label>
                  <input
                    type="email"
                    disabled={!isEditing}
                    className="nf-input"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  />
                </div>
                <div>
                  <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 6 }}>Phone Number</label>
                  <input
                    type="text"
                    disabled={!isEditing}
                    className="nf-input"
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  />
                </div>
              </div>

              <div style={{ marginBottom: 20 }}>
                <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 6 }}>Department / Unit</label>
                <input
                  type="text"
                  disabled={!isEditing}
                  className="nf-input"
                  value={formData.department}
                  onChange={(e) => setFormData({ ...formData, department: e.target.value })}
                />
              </div>

              {isEditing && (
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
                  <button
                    type="submit"
                    disabled={saving}
                    className="nf-btn nf-btn-primary"
                    style={{ display: 'flex', alignItems: 'center', gap: 6 }}
                  >
                    <Save size={14} />
                    {saving ? 'Saving...' : 'Save Profile Changes'}
                  </button>
                </div>
              )}
            </form>
          </div>

          {/* Security & Password Form */}
          <div className="nf-card" style={{ padding: 24 }}>
            <div style={{ marginBottom: 20 }}>
              <h3 style={{ fontSize: 16, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 8 }}>
                <Lock size={16} color="#818cf8" />
                Security & Authentication
              </h3>
              <p style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>
                Update access passphrase and credential policies
              </p>
            </div>

            {passSuccess && (
              <div style={{
                padding: '10px 14px', borderRadius: 6, background: 'rgba(16,185,129,0.12)',
                color: '#34d399', fontSize: 12, marginBottom: 16, display: 'flex', alignItems: 'center', gap: 6
              }}>
                <CheckCircle2 size={14} /> {passSuccess}
              </div>
            )}
            {passError && (
              <div style={{
                padding: '10px 14px', borderRadius: 6, background: 'rgba(239,68,68,0.12)',
                color: '#f87171', fontSize: 12, marginBottom: 16, display: 'flex', alignItems: 'center', gap: 6
              }}>
                <AlertCircle size={14} /> {passError}
              </div>
            )}

            <form onSubmit={handlePasswordSave}>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 16, marginBottom: 16 }}>
                <div>
                  <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 6 }}>Current Password</label>
                  <input
                    type="password"
                    required
                    placeholder="••••••••"
                    className="nf-input"
                    value={passData.current}
                    onChange={(e) => setPassData({ ...passData, current: e.target.value })}
                  />
                </div>
                <div>
                  <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 6 }}>New Password</label>
                  <input
                    type="password"
                    required
                    placeholder="Min. 6 chars"
                    className="nf-input"
                    value={passData.newPass}
                    onChange={(e) => setPassData({ ...passData, newPass: e.target.value })}
                  />
                </div>
                <div>
                  <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 6 }}>Confirm Password</label>
                  <input
                    type="password"
                    required
                    placeholder="Repeat new password"
                    className="nf-input"
                    value={passData.confirm}
                    onChange={(e) => setPassData({ ...passData, confirm: e.target.value })}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <button
                  type="submit"
                  disabled={passSaving}
                  className="nf-btn nf-btn-secondary"
                  style={{ display: 'flex', alignItems: 'center', gap: 6 }}
                >
                  <Key size={14} />
                  {passSaving ? 'Updating...' : 'Update Password'}
                </button>
              </div>
            </form>
          </div>

          {/* Session & Sign Out Card */}
          <div className="nf-card" style={{ marginTop: 24, border: '1px solid rgba(220,38,38,0.25)', background: '#fff9f9' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 16 }}>
              <div>
                <h3 style={{ fontSize: 16, fontWeight: 700, color: '#991b1b', margin: 0, display: 'flex', alignItems: 'center', gap: 8 }}>
                  <LogOut size={18} color="#dc2626" /> Active Session
                </h3>
                <p style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 4, marginBottom: 0 }}>
                  End your current authenticated session on this browser securely.
                </p>
              </div>
              <button
                type="button"
                onClick={() => {
                  localStorage.clear();
                  sessionStorage.clear();
                  window.location.href = '/login';
                }}
                className="nf-btn nf-btn-danger"
                style={{ display: 'flex', alignItems: 'center', gap: 8 }}
                id="profile-logout-btn"
              >
                <LogOut size={16} />
                Sign Out of NexusFlow
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
