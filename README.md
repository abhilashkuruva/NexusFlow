# ⚡ NexusFlow: Autonomous AI Supply Chain Risk Intelligence System

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2-blue.svg)](https://reactjs.org/)
[![Vite](https://img.shields.io/badge/Vite-7.3-purple.svg)](https://vitejs.dev/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**NexusFlow** is an enterprise-grade Supply Chain Risk Intelligence Control Tower. It continuously ingests, tracks, and analyzes real-time shipment movements, supplier resilience metrics, inventory fluctuations, weather disruptions, and route complexities. By combining explainable heuristic AI algorithms with dynamic data visualization, NexusFlow enables supply chain executives and logistics operators to detect, quantify, predict, and mitigate operational disruptions before they impact business continuity.

---

## 🏛️ System Architecture

```text
                             ┌──────────────────────────────┐
                             │       OPERATOR / USER        │
                             └──────────────┬───────────────┘
                                            │
                                            ▼
                             ┌──────────────────────────────┐
                             │    React 18 SPA (Port 3001)  │
                             │  Vite 7 • Tailwind • Leaflet │
                             └──────────────┬───────────────┘
                                            │ HTTP / REST & JWT
                                            ▼
                             ┌──────────────────────────────┐
                             │  Spring Boot 3 (Port 8081)   │
                             │   Spring Security • JJWT     │
                             └──────┬───────────────┬───────┘
                                    │               │
                  ┌─────────────────┴────┐     ┌────┴─────────────────┐
                  ▼                      ▼     ▼                      ▼
           Shipment Engine        Risk AI Model   Inventory Monitor   WebSocket Alert
                  │                      │     │                      │
                  └─────────────────┬────┴─────┴──────────────────────┘
                                    │
                                    ▼
                             ┌──────────────────────────────┐
                             │  Persistence Layer (JPA)     │
                             │ H2 (dev) / PostgreSQL (prod) │
                             └──────────────────────────────┘
```

---

## 📂 Project Structure

```text
NexusFlow/
│
├── .github/                      # CI/CD workflows and automated pipelines
├── backend/                      # Java 21 / Spring Boot 3.2.5 REST API backend
│   ├── src/main/java/            # Spring controllers, services, repositories, entities
│   ├── src/main/resources/       # application.properties, schema DDL, seed data
│   ├── src/test/java/            # JUnit 5 & Mockito test suites (14 tests)
│   ├── Dockerfile                # Multi-stage Eclipse Temurin 21 Docker build
│   ├── pom.xml                   # Maven dependencies and build configuration
│   └── README.md                 # Dedicated backend documentation
│
├── frontend/                     # React 18 / Vite 7 Control Tower frontend
│   ├── src/                      # Components, pages, hooks, services, styles
│   ├── public/                   # Static public assets
│   ├── tests/                    # Playwright smoke test suite
│   ├── Dockerfile                # Multi-stage Node 20 / Nginx Alpine Docker build
│   ├── nginx.conf                # Production reverse proxy configuration
│   ├── package.json              # Frontend dependencies and npm scripts
│   ├── vite.config.js            # Vite configuration and server setup
│   └── README.md                 # Dedicated frontend documentation
│
├── database/                     # Normalized database schema & seed scripts
│   ├── schema/                   # database-setup.sql (complete DDL)
│   ├── seed/                     # data.sql (demo dataset)
│   └── README.md                 # Database setup and migration guide
│
├── docs/                         # Central technical documentation
│   ├── ARCHITECTURE.md           # Architectural deep-dives and system specs
│   ├── diagrams/                 # Architecture, auth, risk, and deployment diagrams
│   ├── troubleshooting/          # Database and runtime troubleshooting runbooks
│   └── README.md                 # Documentation index
│
├── scripts/                      # Developer automation scripts
│   ├── start-all.bat             # Unified Windows launcher
│   ├── start-backend.bat         # Backend launcher
│   └── start-frontend.bat        # Frontend launcher
│
├── .env.example                  # Environment configuration template
├── .gitignore                    # Production Git ignore rules
├── docker-compose.yml            # Multi-container orchestration (DB + API + Web)
├── start-all.bat                 # Root convenience launcher
└── README.md                     # Root project documentation
```

---

## 🚀 Quick Start

### 1. One-Click Windows Launch
Double-click `start-all.bat` in the repository root. It starts both the Spring Boot backend on port `8081` and the React Vite frontend on port `3001`.

### 2. Manual Start

#### Backend (Spring Boot — Port 8081)
```bash
cd backend
mvn spring-boot:run
```

#### Frontend (React / Vite — Port 3001)
```bash
cd frontend
npm install
npm run dev
```

### 3. Docker Compose (Full Stack with PostgreSQL)
```bash
docker-compose up --build -d
```

## ☁️ Deploy with Neon, Render, and Vercel

The repository includes [`render.yaml`](./render.yaml) for the Spring Boot API and
[`frontend/vercel.json`](./frontend/vercel.json) for Vercel's React SPA fallback.

1. Create a Neon PostgreSQL project and copy its host, database name, user, and password.
2. In Render, create a **Blueprint** from this repository. Render will use `render.yaml`
   to create the `nexusflow-api` web service. Set `DB_HOST`, `DB_USERNAME`, and
   `DB_PASSWORD` from Neon, then deploy. `DB_SSLMODE=require` is already configured.
3. Confirm the API is healthy at
   `https://<render-service>.onrender.com/api/auth/health`.
4. Create a Vercel project from this repository with **Root Directory** set to `frontend`.
   Set `VITE_API_URL` to `https://<render-service>.onrender.com/api`, then deploy.
5. Copy the final Vercel URL into Render's `CORS_ALLOWED_ORIGINS` value (without a
   trailing slash) and redeploy the API.

Do not commit Neon credentials or JWT secrets. Render's generated `JWT_SECRET` should
be retained for the lifetime of the deployment; changing it invalidates existing tokens.

---

## 🔑 Demo Credentials

| Role | Email | Password | Privileges |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@nexusflow.com` | `admin123` | Full system control, user management, system audit |
| **Supply Manager** | `manager@nexusflow.com` | `admin123` | Supplier risk scoring, inventory monitoring, shipments |
| **Logistics Manager** | `logistics@nexusflow.com` | `admin123` | Fleet dispatch, route analysis, live shipment tracking |
| **Risk Analyst** | `analyst@nexusflow.com` | `admin123` | Scenario simulations, risk events, predictive models |
| **Supplier** | `supplier@nexusflow.com` | `admin123` | Vendor portal, assigned shipments, compliance view |

---

## 🌐 Application Endpoints

- **Frontend Control Tower**: [http://localhost:3001](http://localhost:3001)
- **Backend API Base**: [http://localhost:8081/api](http://localhost:8081/api)
- **Swagger / OpenAPI Documentation**: [http://localhost:8081/api/swagger-ui.html](http://localhost:8081/api/swagger-ui.html)
- **H2 Database Console**: [http://localhost:8081/api/h2-console](http://localhost:8081/api/h2-console) *(JDBC URL: `jdbc:h2:mem:nexusflow_db`, Username: `sa`, Password: blank)*

---

## 🧪 Verification & Testing

### Backend Test Suite
```bash
cd backend
mvn clean test
```
All **14 unit and service tests** pass covering delay prediction heuristics, inventory reordering, risk evaluation, supplier scoring, and JWT authentication.

### Frontend Production Build
```bash
cd frontend
npm run build
```
Vite compiles and minifies the complete React bundle with zero TypeScript/JavaScript compilation errors.

---

## 📚 Complete Documentation

All architectural specifications, risk algorithms, and runbooks are available in the `docs/` folder:
- **[Documentation Index](docs/README.md)**: Central directory index and overview.
- **[System Architecture Guide](docs/ARCHITECTURE.md)**: Comprehensive deep dive into the 3-tier architecture, domain design, and risk engine algorithms.
- **[Architecture Diagrams](docs/diagrams/)**: High-resolution workflow, security, and deployment architecture diagrams.
- **[Database Troubleshooting](docs/troubleshooting/DATABASE_TROUBLESHOOTING.md)**: Database setup, connection profiles, and resolution steps.

---

## 📄 License
This project is licensed under the MIT License.
