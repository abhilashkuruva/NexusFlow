# ⚙️ NexusFlow Backend (Spring Boot 3.2.5 / Java 21)

Production-grade enterprise RESTful API and real-time WebSocket backend for the **NexusFlow Autonomous AI Supply Chain Risk Intelligence System**.

---

## 🛠️ Technology Stack

- **Java Version**: Java 21 LTS
- **Framework**: Spring Boot 3.2.5
- **Persistence**: Spring Data JPA / Hibernate
- **Security**: Spring Security 6 with stateless JWT (`io.jsonwebtoken:jjwt:0.12.5`)
- **Database Support**: In-memory H2 (Development) / PostgreSQL 15+ (Production)
- **API Documentation**: SpringDoc OpenAPI 2.5.0 (Swagger 3)
- **Build Tool**: Apache Maven 3.9+

---

## 🏛️ Package Architecture (`com.nexusflow`)

```text
src/main/java/com/nexusflow/
├── NexusFlowApplication.java     # Spring Boot application entry point
│
├── config/                       # Spring & security configuration
│   ├── AppConfig.java            # Password encoder, cors, rest templates
│   ├── DemoDataSeeder.java       # Initial data seeder for entities
│   ├── OpenApiConfig.java        # Swagger / OpenAPI v3 documentation config
│   ├── SecurityConfig.java       # SecurityFilterChain, CSRF, session policy
│   ├── StartupConfig.java        # CommandLineRunner diagnostics and demo bootstrap
│   └── WebSocketConfig.java      # STOMP / SockJS WebSocket endpoint config
│
├── controller/                   # REST Controllers (/api context path)
│   ├── AuthController.java       # /api/auth (login, register, session)
│   ├── DashboardKpiController.java # /api/dashboard/kpis
│   ├── IntelligenceController.java # /api/intelligence (AI insights)
│   ├── InventoryController.java  # /api/inventory
│   ├── NotificationController.java # /api/notifications
│   ├── PredictionController.java # /api/predictions (delay forecasting)
│   ├── RecommendationController.java # /api/recommendations (actionable AI)
│   ├── RiskController.java       # /api/risk (simulation, aggregate scoring)
│   ├── RiskEventController.java  # /api/risk-events
│   ├── ShipmentController.java   # /api/shipments (CRUD, live tracking)
│   ├── SupplierController.java   # /api/suppliers (risk assessment)
│   └── UserController.java       # /api/users
│
├── dto/                          # Data Transfer Objects
├── entity/                       # JPA Entities (Shipment, Supplier, User, Inventory, etc.)
├── exception/                    # GlobalExceptionHandler & custom exceptions
├── repository/                   # Spring Data JPA repositories
├── security/                     # JwtAuthenticationFilter & UserPrincipal
├── service/                      # Business interfaces & AI engines
│   ├── SupplierRiskEngine.java   # Multi-factor supplier risk calculation
│   ├── DelayPredictionModel.java # Shipment delay prediction heuristics
│   └── ...
├── serviceImpl/                  # Service implementation classes
└── util/                         # JwtUtil token generator & validator
```

---

## 🚀 Running the Backend

### Prerequisites
- JDK 21 installed (`java -version` returns 21+)
- Maven installed (or use Maven wrapper/IDE)

### Commands
```bash
# Run with default 'dev' profile (H2 in-memory database)
mvn spring-boot:run

# Run with 'prod' profile (PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Run all unit and integration tests
mvn clean test

# Package to executable JAR
mvn clean package -DskipTests
java -jar target/nexusflow-backend-1.0.0.jar
```

---

## 🔑 Default Accounts (Bootstrapped on Startup)

| Email | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `admin@nexusflow.com` | `admin123` | `ADMIN` | System administrator with full privileges |
| `manager@nexusflow.com` | `admin123` | `SUPPLY_MANAGER` | Supply chain operations manager |
| `logistics@nexusflow.com` | `admin123` | `LOGISTICS_MANAGER` | Fleet & logistics coordinator |
| `analyst@nexusflow.com` | `admin123` | `ANALYST` | Risk and predictive analytics officer |
| `supplier@nexusflow.com` | `admin123` | `SUPPLIER` | External vendor portal access |

---

## 📖 API Documentation

Once the backend is running, access Swagger UI at:
- **URL**: [http://localhost:8081/api/swagger-ui.html](http://localhost:8081/api/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8081/api-docs](http://localhost:8081/api-docs)
