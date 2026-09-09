# 📚 NexusFlow Technical Documentation

Welcome to the documentation repository for **NexusFlow: Autonomous AI Supply Chain Risk Intelligence System**.

---

## 📂 Documentation Directory Index

```text
docs/
├── ARCHITECTURE.md          # Complete system architecture, risk models, and technical guide
├── diagrams/                # Architectural flow & deployment diagrams
│   ├── diag_system_architecture.png
│   ├── diag_auth_flow.png
│   ├── diag_risk_flow.png
│   ├── diag_inventory_flow.png
│   └── diag_deployment.png
└── troubleshooting/         # Database and operational troubleshooting runbooks
    └── DATABASE_TROUBLESHOOTING.md
```

---

## 🏛️ 1. Architecture & Design

- **[System Architecture & Master Guide](ARCHITECTURE.md)**: Deep dive into the 3-tier architecture, Spring Boot 3 backend design, React control tower, domain models, and explainable AI heuristic models.

---

## 📊 2. Visual Architecture Diagrams (`diagrams/`)

| Diagram | Description |
| :--- | :--- |
| **[`diag_system_architecture.png`](diagrams/diag_system_architecture.png)** | End-to-end full stack architecture (Client, API Gateway/Security, Domain Services, Persistence, External Feeds) |
| **[`diag_auth_flow.png`](diagrams/diag_auth_flow.png)** | JWT authentication lifecycle, token validation filter, security context propagation |
| **[`diag_risk_flow.png`](diagrams/diag_risk_flow.png)** | AI Risk engine calculation pipeline: weather, route, supplier risk, multi-factor aggregation |
| **[`diag_inventory_flow.png`](diagrams/diag_inventory_flow.png)** | Inventory stock monitoring, reorder threshold triggers, automated alerting |
| **[`diag_deployment.png`](diagrams/diag_deployment.png)** | Containerized deployment topology with Docker Compose, Nginx, Spring Boot, and PostgreSQL |

---

## 🔧 3. Operational Troubleshooting

- **[Database Troubleshooting Runbook](troubleshooting/DATABASE_TROUBLESHOOTING.md)**: Common connectivity, migration, H2 console, and PostgreSQL configuration resolutions.
