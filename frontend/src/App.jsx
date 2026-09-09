import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login.jsx';
import Dashboard from './pages/Dashboard.jsx';
import Shipments from './pages/Shipments.jsx';
import Suppliers from './pages/Suppliers.jsx';
import Risk from './pages/Risk.jsx';
import Analytics from './pages/Analytics.jsx';
import Inventory from './pages/Inventory.jsx';
import Notifications from './pages/Notifications.jsx';
import Profile from './pages/Profile.jsx';
import Users from './pages/Users.jsx';
import SupplierPortal from './pages/SupplierPortal.jsx';
import AppShell from './components/layout/AppShell.jsx';
import { useAuth } from './hooks/useAuth.js';

/** Redirects to /login if not authenticated */
function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return children;
}

/** Redirects to /dashboard if the user's role is not in allowedRoles */
function RoleRoute({ allowedRoles, children }) {
  const { role } = useAuth();
  if (!allowedRoles.includes(role)) return <Navigate to="/dashboard" replace />;
  return children;
}

/** Supplier role gets redirected to their dedicated portal */
function SupplierRedirect({ children }) {
  const { isSupplier } = useAuth();
  if (isSupplier) return <Navigate to="/portal" replace />;
  return children;
}

function App() {
  return (
    <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <Routes>
        <Route path="/login" element={<Login />} />

        {/* Authenticated shell */}
        <Route element={
          <ProtectedRoute>
            <AppShell />
          </ProtectedRoute>
        }>
          <Route path="/dashboard" element={
            <SupplierRedirect><Dashboard /></SupplierRedirect>
          } />
          <Route path="/shipments" element={
            <SupplierRedirect><Shipments /></SupplierRedirect>
          } />
          <Route path="/suppliers" element={
            <RoleRoute allowedRoles={['ADMIN', 'SUPPLY_MANAGER', 'LOGISTICS_MANAGER']}>
              <Suppliers />
            </RoleRoute>
          } />
          <Route path="/inventory" element={
            <RoleRoute allowedRoles={['ADMIN', 'SUPPLY_MANAGER', 'LOGISTICS_MANAGER', 'ANALYST']}>
              <Inventory />
            </RoleRoute>
          } />
          <Route path="/risk" element={<Risk />} />
          <Route path="/analytics" element={<Analytics />} />
          <Route path="/notifications" element={<Notifications />} />
          <Route path="/profile" element={<Profile />} />

          {/* ADMIN only */}
          <Route path="/users" element={
            <RoleRoute allowedRoles={['ADMIN']}>
              <Users />
            </RoleRoute>
          } />

          {/* Supplier portal — for SUPPLIER role */}
          <Route path="/portal" element={
            <RoleRoute allowedRoles={['SUPPLIER', 'ADMIN']}>
              <SupplierPortal />
            </RoleRoute>
          } />
        </Route>

        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="*" element={<Navigate to="/dashboard" />} />
      </Routes>
    </Router>
  );
}

export default App;
