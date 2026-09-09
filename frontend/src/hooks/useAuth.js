/**
 * useAuth — central hook for reading authenticated user from localStorage.
 * Returns the user object and role helpers.
 */
import { useMemo } from 'react';

export function useAuth() {
  const user = useMemo(() => {
    try {
      const stored = localStorage.getItem('user');
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  }, []);

  const token = localStorage.getItem('token');
  const isAuthenticated = !!token && !!user;
  const role = user?.role ?? null;

  return {
    user,
    token,
    isAuthenticated,
    role,
    isAdmin: role === 'ADMIN',
    isSupplyManager: role === 'SUPPLY_MANAGER',
    isLogisticsManager: role === 'LOGISTICS_MANAGER',
    isSupplier: role === 'SUPPLIER',
    isAnalyst: role === 'ANALYST',
    // Can perform write actions (create/edit/delete)
    canWrite: role === 'ADMIN' || role === 'SUPPLY_MANAGER' || role === 'LOGISTICS_MANAGER',
    // Can manage suppliers
    canManageSuppliers: role === 'ADMIN' || role === 'SUPPLY_MANAGER',
    // Can manage shipments
    canManageShipments: role === 'ADMIN' || role === 'LOGISTICS_MANAGER' || role === 'SUPPLY_MANAGER',
    // Can manage inventory
    canManageInventory: role === 'ADMIN' || role === 'LOGISTICS_MANAGER',
    // Can manage users
    canManageUsers: role === 'ADMIN',
  };
}

export default useAuth;
