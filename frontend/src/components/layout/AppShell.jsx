import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Topbar from './Topbar';

const AppShell = () => {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <div className="nf-app-shell">
      <Topbar onSidebarToggle={() => setMobileOpen(v => !v)} />

      <Sidebar
        mobileOpen={mobileOpen}
        onMobileClose={() => setMobileOpen(false)}
      />

      {/* Main content — automatically offsets on desktop (paddingLeft 260px) and full-width on mobile/tablet */}
      <main className="nf-main-content">
        <div className="nf-page">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default AppShell;
