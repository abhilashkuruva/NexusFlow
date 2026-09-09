import React, { useState, useEffect, useMemo } from 'react';
import {
  Users as UsersIcon, UserPlus, Shield, CheckCircle2, XCircle,
  Search, RefreshCw, Edit3, Trash2, Mail, Calendar, Key, AlertCircle
} from 'lucide-react';
import { userAPI, authAPI } from '../services/api.js';

const ROLE_OPTIONS = [
  { value: 'ADMIN', label: 'Admin', color: '#f87171', bg: 'rgba(239,68,68,0.15)' },
  { value: 'SUPPLY_MANAGER', label: 'Supply Manager', color: '#818cf8', bg: 'rgba(79,124,255,0.15)' },
  { value: 'LOGISTICS_MANAGER', label: 'Logistics Manager', color: '#34d399', bg: 'rgba(16,185,129,0.15)' },
  { value: 'SUPPLIER', label: 'Supplier', color: '#fbbf24', bg: 'rgba(245,158,11,0.15)' },
  { value: 'ANALYST', label: 'Analyst', color: '#a78bfa', bg: 'rgba(124,58,237,0.15)' },
];

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [roleFilter, setRoleFilter] = useState('ALL');

  // Modals
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingUser, setEditingUser] = useState(null);

  // Form states
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: 'ANALYST',
  });
  const [formSubmitting, setFormSubmitting] = useState(false);
  const [formError, setFormError] = useState('');

  const fetchUsers = async () => {
    setLoading(true);
    setError('');
    try {
      // Fetch all users including inactive
      const res = await userAPI.getAll();
      setUsers(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.error('Failed to load users:', err);
      setError(err.response?.data?.message || 'Failed to load user list');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleOpenAdd = () => {
    setFormData({
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      role: 'ANALYST',
    });
    setFormError('');
    setShowAddModal(true);
  };

  const handleOpenEdit = (u) => {
    setEditingUser(u);
    setFormData({
      firstName: u.firstName || '',
      lastName: u.lastName || '',
      email: u.email || '',
      password: '',
      role: u.role || 'ANALYST',
    });
    setFormError('');
  };

  const handleSaveAdd = async (e) => {
    e.preventDefault();
    setFormSubmitting(true);
    setFormError('');
    try {
      await authAPI.register({
        email: formData.email,
        password: formData.password || 'admin123',
        firstName: formData.firstName,
        lastName: formData.lastName,
        role: formData.role,
      });
      setSuccessMsg(`User ${formData.email} registered successfully!`);
      setShowAddModal(false);
      fetchUsers();
      setTimeout(() => setSuccessMsg(''), 4000);
    } catch (err) {
      setFormError(err.response?.data?.message || 'Failed to create user');
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleSaveEdit = async (e) => {
    e.preventDefault();
    if (!editingUser) return;
    setFormSubmitting(true);
    setFormError('');
    try {
      await userAPI.update(editingUser.id, {
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        role: formData.role,
      });
      setSuccessMsg(`User updated successfully!`);
      setEditingUser(null);
      fetchUsers();
      setTimeout(() => setSuccessMsg(''), 4000);
    } catch (err) {
      setFormError(err.response?.data?.message || 'Failed to update user');
    } finally {
      setFormSubmitting(false);
    }
  };

  const handleDeactivate = async (u) => {
    if (!window.confirm(`Are you sure you want to deactivate ${u.email}?`)) return;
    try {
      await userAPI.deactivate(u.id);
      setSuccessMsg(`User ${u.email} deactivated.`);
      fetchUsers();
      setTimeout(() => setSuccessMsg(''), 4000);
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to deactivate user');
    }
  };

  // Filtered list
  const filteredUsers = useMemo(() => {
    return users.filter((u) => {
      const matchSearch =
        (u.email || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
        (u.firstName || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
        (u.lastName || '').toLowerCase().includes(searchQuery.toLowerCase());
      const matchRole = roleFilter === 'ALL' || u.role === roleFilter;
      return matchSearch && matchRole;
    });
  }, [users, searchQuery, roleFilter]);

  const stats = useMemo(() => {
    const total = users.length;
    const active = users.filter((u) => u.isActive !== false).length;
    const admins = users.filter((u) => u.role === 'ADMIN').length;
    return { total, active, admins };
  }, [users]);

  const getRoleBadge = (roleName) => {
    const r = ROLE_OPTIONS.find((opt) => opt.value === roleName) || {
      label: roleName || 'Unknown',
      color: '#94a3b8',
      bg: 'rgba(148,163,184,0.15)',
    };
    return (
      <span
        style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: 6,
          background: r.bg,
          color: r.color,
          border: `1px solid ${r.color}30`,
          borderRadius: 20,
          padding: '2px 10px',
          fontSize: 11,
          fontWeight: 600,
        }}
      >
        <span style={{ width: 6, height: 6, borderRadius: '50%', background: r.color }} />
        {r.label}
      </span>
    );
  };

  return (
    <div style={{ padding: '24px 32px', maxWidth: 1400, margin: '0 auto' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 24, flexWrap: 'wrap', gap: 16 }}>
        <div>
          <h1 style={{ fontSize: 24, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 10 }}>
            <UsersIcon size={24} color="#4f7cff" />
            User & Access Management
          </h1>
          <p style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 4 }}>
            Control user provisioning, role assignments, and platform access permissions.
          </p>
        </div>
        <div style={{ display: 'flex', gap: 12 }}>
          <button
            onClick={fetchUsers}
            disabled={loading}
            className="nf-btn nf-btn-secondary"
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
            Refresh
          </button>
          <button
            onClick={handleOpenAdd}
            className="nf-btn nf-btn-primary"
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <UserPlus size={16} />
            Provision New User
          </button>
        </div>
      </div>

      {/* Feedback banner */}
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
      {error && (
        <div style={{
          padding: '12px 16px', borderRadius: 8, marginBottom: 20,
          background: 'rgba(239,68,68,0.12)', border: '1px solid rgba(239,68,68,0.3)',
          color: '#f87171', fontSize: 13, display: 'flex', alignItems: 'center', gap: 8
        }}>
          <AlertCircle size={16} />
          {error}
        </div>
      )}

      {/* KPI Stats */}
      <div style={{
        display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
        gap: 16, marginBottom: 24
      }}>
        <div className="nf-card" style={{ padding: '16px 20px' }}>
          <div style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>Total Accounts</div>
          <div style={{ fontSize: 28, fontWeight: 700, color: 'var(--text-primary)', marginTop: 4 }}>{stats.total}</div>
        </div>
        <div className="nf-card" style={{ padding: '16px 20px' }}>
          <div style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>Active Users</div>
          <div style={{ fontSize: 28, fontWeight: 700, color: '#34d399', marginTop: 4 }}>{stats.active}</div>
        </div>
        <div className="nf-card" style={{ padding: '16px 20px' }}>
          <div style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>Administrators</div>
          <div style={{ fontSize: 28, fontWeight: 700, color: '#f87171', marginTop: 4 }}>{stats.admins}</div>
        </div>
        <div className="nf-card" style={{ padding: '16px 20px' }}>
          <div style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>Security Policy</div>
          <div style={{ fontSize: 13, color: '#818cf8', fontWeight: 600, marginTop: 10, display: 'flex', alignItems: 'center', gap: 6 }}>
            <Shield size={16} /> RBAC Enforced
          </div>
        </div>
      </div>

      {/* Filter Toolbar */}
      <div className="nf-card" style={{ padding: 16, marginBottom: 20, display: 'flex', gap: 16, alignItems: 'center', flexWrap: 'wrap' }}>
        <div style={{ position: 'relative', flex: 1, minWidth: 240 }}>
          <Search size={16} style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
          <input
            type="text"
            className="nf-input"
            style={{ paddingLeft: 36, width: '100%' }}
            placeholder="Search by name or email..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>Role Filter:</span>
          <select
            className="nf-select"
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            style={{ padding: '8px 12px', fontSize: 13 }}
          >
            <option value="ALL">All Roles</option>
            {ROLE_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Users Table */}
      <div className="nf-card" style={{ overflow: 'hidden' }}>
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: 13 }}>
            <thead>
              <tr style={{ background: 'var(--bg-elevated)', borderBottom: '1px solid var(--border-subtle)', color: 'var(--text-muted)' }}>
                <th style={{ padding: '12px 16px', fontWeight: 600 }}>User</th>
                <th style={{ padding: '12px 16px', fontWeight: 600 }}>Assigned Role</th>
                <th style={{ padding: '12px 16px', fontWeight: 600 }}>Status</th>
                <th style={{ padding: '12px 16px', fontWeight: 600 }}>Registered</th>
                <th style={{ padding: '12px 16px', fontWeight: 600, textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading && users.length === 0 ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
                    <RefreshCw size={24} className="animate-spin" style={{ margin: '0 auto 10px' }} />
                    Loading user registry...
                  </td>
                </tr>
              ) : filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
                    No users matching criteria
                  </td>
                </tr>
              ) : (
                filteredUsers.map((u) => {
                  const isActive = u.isActive !== false;
                  return (
                    <tr
                      key={u.id}
                      style={{ borderBottom: '1px solid var(--border-subtle)', transition: 'background 0.15s' }}
                      className="table-row-hover"
                    >
                      <td style={{ padding: '14px 16px' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                          <div style={{
                            width: 34, height: 34, borderRadius: 8,
                            background: 'linear-gradient(135deg, #4f7cff, #818cf8)',
                            display: 'flex', alignItems: 'center', justifyContent: 'center',
                            fontWeight: 700, color: '#fff', fontSize: 13
                          }}>
                            {(u.firstName?.[0] || u.email?.[0] || 'U').toUpperCase()}
                          </div>
                          <div>
                            <div style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                              {u.firstName || u.lastName ? `${u.firstName || ''} ${u.lastName || ''}`.trim() : 'User'}
                            </div>
                            <div style={{ fontSize: 12, color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: 4 }}>
                              <Mail size={12} />
                              {u.email}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td style={{ padding: '14px 16px' }}>
                        {getRoleBadge(u.role)}
                      </td>
                      <td style={{ padding: '14px 16px' }}>
                        {isActive ? (
                          <span style={{
                            display: 'inline-flex', alignItems: 'center', gap: 4,
                            color: '#34d399', fontSize: 12, fontWeight: 600
                          }}>
                            <CheckCircle2 size={14} /> Active
                          </span>
                        ) : (
                          <span style={{
                            display: 'inline-flex', alignItems: 'center', gap: 4,
                            color: '#f87171', fontSize: 12, fontWeight: 600
                          }}>
                            <XCircle size={14} /> Deactivated
                          </span>
                        )}
                      </td>
                      <td style={{ padding: '14px 16px', color: 'var(--text-muted)', fontSize: 12 }}>
                        {u.createdAt ? new Date(u.createdAt).toLocaleDateString() : 'System Seed'}
                      </td>
                      <td style={{ padding: '14px 16px', textAlign: 'right' }}>
                        <div style={{ display: 'inline-flex', gap: 8 }}>
                          <button
                            onClick={() => handleOpenEdit(u)}
                            className="nf-btn nf-btn-secondary"
                            style={{ padding: '6px 10px', fontSize: 12 }}
                            title="Edit Role & Details"
                          >
                            <Edit3 size={13} /> Edit
                          </button>
                          {isActive && (
                            <button
                              onClick={() => handleDeactivate(u)}
                              className="nf-btn"
                              style={{
                                padding: '6px 10px', fontSize: 12,
                                background: 'rgba(239,68,68,0.1)', color: '#f87171',
                                border: '1px solid rgba(239,68,68,0.2)'
                              }}
                              title="Deactivate Account"
                            >
                              <Trash2 size={13} />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Provision Modal */}
      {showAddModal && (
        <div style={{
          position: 'fixed', inset: 0, zIndex: 60,
          background: 'rgba(0,0,0,0.6)', backdropFilter: 'blur(4px)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 16
        }}>
          <div className="nf-card" style={{ maxWidth: 480, width: '100%', padding: 24, boxShadow: '0 20px 40px rgba(0,0,0,0.5)' }}>
            <h2 style={{ fontSize: 18, fontWeight: 700, marginBottom: 16, color: 'var(--text-primary)' }}>
              Provision New User
            </h2>
            {formError && (
              <div style={{ padding: '10px 14px', borderRadius: 6, background: 'rgba(239,68,68,0.15)', color: '#f87171', fontSize: 12, marginBottom: 16 }}>
                {formError}
              </div>
            )}
            <form onSubmit={handleSaveAdd}>
              <div className="nf-grid-2-responsive" style={{ gap: 12, marginBottom: 12 }}>
                <div>
                  <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 4 }}>First Name</label>
                  <input
                    type="text"
                    required
                    className="nf-input"
                    value={formData.firstName}
                    onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                  />
                </div>
                <div>
                  <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 4 }}>Last Name</label>
                  <input
                    type="text"
                    required
                    className="nf-input"
                    value={formData.lastName}
                    onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                  />
                </div>
              </div>

              <div style={{ marginBottom: 12 }}>
                <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 4 }}>Email Address</label>
                <input
                  type="email"
                  required
                  className="nf-input"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                />
              </div>

              <div style={{ marginBottom: 12 }}>
                <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 4 }}>Initial Password</label>
                <input
                  type="password"
                  placeholder="Defaults to admin123"
                  className="nf-input"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                />
              </div>

              <div style={{ marginBottom: 20 }}>
                <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 4 }}>Platform Role</label>
                <select
                  className="nf-select"
                  style={{ width: '100%', padding: '10px 12px' }}
                  value={formData.role}
                  onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                >
                  {ROLE_OPTIONS.map((r) => (
                    <option key={r.value} value={r.value}>{r.label}</option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
                <button
                  type="button"
                  className="nf-btn nf-btn-secondary"
                  onClick={() => setShowAddModal(false)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={formSubmitting}
                  className="nf-btn nf-btn-primary"
                >
                  {formSubmitting ? 'Creating...' : 'Create User'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      {editingUser && (
        <div style={{
          position: 'fixed', inset: 0, zIndex: 60,
          background: 'rgba(0,0,0,0.6)', backdropFilter: 'blur(4px)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 16
        }}>
          <div className="nf-card" style={{ maxWidth: 480, width: '100%', padding: 24, boxShadow: '0 20px 40px rgba(0,0,0,0.5)' }}>
            <h2 style={{ fontSize: 18, fontWeight: 700, marginBottom: 16, color: 'var(--text-primary)' }}>
              Modify User: {editingUser.email}
            </h2>
            {formError && (
              <div style={{ padding: '10px 14px', borderRadius: 6, background: 'rgba(239,68,68,0.15)', color: '#f87171', fontSize: 12, marginBottom: 16 }}>
                {formError}
              </div>
            )}
            <form onSubmit={handleSaveEdit}>
              <div className="nf-grid-2-responsive" style={{ gap: 12, marginBottom: 12 }}>
                <div>
                  <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 4 }}>First Name</label>
                  <input
                    type="text"
                    required
                    className="nf-input"
                    value={formData.firstName}
                    onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                  />
                </div>
                <div>
                  <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 4 }}>Last Name</label>
                  <input
                    type="text"
                    required
                    className="nf-input"
                    value={formData.lastName}
                    onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                  />
                </div>
              </div>

              <div style={{ marginBottom: 12 }}>
                <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 4 }}>Email Address</label>
                <input
                  type="email"
                  required
                  className="nf-input"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                />
              </div>

              <div style={{ marginBottom: 20 }}>
                <label style={{ fontSize: 12, color: 'var(--text-muted)', display: 'block', marginBottom: 4 }}>Role Permission</label>
                <select
                  className="nf-select"
                  style={{ width: '100%', padding: '10px 12px' }}
                  value={formData.role}
                  onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                >
                  {ROLE_OPTIONS.map((r) => (
                    <option key={r.value} value={r.value}>{r.label}</option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
                <button
                  type="button"
                  className="nf-btn nf-btn-secondary"
                  onClick={() => setEditingUser(null)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={formSubmitting}
                  className="nf-btn nf-btn-primary"
                >
                  {formSubmitting ? 'Saving...' : 'Save Changes'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
