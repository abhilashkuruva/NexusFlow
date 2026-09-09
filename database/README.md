# 🗄️ NexusFlow Database Architecture

This directory houses the schema DDL and initial demo seed data for the **NexusFlow Autonomous AI Supply Chain Risk Intelligence System**.

---

## 📁 Directory Structure

```text
database/
├── schema/
│   └── database-setup.sql    # Complete DDL: tables, indexes, constraints, views
├── seed/
│   └── data.sql              # Initial demo seed dataset
└── README.md                 # Database management guide
```

---

## 🏗️ Supported Databases

### 1. In-Memory H2 Database (Default `dev` Profile)
- **URL**: `jdbc:h2:mem:nexusflow_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- **Driver**: `org.h2.Driver`
- **Username**: `sa`
- **Password**: *(empty)*
- **Console**: Accessible at `http://localhost:8081/api/h2-console`
- **Behavior**: Schema is automatically created and populated at startup by Hibernate and Spring Boot seeder services.

### 2. PostgreSQL 15+ (`prod` Profile / Docker)
- **Database**: `nexusflow`
- **User**: `nexusflow`
- **Password**: `nexusflow_password`
- **Port**: `5432`
- **URL**: `jdbc:postgresql://localhost:5432/nexusflow`

---

## 📜 Core Schema Entity Relationships

| Entity / Table | Description |
| :--- | :--- |
| `users` | User credentials, roles (`ADMIN`, `SUPPLY_MANAGER`, `LOGISTICS_MANAGER`, `ANALYST`, `SUPPLIER`), active status |
| `suppliers` | Supplier master with risk score, on-time delivery rate, tier, status |
| `shipments` | Active shipment tracking, origin, destination, ETA, cargo value, coordinates |
| `inventory` | Stock monitoring, warehouse location, reorder point, criticality |
| `risk_events` | AI-detected risk events with severity, confidence, affected entities |
| `recommendations` | Autonomous AI mitigation strategies (reroute, buffer stock, expedite) |
| `predictions` | ML/heuristic delivery delay probabilities and impact scores |
| `notifications` | System alerts and user notifications |

---

## 🚀 Manual Initialization

To manually initialize PostgreSQL using the CLI:

```bash
psql -U nexusflow -d nexusflow -f database/schema/database-setup.sql
psql -U nexusflow -d nexusflow -f database/seed/data.sql
```
