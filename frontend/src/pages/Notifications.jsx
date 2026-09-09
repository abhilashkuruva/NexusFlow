import React, { useEffect, useState, useMemo } from 'react';
import {
  Bell, CheckCircle2, AlertTriangle, ShieldAlert, Trash2,
  RefreshCw, CheckCheck, Clock, Search, Filter, ArrowRight,
  Package, Building2, ExternalLink
} from 'lucide-react';
import { Link } from 'react-router-dom';
import { notificationAPI } from '../services/api';

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionSuccess, setActionSuccess] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [activeTab, setActiveTab] = useState('ALL'); // ALL, UNREAD, CRITICAL, DELAYS

  const user = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem('user') || '{}');
    } catch {
      return {};
    }
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      setError('');
      const uid = user.id || null;

      const [listRes, unreadRes] = await Promise.all([
        uid ? notificationAPI.getByUser(uid) : Promise.resolve({ data: [] }),
        uid ? notificationAPI.getUnreadCount(uid) : Promise.resolve({ data: { count: 0 } })
      ]);

      const rawList = listRes?.data;
      setNotifications(Array.isArray(rawList) ? rawList : []);

      const rawUnread = unreadRes?.data;
      const count =
        rawUnread && typeof rawUnread === 'object' && 'count' in rawUnread
          ? rawUnread.count
          : typeof rawUnread === 'number'
            ? rawUnread
            : 0;
      setUnreadCount(count ?? 0);
    } catch (e) {
      console.error('Failed to load notifications:', e);
      setError(e?.response?.data?.message || e?.message || 'Failed to load notifications');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleMarkOneRead = async (id) => {
    try {
      await notificationAPI.markAsRead(id);
      setNotifications(prev =>
        prev.map(n => (n.id === id ? { ...n, isRead: true } : n))
      );
      setUnreadCount(c => Math.max(0, c - 1));
    } catch (e) {
      console.error('Failed to mark read:', e);
    }
  };

  const handleMarkAllRead = async () => {
    if (!user.id) return;
    try {
      await notificationAPI.markAllAsRead(user.id);
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
      setUnreadCount(0);
      setActionSuccess('All notifications marked as read.');
      setTimeout(() => setActionSuccess(''), 3000);
    } catch (e) {
      console.error('Failed to mark all as read:', e);
      setError('Failed to mark all as read.');
    }
  };

  const handleDelete = async (id) => {
    try {
      await notificationAPI.delete(id);
      setNotifications(prev => {
        const target = prev.find(n => n.id === id);
        if (target && !target.isRead) {
          setUnreadCount(c => Math.max(0, c - 1));
        }
        return prev.filter(n => n.id !== id);
      });
    } catch (e) {
      console.error('Failed to delete notification:', e);
    }
  };

  const filteredNotifications = useMemo(() => {
    return notifications.filter(n => {
      const title = (n.title || '').toLowerCase();
      const message = (n.message || '').toLowerCase();
      const q = searchQuery.toLowerCase();
      const matchesSearch = !q || title.includes(q) || message.includes(q);

      if (!matchesSearch) return false;

      if (activeTab === 'UNREAD') return !n.isRead;
      if (activeTab === 'CRITICAL') {
        return (n.type === 'CRITICAL' || title.includes('critical') || title.includes('risk') || message.includes('critical'));
      }
      if (activeTab === 'DELAYS') {
        return (title.includes('delay') || message.includes('delay') || title.includes('transit'));
      }
      return true;
    });
  }, [notifications, searchQuery, activeTab]);

  const getNotificationIcon = (n) => {
    const title = (n.title || '').toLowerCase();
    const type = (n.type || '').toUpperCase();

    if (type === 'CRITICAL' || title.includes('critical') || title.includes('risk')) {
      return { icon: ShieldAlert, color: '#ef4444', bg: 'rgba(239,68,68,0.12)' };
    }
    if (title.includes('delay') || title.includes('transit')) {
      return { icon: Clock, color: '#f59e0b', bg: 'rgba(245,158,11,0.12)' };
    }
    if (title.includes('stock') || title.includes('inventory')) {
      return { icon: Package, color: '#3b82f6', bg: 'rgba(59,130,246,0.12)' };
    }
    if (title.includes('supplier')) {
      return { icon: Building2, color: '#8b5cf6', bg: 'rgba(139,92,246,0.12)' };
    }
    return { icon: Bell, color: 'var(--accent-red)', bg: 'rgba(220,38,38,0.12)' };
  };

  return (
    <div className="nf-page">
      {/* Page Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24, flexWrap: 'wrap', gap: 16 }}>
        <div>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: 6, background: 'rgba(220,38,38,0.1)', color: 'var(--accent-red)', padding: '3px 10px', borderRadius: 20, fontSize: 11, fontWeight: 700, marginBottom: 8 }}>
            <Bell size={12} /> System Telemetry & Operational Alerts
          </div>
          <h1 style={{ fontSize: 24, fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>
            Notifications & Early Warnings
          </h1>
          <p style={{ fontSize: 13, color: 'var(--text-muted)', margin: '4px 0 0' }}>
            Real-time automated alerts for shipment deviations, supplier SLA breaches, and critical warehouse inventory depletion.
          </p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          {unreadCount > 0 && (
            <button
              onClick={handleMarkAllRead}
              className="nf-btn nf-btn-secondary"
              style={{ display: 'flex', alignItems: 'center', gap: 6 }}
            >
              <CheckCheck size={14} /> Mark All as Read
            </button>
          )}

          <button
            onClick={fetchNotifications}
            className="nf-btn nf-btn-primary"
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <RefreshCw size={14} className={loading ? 'animate-spin' : ''} /> Refresh
          </button>
        </div>
      </div>

      {actionSuccess && (
        <div className="nf-card" style={{ background: 'rgba(16,185,129,0.08)', border: '1px solid rgba(16,185,129,0.25)', color: '#34d399', marginBottom: 20, display: 'flex', alignItems: 'center', gap: 10 }}>
          <CheckCircle2 size={18} />
          <span>{actionSuccess}</span>
        </div>
      )}

      {error && (
        <div className="nf-card" style={{ background: 'rgba(239,68,68,0.08)', border: '1px solid rgba(239,68,68,0.25)', color: '#f87171', marginBottom: 20, display: 'flex', alignItems: 'center', gap: 10 }}>
          <AlertTriangle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* Filter and Search Bar */}
      <div className="nf-card" style={{ padding: '16px 20px', marginBottom: 20 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 16 }}>
          {/* Tabs */}
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
            {[
              { id: 'ALL', label: 'All Alerts', count: notifications.length },
              { id: 'UNREAD', label: 'Unread', count: unreadCount },
              { id: 'CRITICAL', label: 'Critical Risk', count: notifications.filter(n => (n.title || '').toLowerCase().includes('critical') || (n.title || '').toLowerCase().includes('risk')).length },
              { id: 'DELAYS', label: 'Transit Delays', count: notifications.filter(n => (n.title || '').toLowerCase().includes('delay')).length },
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                style={{
                  background: activeTab === tab.id ? 'var(--accent-red)' : 'var(--bg-card)',
                  color: activeTab === tab.id ? '#ffffff' : 'var(--text-secondary)',
                  border: activeTab === tab.id ? '1px solid var(--accent-red)' : '1px solid var(--border-subtle)',
                  padding: '6px 14px',
                  borderRadius: 10,
                  fontSize: 12,
                  fontWeight: 600,
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: 6,
                  transition: 'all 0.15s ease'
                }}
              >
                {tab.label}
                <span style={{
                  background: activeTab === tab.id ? 'rgba(255,255,255,0.25)' : 'var(--bg-elevated)',
                  color: activeTab === tab.id ? '#ffffff' : 'var(--text-muted)',
                  padding: '1px 6px',
                  borderRadius: 10,
                  fontSize: 10.5,
                  fontWeight: 700
                }}>
                  {tab.count}
                </span>
              </button>
            ))}
          </div>

          {/* Search Input */}
          <div style={{ position: 'relative', minWidth: 260 }}>
            <Search size={15} style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            <input
              type="text"
              placeholder="Search notifications..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              className="nf-input"
              style={{ paddingLeft: 34, width: '100%' }}
            />
          </div>
        </div>
      </div>

      {/* Notifications Stream */}
      {loading ? (
        <div style={{ display: 'grid', gap: 12 }}>
          {[1, 2, 3, 4, 5].map(i => (
            <div key={i} className="nf-skeleton" style={{ height: 80, borderRadius: 14 }} />
          ))}
        </div>
      ) : filteredNotifications.length === 0 ? (
        <div className="nf-card" style={{ textAlign: 'center', padding: '60px 20px' }}>
          <div style={{
            width: 56, height: 56, borderRadius: '50%',
            background: 'rgba(220,38,38,0.08)',
            display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
            marginBottom: 16
          }}>
            <CheckCircle2 size={28} color="var(--accent-red)" />
          </div>
          <h3 style={{ fontSize: 16, fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 6px' }}>
            No notifications found
          </h3>
          <p style={{ fontSize: 13, color: 'var(--text-muted)', maxWidth: 420, margin: '0 auto' }}>
            {searchQuery || activeTab !== 'ALL'
              ? 'No matching notifications found for the current search filter.'
              : 'Your supply chain pipeline has zero active alerts. All shipments and suppliers are operating within nominal SLA limits.'}
          </p>
        </div>
      ) : (
        <div style={{ display: 'grid', gap: 12 }}>
          {filteredNotifications.map((n, idx) => {
            const iconConfig = getNotificationIcon(n);
            const Icon = iconConfig.icon;
            const isUnread = !n.isRead;

            return (
              <div
                key={n.id || idx}
                className="nf-card nf-fade-in"
                style={{
                  padding: '16px 20px',
                  display: 'flex',
                  alignItems: 'flex-start',
                  gap: 16,
                  position: 'relative',
                  borderLeft: isUnread ? `4px solid ${iconConfig.color}` : '1px solid var(--border-subtle)',
                  background: isUnread ? 'var(--bg-card-hover)' : 'var(--bg-card)',
                  transition: 'all 0.2s ease'
                }}
              >
                {/* Status Indicator Icon */}
                <div style={{
                  width: 38, height: 38, borderRadius: 10,
                  background: iconConfig.bg,
                  border: `1px solid ${iconConfig.color}30`,
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  flexShrink: 0
                }}>
                  <Icon size={18} color={iconConfig.color} />
                </div>

                {/* Body Content */}
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap', marginBottom: 4 }}>
                    <h4 style={{ fontSize: 14, fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>
                      {n.title || 'System Notification'}
                    </h4>

                    {isUnread && (
                      <span style={{
                        background: 'rgba(220,38,38,0.15)',
                        color: 'var(--accent-red)',
                        padding: '1px 8px',
                        borderRadius: 12,
                        fontSize: 10,
                        fontWeight: 700
                      }}>
                        NEW
                      </span>
                    )}

                    {n.type && (
                      <span style={{
                        background: 'var(--bg-elevated)',
                        color: 'var(--text-muted)',
                        padding: '1px 8px',
                        borderRadius: 12,
                        fontSize: 10.5,
                        fontWeight: 600,
                        textTransform: 'uppercase'
                      }}>
                        {n.type}
                      </span>
                    )}
                  </div>

                  <p style={{ fontSize: 13, color: 'var(--text-secondary)', margin: '0 0 10px', lineHeight: 1.45 }}>
                    {n.message || 'No additional details provided.'}
                  </p>

                  {/* Actions & Timestamps Footer */}
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 12 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 12, fontSize: 11.5, color: 'var(--text-muted)' }}>
                      <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                        <Clock size={12} />
                        {n.createdAt ? new Date(n.createdAt).toLocaleString() : 'Recent'}
                      </span>

                      {n.relatedShipmentId && (
                        <Link
                          to="/shipments"
                          style={{
                            display: 'flex', alignItems: 'center', gap: 4,
                            color: 'var(--accent-blue)', textDecoration: 'none', fontWeight: 600
                          }}
                        >
                          <Package size={12} /> View Shipment <ExternalLink size={10} />
                        </Link>
                      )}

                      {n.relatedSupplierId && (
                        <Link
                          to="/suppliers"
                          style={{
                            display: 'flex', alignItems: 'center', gap: 4,
                            color: 'var(--accent-purple)', textDecoration: 'none', fontWeight: 600
                          }}
                        >
                          <Building2 size={12} /> View Supplier <ExternalLink size={10} />
                        </Link>
                      )}
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      {isUnread && (
                        <button
                          onClick={() => handleMarkOneRead(n.id)}
                          style={{
                            background: 'transparent',
                            border: 'none',
                            color: 'var(--accent-blue)',
                            fontSize: 12,
                            fontWeight: 600,
                            cursor: 'pointer',
                            display: 'flex',
                            alignItems: 'center',
                            gap: 4
                          }}
                          title="Mark as read"
                        >
                          <CheckCircle2 size={13} /> Mark Read
                        </button>
                      )}

                      <button
                        onClick={() => handleDelete(n.id)}
                        style={{
                          background: 'transparent',
                          border: 'none',
                          color: 'var(--text-muted)',
                          fontSize: 12,
                          cursor: 'pointer',
                          display: 'flex',
                          alignItems: 'center',
                          gap: 4,
                          padding: 4
                        }}
                        title="Dismiss notification"
                      >
                        <Trash2 size={13} />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
