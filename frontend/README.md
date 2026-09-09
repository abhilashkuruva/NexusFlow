# 💻 NexusFlow Frontend (React 18 / Vite 7 / TailwindCSS)

Modern, ultra-responsive Supply Chain Risk Intelligence Control Tower UI built with **React 18**, **Vite 7**, and **TailwindCSS**.

---

## 🛠️ Technology Stack

- **Core**: React 18.2 (Hooks, Functional Components, Context API)
- **Build Tool**: Vite 7.3 with native ESM compilation
- **Routing**: React Router DOM v6
- **Styling**: TailwindCSS 3.4 with custom dark cyber-intelligence theme
- **Icons**: Lucide React
- **Data Visualization**: Recharts & Chart.js with dynamic canvas rendering
- **Maps**: Leaflet (interactive route & shipment tracking)
- **Real-Time**: STOMP / SockJS WebSocket subscriber client
- **Testing**: Playwright End-to-End & Smoke test suite

---

## 📁 Source Architecture (`src/`)

```text
src/
├── App.jsx                       # Master router with protected routes & role access
├── index.jsx                     # React DOM root entry point
├── index.css                     # Tailwind design tokens, scrollbars, and keyframes
│
├── components/layout/
│   ├── AppShell.jsx              # Main application shell with sidebar & topbar
│   ├── Sidebar.jsx               # Navigation drawer with active link highlighting
│   └── Topbar.jsx                # User profile dropdown, alerts badge, breadcrumbs
│
├── hooks/
│   └── useAuth.js                # Authentication state & JWT decode hook
│
├── pages/
│   ├── Analytics.jsx             # Risk trend visualizer, route delay distribution
│   ├── Dashboard.jsx             # Executive KPI control tower & active alerts
│   ├── Inventory.jsx             # Stock monitor, critical shortage indicator
│   ├── Login.jsx                 # Secure login page with role demo presets
│   ├── Notifications.jsx         # Real-time alert notifications list
│   ├── Profile.jsx               # User profile, role permissions, preferences
│   ├── Risk.jsx                  # AI risk simulator & scenario stress tester
│   ├── Shipments.jsx             # Shipment management, live tracking map, filters
│   ├── SupplierPortal.jsx        # Vendor-specific portal view
│   ├── Suppliers.jsx             # Supplier directory & resilience scorecard
│   └── Users.jsx                 # User management table (Admin only)
│
├── services/
│   └── api.js                    # Central Axios client with JWT request/response interceptors
```

---

## 🚀 Running the Frontend

### Prerequisites
- Node.js 18+ or 20+
- npm 9+

### Commands
```bash
# Install dependencies
npm install

# Start development server on port 3001
npm run dev

# Build for production (outputs to dist/)
npm run build

# Preview production build locally
npm run preview

# Run Playwright smoke tests
npx playwright test
```

---

## 🌐 Environment Variables

Set in `.env` (or pass via environment):

```env
VITE_API_BASE_URL=http://localhost:8081/api
```
