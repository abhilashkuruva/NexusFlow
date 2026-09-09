import React, { useEffect, useMemo, useState } from 'react';
import { inventoryAPI } from '../services/api';
import { AlertTriangle, Clock, Package2, RefreshCw, Search, Warehouse, ShieldCheck } from 'lucide-react';
import { useAuth } from '../hooks/useAuth.js';

function Inventory() {
  const { canManageInventory, role } = useAuth();
  const [items, setItems] = useState([]);
  const [stats, setStats] = useState(null);
  const [lowStockItems, setLowStockItems] = useState([]);
  const [reorderItems, setReorderItems] = useState([]);
  const [overstockItems, setOverstockItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const fetchData = async () => {
    try {
      setLoading(true);
      setError('');

      const [itemsRes, statsRes, lowStockRes, reorderRes, overstockRes] = await Promise.all([
        inventoryAPI.getAll(),
        inventoryAPI.getStats(),
        inventoryAPI.getLowStock(),
        inventoryAPI.getReorderNeeded(),
        inventoryAPI.getOverstock(),
      ]);

      setItems(Array.isArray(itemsRes?.data) ? itemsRes.data : []);
      setStats(statsRes?.data ?? null);
      setLowStockItems(Array.isArray(lowStockRes?.data) ? lowStockRes.data : []);
      setReorderItems(Array.isArray(reorderRes?.data) ? reorderRes.data : []);
      setOverstockItems(Array.isArray(overstockRes?.data) ? overstockRes.data : []);
    } catch (e) {
      setError(e?.message || 'Failed to load inventory intelligence');
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSearch = async () => {
    const q = searchQuery.trim();
    try {
      setLoading(true);
      setError('');

      if (!q) {
        await fetchData();
        return;
      }

      const res = await inventoryAPI.search(q);
      setItems(Array.isArray(res?.data) ? res.data : []);
    } catch (e) {
      setError(e?.message || 'Search failed');
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const metrics = useMemo(() => {
    const totalItems = Number(stats?.totalItems ?? items.length ?? 0);
    const lowStock = Number(stats?.lowStockItems ?? lowStockItems.length ?? 0);
    const totalValue = Number(stats?.totalValue ?? 0);
    const totalUnits = items.reduce((sum, item) => sum + Number(item?.availableQuantity ?? 0), 0);
    const reservedUnits = items.reduce((sum, item) => sum + Number(item?.reservedQuantity ?? 0), 0);
    const coverage = totalItems > 0 ? Math.max(0, Math.round((1 - lowStock / totalItems) * 100)) : 0;

    return {
      totalItems,
      lowStock,
      totalValue,
      totalUnits,
      reservedUnits,
      coverage,
    };
  }, [stats, items, lowStockItems.length]);

  const formatCurrency = (value) =>
    new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0,
    }).format(Number(value ?? 0));

  const formatDate = (value) => {
    if (!value) return 'Not checked';
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? 'Not checked' : date.toLocaleString();
  };

  const getStockBadge = (item) => {
    const status = (item?.stockStatus || '').toUpperCase();
    if (status === 'OVERSTOCK') return { label: 'Overstock', className: 'nf-badge-medium' };
    if (status === 'REORDER' || status === 'LOW_STOCK') return { label: 'Reorder', className: 'nf-badge-critical' };
    if ((item?.stockRiskLevel || '').toUpperCase() === 'HIGH' || (item?.stockRiskLevel || '').toUpperCase() === 'CRITICAL') {
      return { label: item.stockRiskLevel, className: 'nf-badge-high' };
    }
    return { label: 'Healthy', className: 'nf-badge-low' };
  };

  const visibleItems = items ?? [];

  return (
    <div className="nf-page">
      <div className="flex flex-col gap-4 mb-4">
        <div className="flex justify-between items-center flex-wrap gap-2">
          <div className="flex flex-col gap-1">
            <h2 className="text-xl font-semibold" style={{ color: 'var(--text-primary)' }}>
              Inventory Intelligence
            </h2>
            <p className="text-sm" style={{ color: 'var(--text-muted)' }}>
              Stock position, shortage probability, and reorder signals across warehouses.
            </p>
          </div>
          <div>
            {canManageInventory ? (
              <span style={{ fontSize: 11, color: '#34d399', background: 'rgba(16,185,129,0.12)', padding: '4px 10px', borderRadius: 20, border: '1px solid rgba(16,185,129,0.25)', fontWeight: 600, display: 'inline-flex', alignItems: 'center', gap: 5 }}>
                <ShieldCheck size={12} /> Management Authority ({role})
              </span>
            ) : (
              <span style={{ fontSize: 11, color: '#818cf8', background: 'rgba(79,124,255,0.12)', padding: '4px 10px', borderRadius: 20, border: '1px solid rgba(79,124,255,0.25)', fontWeight: 600 }}>
                Read-Only Telemetry ({role})
              </span>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
          <div className="nf-card nf-kpi nf-kpi-blue">
            <div className="kpi-value">{metrics.totalItems}</div>
            <div className="kpi-label">Inventory Records</div>
          </div>
          <div className="nf-card nf-kpi nf-kpi-orange">
            <div className="kpi-value">{metrics.lowStock}</div>
            <div className="kpi-label">Low Stock Items</div>
          </div>
          <div className="nf-card nf-kpi nf-kpi-red">
            <div className="kpi-value">{metrics.totalUnits.toLocaleString()}</div>
            <div className="kpi-label">Available Units</div>
          </div>
          <div className="nf-card nf-kpi nf-kpi-green">
            <div className="kpi-value">{formatCurrency(metrics.totalValue)}</div>
            <div className="kpi-label">Inventory Value</div>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="nf-card">
            <div className="text-xs uppercase tracking-wide" style={{ color: 'var(--text-muted)' }}>
              Coverage
            </div>
            <div className="text-2xl font-semibold mt-1" style={{ color: 'var(--text-primary)' }}>
              {metrics.coverage}%
            </div>
            <div className="text-sm mt-1" style={{ color: 'var(--text-muted)' }}>
              Share of inventory above low-stock thresholds.
            </div>
          </div>
          <div className="nf-card">
            <div className="text-xs uppercase tracking-wide" style={{ color: 'var(--text-muted)' }}>
              Reserved Units
            </div>
            <div className="text-2xl font-semibold mt-1" style={{ color: 'var(--text-primary)' }}>
              {metrics.reservedUnits.toLocaleString()}
            </div>
            <div className="text-sm mt-1" style={{ color: 'var(--text-muted)' }}>
              Stock already allocated to active demand.
            </div>
          </div>
          <div className="nf-card">
            <div className="text-xs uppercase tracking-wide" style={{ color: 'var(--text-muted)' }}>
              Reorder Queue
            </div>
            <div className="text-2xl font-semibold mt-1" style={{ color: 'var(--text-primary)' }}>
              {reorderItems.length}
            </div>
            <div className="text-sm mt-1" style={{ color: 'var(--text-muted)' }}>
              Items at or below the reorder point.
            </div>
          </div>
        </div>
      </div>

      {error && (
        <div
          className="nf-card"
          style={{
            background: 'rgba(239,68,68,0.08)',
            border: '1px solid rgba(239,68,68,0.25)',
            color: '#f87171',
          }}
        >
          {error}
        </div>
      )}

      <div className="nf-card mb-4">
        <div className="flex flex-col sm:flex-row gap-3 items-end">
          <div className="flex-1">
            <label className="nf-label" htmlFor="inventory-search">
              Search inventory
            </label>
            <input
              id="inventory-search"
              type="text"
              placeholder="Search product, SKU, or warehouse..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="nf-input"
            />
          </div>

          <div className="flex gap-2">
            <button type="button" onClick={handleSearch} className="nf-btn nf-btn-primary">
              <Search size={14} /> Search
            </button>
            <button type="button" onClick={fetchData} className="nf-btn nf-btn-secondary">
              <RefreshCw size={14} /> Refresh
            </button>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-[2.2fr_1fr] gap-4">
        <div className="nf-card">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-lg font-semibold" style={{ color: 'var(--text-primary)' }}>
                Stock Position
              </h3>
              <p className="text-sm" style={{ color: 'var(--text-muted)' }}>
                Real-time view of product availability and warehouse exposure.
              </p>
            </div>
            <div className="flex items-center gap-2 text-sm" style={{ color: 'var(--text-muted)' }}>
              <Warehouse size={14} />
              {visibleItems.length} rows
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="nf-table">
              <thead>
                <tr>
                  <th>Product</th>
                  <th>SKU</th>
                  <th>Warehouse</th>
                  <th>Available</th>
                  <th>Reserved</th>
                  <th>Risk</th>
                  <th>Last Check</th>
                </tr>
              </thead>

              <tbody>
                {loading ? (
                  Array.from({ length: 6 }).map((_, i) => (
                    <tr key={i}>
                      {Array.from({ length: 7 }).map((__, j) => (
                        <td key={j}>
                          <div
                            className="nf-skeleton"
                            style={{ height: 18, borderRadius: 4, width: j === 0 ? '130px' : '80%' }}
                          />
                        </td>
                      ))}
                    </tr>
                  ))
                ) : visibleItems.length === 0 ? (
                  <tr>
                    <td colSpan={7} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                      No inventory items found.
                    </td>
                  </tr>
                ) : (
                  visibleItems.map((item, idx) => {
                    const badge = getStockBadge(item);
                    return (
                      <tr key={item?.id ?? idx} className="hover:bg-elevated">
                        <td>
                          <div style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                            {item?.productName ?? 'Unspecified product'}
                          </div>
                          <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                            {item?.category ?? 'Uncategorized'}
                          </div>
                        </td>
                        <td style={{ fontFamily: 'monospace', color: 'var(--accent-cyan)' }}>
                          {item?.sku ?? 'N/A'}
                        </td>
                        <td>
                          <div style={{ color: 'var(--text-primary)', fontWeight: 500 }}>
                            {item?.warehouseName ?? 'Unknown warehouse'}
                          </div>
                          <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                            {item?.warehouseLocation ?? item?.warehouseCity ?? item?.warehouseCountry ?? 'No location'}
                          </div>
                        </td>
                        <td style={{ color: 'var(--text-primary)', fontWeight: 600 }}>
                          {Number(item?.availableQuantity ?? 0).toLocaleString()}
                        </td>
                        <td style={{ color: 'var(--text-secondary)' }}>
                          {Number(item?.reservedQuantity ?? 0).toLocaleString()}
                        </td>
                        <td>
                          <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                            <span className={`nf-badge ${badge.className}`}>{badge.label}</span>
                            <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                              Status: {item?.stockStatus ?? 'HEALTHY'} | Risk: {item?.stockRiskLevel ?? 'LOW'}
                            </span>
                          </div>
                        </td>
                        <td style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                          {formatDate(item?.lastStockCheck)}
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>

        <div className="flex flex-col gap-4">
          <div className="nf-card">
            <div className="flex items-center gap-2 mb-3">
              <AlertTriangle size={16} color="var(--accent-orange)" />
              <h3 className="text-lg font-semibold" style={{ color: 'var(--text-primary)' }}>
                Reorder Watchlist
              </h3>
            </div>
            <div className="space-y-3">
              {(reorderItems.slice(0, 5)).length === 0 ? (
                <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No reorder-needed items right now.</div>
              ) : (
                reorderItems.slice(0, 5).map((item) => (
                  <div key={item?.id} className="nf-ai-recommendation">
                    <div className="flex items-center justify-between gap-3">
                      <div>
                        <div style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                          {item?.productName ?? 'Unspecified product'}
                        </div>
                        <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                          {item?.warehouseName ?? 'Unknown warehouse'}
                        </div>
                      </div>
                      <span className="nf-badge nf-badge-medium">
                        {Number(item?.availableQuantity ?? 0)} units
                      </span>
                    </div>
                    <div style={{ marginTop: 8, display: 'flex', alignItems: 'center', gap: 8 }}>
                      <Clock size={12} color="var(--text-muted)" />
                      <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                        Reorder point: {item?.reorderPoint ?? 'N/A'}
                      </span>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="nf-card">
            <div className="flex items-center gap-2 mb-3">
              <Package2 size={16} color="var(--accent-blue)" />
              <h3 className="text-lg font-semibold" style={{ color: 'var(--text-primary)' }}>
                Low Stock Hotspots
              </h3>
            </div>
            <div className="space-y-3">
              {lowStockItems.length === 0 ? (
                <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No low stock items detected.</div>
              ) : (
                lowStockItems.slice(0, 5).map((item) => (
                  <div key={item?.id} style={{ paddingBottom: 12, borderBottom: '1px solid var(--border-subtle)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12 }}>
                      <div>
                        <div style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                          {item?.productName ?? 'Unspecified product'}
                        </div>
                        <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                          {item?.warehouseName ?? 'Unknown warehouse'}
                        </div>
                      </div>
                      <span className="nf-badge nf-badge-critical">
                        {Number(item?.availableQuantity ?? 0)} left
                      </span>
                    </div>
                    <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 6 }}>
                      Reorder point: {item?.reorderPoint ?? 'N/A'} | Status: {item?.stockStatus ?? 'HEALTHY'}
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="nf-card">
            <div className="flex items-center gap-2 mb-3">
              <Package2 size={16} color="var(--accent-purple)" />
              <h3 className="text-lg font-semibold" style={{ color: 'var(--text-primary)' }}>
                Overstock Signals
              </h3>
            </div>
            <div className="space-y-3">
              {overstockItems.length === 0 ? (
                <div style={{ color: 'var(--text-muted)', fontSize: 13 }}>No overstock items detected.</div>
              ) : (
                overstockItems.slice(0, 5).map((item) => (
                  <div key={item?.id} className="nf-ai-recommendation">
                    <div style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                      {item?.productName ?? 'Unspecified product'}
                    </div>
                    <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                      {item?.warehouseName ?? 'Unknown warehouse'} | {Number(item?.availableQuantity ?? 0).toLocaleString()} units
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Inventory;
