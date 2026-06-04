# NexusFlow — Autonomous AI Supply Chain Risk Intelligence System

A modern, AI-inspired Supply Chain Risk Intelligence Dashboard built with Java 21, Spring Boot 3, React, and PostgreSQL/MySQL.

## 🚀 Quick Start

### Prerequisites
- Java 21
- Node.js 18+
- Maven 3.8+
- PostgreSQL or MySQL

### Database Setup

1. Create database:
```sql
CREATE DATABASE nexusflow;
```

2. Run the setup script:
```bash
cd nexusflow-backend
mysql -u root -p nexusflow < src/main/resources/database-setup.sql
```

### Backend Setup

```bash
cd nexusflow-backend
mvn clean install
mvn spring-boot:run
```

Backend runs on: http://localhost:8080

### Frontend Setup

```bash
cd nexusflow-frontend
npm install
npm start
```

Frontend runs on: http://localhost:3000

### Demo Credentials
- Email: admin@nexusflow.com
- Password: admin123

## 📁 Project Structure

```
nexusflow/
├── nexusflow-backend/          # Spring Boot Backend
│   ├── src/main/java/com/nexusflow/
│   │   ├── controller/         # REST API Controllers
│   │   ├── service/            # Business Logic
│   │   ├── repository/         # Data Access Layer
│   │   ├── entity/             # JPA Entities
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── config/             # Configuration Classes
│   │   ├── exception/          # Exception Handling
│   │   └── util/               # Utility Classes
│   └── src/main/resources/
│       ├── application.properties
│       └── database-setup.sql
│
└── nexusflow-frontend/         # React Frontend
    ├── src/
    │   ├── pages/              # Page Components
    │   ├── services/           # API Service Layer
    │   ├── App.js              # Main App Component
    │   └── index.js            # Entry Point
    └── package.json
```

## 🏗️ Architecture

### Backend Architecture
- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Data access using Spring Data JPA
- **Entity Layer**: JPA entities mapping to database tables
- **DTO Layer**: Data transfer objects for API responses

### Frontend Architecture
- **Pages**: Full-page components for each route
- **Services**: API communication using Axios
- **React Router**: Client-side routing
- **Recharts**: Data visualization
- **TailwindCSS**: Utility-first CSS framework

## 📊 Database Schema

### Tables
1. **users** - User accounts and authentication
2. **suppliers** - Supplier information and performance
3. **shipments** - Shipment tracking data
4. **shipment_routes** - Shipment route details
5. **risk_scores** - AI-calculated risk assessments
6. **delay_predictions** - Delay prediction results
7. **notifications** - User notifications and alerts

### Key Relationships
- One Supplier → Many Shipments
- One Shipment → One Risk Score
- One Shipment → One Delay Prediction
- One User → Many Notifications

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/validate` - Validate JWT token

### Shipments
- `GET /api/shipments` - Get all shipments
- `GET /api/shipments/{id}` - Get shipment by ID
- `POST /api/shipments` - Create shipment
- `PUT /api/shipments/{id}` - Update shipment
- `PATCH /api/shipments/{id}/status` - Update status
- `DELETE /api/shipments/{id}` - Delete shipment

### Suppliers
- `GET /api/suppliers` - Get all suppliers
- `POST /api/suppliers` - Create supplier
- `PUT /api/suppliers/{id}` - Update supplier
- `DELETE /api/suppliers/{id}` - Delete supplier

### Risk Analysis
- `POST /api/risk/calculate/{shipmentId}` - Calculate risk score
- `POST /api/risk/predict/{shipmentId}` - Predict delay
- `POST /api/risk/calculate-all` - Calculate all risks
- `GET /api/risk/high-risk` - Get high-risk shipments
- `GET /api/risk/stats/summary` - Get risk statistics

### Analytics
- `GET /api/analytics/dashboard` - Get dashboard analytics
- `GET /api/analytics/quick-stats` - Get quick statistics
- `GET /api/analytics/charts/*` - Get chart data

## 🎯 Features

### User Management
- User registration and authentication
- Role-based access (Admin, Analyst, Manager)
- JWT token-based security

### Shipment Tracking
- Create and manage shipments
- Track shipment status in real-time
- Search and filter shipments

### Supplier Management
- Add and manage suppliers
- Track supplier performance
- Reliability scoring

### Risk Analysis
- AI-powered risk scoring
- Delay prediction algorithms
- Risk factor analysis

### Analytics Dashboard
- Real-time statistics
- Interactive charts
- Performance metrics

## 🛠️ Tech Stack

### Backend
- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL/PostgreSQL
- JWT (io.jsonwebtoken)
- SpringDoc OpenAPI

### Frontend
- React 18
- React Router 6
- Axios
- Recharts
- TailwindCSS

## 📖 Documentation

### API Documentation
Access Swagger UI at: http://localhost:8080/swagger-ui.html

### Risk Calculation Algorithm

Risk scores are calculated based on:
1. **Supplier Reliability** (30% weight) - Lower reliability = higher risk
2. **Shipment Priority** (20% weight) - Higher priority = higher risk
3. **Days Until Delivery** (15% weight) - Fewer days = higher risk
4. **International Shipment** (15% weight) - Cross-border = higher risk
5. **Cargo Value** (10% weight) - Higher value = higher risk
6. **Historical Delay Rate** (10% weight) - More delays = higher risk

Risk Levels:
- **LOW**: Score ≤ 25
- **MEDIUM**: Score 26-50
- **HIGH**: Score 51-75
- **CRITICAL**: Score > 75

## 🧪 Testing

### Backend Tests
```bash
cd nexusflow-backend
mvn test
```

### API Testing
Import the Postman collection from `nexusflow-backend/postman/` for API testing.

## 📝 License

MIT License - See LICENSE file for details.

## 👥 Team

NexusFlow Development Team

---

Built for the Hackathon 2026