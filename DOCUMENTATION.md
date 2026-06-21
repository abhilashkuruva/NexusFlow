# NexusFlow — Complete System + Codebase Documentation (Interview Ready)


## 1. Project Overview

### 1.1 What is NexusFlow?
NexusFlow is an Autonomous AI Supply Chain Risk Intelligence System designed to help businesses monitor, analyze, and predict risks in their supply chain operations. It provides real-time tracking of shipments, supplier performance analysis, and AI-powered risk predictions.

### 1.2 Real-World Problem
Supply chain disruptions cost businesses billions annually. Companies struggle with:
- Lack of visibility into shipment status
- Inability to predict delays before they occur
- Poor supplier performance tracking
- Reactive rather than proactive risk management

### 1.3 Solution
NexusFlow addresses these challenges by:
- Providing a centralized dashboard for all supply chain operations
- Using AI algorithms to predict potential delays
- Calculating risk scores based on multiple factors
- Sending proactive notifications for high-risk situations

### 1.4 Project Objectives
1. Create a user-friendly interface for supply chain management
2. Implement AI-powered risk analysis
3. Provide real-time analytics and reporting
4. Enable proactive decision-making through predictions

## 2. User Roles

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full access to all features, user management |
| **MANAGER** | Manage shipments and suppliers, view analytics |
| **ANALYST** | View data, run risk analysis, generate reports |

## 3. Module Explanation

### 3.1 User Management Module
- **Purpose**: Handle user authentication and authorization
- **Features**: Registration, login, JWT token management
- **Database**: `users` table

### 3.2 Shipment Tracking Module
- **Purpose**: Track and manage shipments throughout their lifecycle
- **Features**: Create, update, delete shipments; status tracking
- **Database**: `shipments`, `shipment_routes` tables

### 3.3 Supplier Management Module
- **Purpose**: Maintain supplier information and performance metrics
- **Features**: CRUD operations, reliability scoring
- **Database**: `suppliers` table

### 3.4 Risk Analysis Module
- **Purpose**: Calculate risk scores and predict delays
- **Features**: AI-powered algorithms, risk factor analysis
- **Database**: `risk_scores`, `delay_predictions` tables

### 3.5 Analytics Module
- **Purpose**: Provide insights through data visualization
- **Features**: Charts, statistics, trend analysis
- **Database**: Aggregates data from all tables

### 3.6 Notifications Module
- **Purpose**: Alert users about important events
- **Features**: Risk alerts, status updates, predictions
- **Database**: `notifications` table

## 4. Database Schema Explanation

### 4.1 Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'ANALYST', 'MANAGER') DEFAULT 'ANALYST',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
- **Purpose**: Store user account information
- **Primary Key**: `id` - Unique identifier for each user
- **Unique Constraint**: `email` - Ensures no duplicate email addresses

### 4.2 Suppliers Table
```sql
CREATE TABLE suppliers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    country VARCHAR(100),
    reliability_score DECIMAL(3,2) DEFAULT 3.00,
    total_shipments INT DEFAULT 0,
    delayed_shipments INT DEFAULT 0,
    delay_rate DECIMAL(5,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
- **Purpose**: Store supplier information and performance metrics
- **Key Fields**:
  - `reliability_score`: 0-5 scale rating
  - `delay_rate`: Percentage of delayed shipments
  - `total_shipments`, `delayed_shipments`: For calculating delay rate

### 4.3 Shipments Table
```sql
CREATE TABLE shipments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tracking_number VARCHAR(50) UNIQUE NOT NULL,
    supplier_id BIGINT NOT NULL,
    origin_city VARCHAR(100) NOT NULL,
    origin_country VARCHAR(100) NOT NULL,
    destination_city VARCHAR(100) NOT NULL,
    destination_country VARCHAR(100) NOT NULL,
    shipment_date DATE NOT NULL,
    estimated_delivery_date DATE NOT NULL,
    actual_delivery_date DATE,
    status ENUM('PENDING', 'IN_TRANSIT', 'DELAYED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    cargo_type VARCHAR(100),
    weight_kg DECIMAL(10,2),
    value_usd DECIMAL(12,2),
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);
```
- **Purpose**: Track shipment information
- **Foreign Key**: `supplier_id` links to suppliers table
- **Unique Constraint**: `tracking_number` for shipment identification

### 4.4 Risk Scores Table
```sql
CREATE TABLE risk_scores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shipment_id BIGINT UNIQUE NOT NULL,
    risk_level ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    risk_score DECIMAL(5,2) NOT NULL,
    delay_probability DECIMAL(5,2) NOT NULL,
    factors TEXT,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE
);
```
- **Purpose**: Store AI-calculated risk assessments
- **One-to-One**: Each shipment has exactly one risk score
- **Cascade Delete**: Risk score deleted when shipment is deleted

### 4.5 Delay Predictions Table
```sql
CREATE TABLE delay_predictions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shipment_id BIGINT UNIQUE NOT NULL,
    predicted_delay_hours INT DEFAULT 0,
    confidence_score DECIMAL(5,2) DEFAULT 0.00,
    prediction_reason TEXT,
    is_delayed BOOLEAN DEFAULT FALSE,
    predicted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE
);
```
- **Purpose**: Store delay predictions
- **One-to-One**: Each shipment has exactly one prediction
- **Key Fields**:
  - `predicted_delay_hours`: Expected delay duration
  - `confidence_score`: AI confidence in prediction (0-100%)
  - `is_delayed`: Boolean flag for quick filtering

### 4.6 Notifications Table
```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('RISK_ALERT', 'DELAY_WARNING', 'STATUS_UPDATE', 'SYSTEM') NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    related_shipment_id BIGINT,
    related_supplier_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (related_shipment_id) REFERENCES shipments(id),
    FOREIGN KEY (related_supplier_id) REFERENCES suppliers(id)
);
```
- **Purpose**: Store user notifications
- **Foreign Keys**: Links to users, shipments, and suppliers
- **Flexible Design**: Can relate to shipments, suppliers, or both

## 5. Backend Architecture Explanation

### 5.1 Layered Architecture
```
┌─────────────────────────────────────────────────┐
│                  Controller Layer                │
│  (Handles HTTP requests, validates input)        │
├─────────────────────────────────────────────────┤
│                    Service Layer                 │
│  (Business logic, calculations, orchestration)   │
├─────────────────────────────────────────────────┤
│                  Repository Layer                │
│  (Data access, JPA/Hibernate operations)         │
├─────────────────────────────────────────────────┤
│                    Entity Layer                  │
│  (JPA entities mapping to database tables)       │
└─────────────────────────────────────────────────┘
```

### 5.2 Request Flow
1. **Client** sends HTTP request
2. **Controller** receives request, validates input
3. **Service** processes business logic
4. **Repository** fetches/saves data
5. **Database** stores/retrieves data
6. **Response** returned to client

### 5.3 Why This Architecture?
- **Separation of Concerns**: Each layer has a specific responsibility
- **Maintainability**: Easy to modify one layer without affecting others
- **Testability**: Each layer can be tested independently
- **Scalability**: Easy to add new features

## 6. Frontend Architecture Explanation

### 6.1 Component Structure
```
src/
├── pages/           # Full-page components (routes)
├── services/        # API communication layer
├── App.js           # Main application component
└── index.js         # Entry point
```

### 6.2 Data Flow
```
User Action → Page Component → API Service → Backend
                                      ↓
                            Update State → Re-render UI
```

### 6.3 Why React?
- **Component-based**: Reusable UI components
- **Virtual DOM**: Efficient updates
- **Large Ecosystem**: Rich library support
- **Developer Experience**: Easy to debug and maintain

## 7. API Explanation

### 7.1 REST API Design Principles
- **Resource-based URLs**: `/api/shipments`, `/api/suppliers`
- **HTTP Methods**: GET (read), POST (create), PUT (update), DELETE (delete)
- **Stateless**: Each request contains all necessary information
- **JSON Format**: Consistent data exchange format

### 7.2 Request-Response Lifecycle

```
1. Client sends request with JWT token in Authorization header
2. Spring Security validates token
3. Request routed to appropriate Controller
4. Controller validates input data
5. Service layer processes business logic
6. Repository layer accesses database
7. Response serialized to JSON
8. Response sent to client with appropriate HTTP status
```

## 8. DTO Explanation

### 8.1 What are DTOs?
Data Transfer Objects (DTOs) are used to transfer data between layers and in API responses. They:
- Decouple internal entities from external API
- Allow different representations for different use cases
- Provide data validation
- Improve security by controlling exposed fields

### 8.2 Example DTOs
- **AuthRequest**: Login credentials
- **AuthResponse**: Login response with token
- **ShipmentDTO**: Shipment data for API responses
- **AnalyticsDTO**: Aggregated analytics data

## 9. Risk Calculation Logic

### 9.1 Algorithm Overview
```
Total Risk = Supplier Reliability Factor (30%)
           + Priority Factor (20%)
           + Time Factor (15%)
           + International Factor (15%)
           + Value Factor (10%)
           + Delay Rate Factor (10%)
```

### 9.2 Factor Details

**Supplier Reliability (0-30 points)**
- Score 5.0 = 0 points (lowest risk)
- Score 1.0 = 30 points (highest risk)
- Formula: `(5.0 - min(score, 5.0)) / 5.0 * 30`

**Priority (0-20 points)**
- URGENT = 20 points
- HIGH = 15 points
- MEDIUM = 10 points
- LOW = 5 points

**Days Until Delivery (0-15 points)**
- < 3 days = 15 points
- 3-7 days = 10 points
- 7-14 days = 5 points
- > 14 days = 0 points

**International Shipment (0-15 points)**
- Cross-border = 15 points
- Domestic = 0 points

**Cargo Value (0-10 points)**
- > $100,000 = 10 points
- $50,000-$100,000 = 7 points
- $25,000-$50,000 = 4 points
- < $25,000 = 2 points

**Delay Rate (0-10 points)**
- Formula: `delay_rate / 100 * 10`

## 10. Delay Prediction Logic

### 10.1 Algorithm Overview
```
Base Probability = Supplier Delay Rate
Time Adjustment = Based on days until delivery
Status Adjustment = Based on current shipment status
Final Probability = min(Base + Time + Status, 1.0)
```

### 10.2 Confidence Score
Based on:
- Supplier's historical data volume
- Days until delivery
- Data quality

## 11. Error Handling

### 11.1 Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    - Handles ResourceNotFoundException (404)
    - Handles ValidationException (400)
    - Handles AuthenticationException (401)
    - Handles generic exceptions (500)
}
```

### 11.2 Error Response Format
```json
{
    "status": 404,
    "message": "Resource not found",
    "timestamp": "2024-01-01T10:00:00"
}
```

## 12. Security

### 12.1 JWT Authentication
1. User logs in with credentials
2. Server validates and generates JWT token
3. Client stores token in localStorage
4. Client sends token in Authorization header
5. Server validates token on each request

### 12.2 Password Security
- Passwords hashed using BCrypt
- Never stored in plain text
- Salt automatically generated

### 12.3 CORS Configuration
- Allows requests from React frontend (port 3000)
- Configures allowed methods and headers
- Enables credentials for authentication

## 13. Testing Strategy

### 13.1 Unit Tests
- Test individual service methods
- Mock repository dependencies
- Verify business logic

### 13.2 Integration Tests
- Test API endpoints
- Verify database interactions
- Test complete workflows

### 13.3 API Testing
- Use Postman collection
- Test all CRUD operations
- Verify error handling

## 14. Deployment Considerations

### 14.1 Backend Deployment
- Package as JAR file
- Configure production database
- Set environment variables
- Use HTTPS in production

### 14.2 Frontend Deployment
- Build optimized bundle
- Configure API base URL
- Use CDN for static assets
- Enable gzip compression

## 15. Future Enhancements

---

## 16. Authoritative Database Model (Code-Accurate)

### 16.1 Source of truth
The actual schema used by the app is defined in:
- `nexusflow-backend/src/main/resources/database-setup.sql`
- plus sample/seed data in the same file
- plus extra seed/sample rows and a `shipment_summary` view in `nexusflow-backend/src/main/resources/data.sql`

### 16.2 Tables, fields, constraints, and connections

#### 16.2.1 `users`
- **Primary key**: `id BIGINT AUTO_INCREMENT`
- **Unique**: `email VARCHAR(255) NOT NULL UNIQUE`
- **Role**: `role ENUM('ADMIN','ANALYST','MANAGER') NOT NULL DEFAULT 'ANALYST'`
- **Activation**: `is_active BOOLEAN DEFAULT TRUE`
- **Timestamps**: `created_at`, `updated_at` (updated automatically)

Indexes:
- `idx_users_email(email)`
- `idx_users_role(role)`

#### 16.2.2 `suppliers`
- **Primary key**: `id`
- **Core fields**: `name`, `contact_person`, `email`, `phone`, `address`, `country`
- **Reliability**: `reliability_score DECIMAL(4,2) DEFAULT 5.00`
- **Historical counters**: `total_shipments INT DEFAULT 0`, `delayed_shipments INT DEFAULT 0`
- **Derived**: `delay_rate` exists in earlier doc/schema drafts but in the authoritative DB schema only the counters exist; in code `Supplier.getDelayRate()` computes it from those counters.
- **Activation**: `is_active BOOLEAN DEFAULT TRUE`
- **Timestamps**: `created_at`, `updated_at`

Indexes:
- `idx_suppliers_country(country)`
- `idx_suppliers_reliability(reliability_score)`

#### 16.2.3 `shipments`
- **Primary key**: `id`
- **Unique**: `tracking_number VARCHAR(50) NOT NULL UNIQUE`
- **FK**: `supplier_id` → `suppliers(id)` with `ON DELETE CASCADE`
- **Route**: origin/destination cities + countries
- **Timeline**:
  - `shipment_date DATE NOT NULL`
  - `estimated_delivery_date DATE NOT NULL`
  - `actual_delivery_date DATE NULL`
- **Lifecycle**: `status ENUM('PENDING','IN_TRANSIT','DELAYED','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PENDING'`
- **Cargo**: `cargo_type`, `weight_kg`, `value_usd`, `priority ENUM('LOW','MEDIUM','HIGH','URGENT') DEFAULT 'MEDIUM'`, `notes`
- **Timestamps**: `created_at`, `updated_at`

Indexes:
- `idx_shipments_tracking(tracking_number)`
- `idx_shipments_supplier(supplier_id)`
- `idx_shipments_status(status)`
- `idx_shipments_dates(shipment_date, estimated_delivery_date)`

#### 16.2.4 `shipment_routes`
- **Primary key**: `id`
- **FK**: `shipment_id` → `shipments(id)` with `ON DELETE CASCADE`
- **Checkpoint info**: `checkpoint_name`, `checkpoint_city`, `checkpoint_country`
- **Times**: `arrival_time`, `departure_time`
- **Progress**: `status ENUM('PENDING','ARRIVED','DEPARTED','SKIPPED') DEFAULT 'PENDING'`
- `notes`
- `created_at`

Indexes:
- `idx_routes_shipment(shipment_id)`
- `idx_routes_status(status)`

#### 16.2.5 `risk_scores`
- **Primary key**: `id`
- **FK / one-to-one**: `shipment_id BIGINT NOT NULL UNIQUE` → `shipments(id)` with `ON DELETE CASCADE`
- **Risk values**:
  - `risk_level ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL`
  - `risk_score DECIMAL(5,2) NOT NULL`
  - `delay_probability DECIMAL(5,2) NOT NULL`
- `factors TEXT`
- `calculated_at`, `updated_at`

Indexes:
- `idx_risk_shipment(shipment_id)`
- `idx_risk_level(risk_level)`
- `idx_risk_score(risk_score)`

#### 16.2.6 `delay_predictions`
- **Primary key**: `id`
- **FK / one-to-one**: `shipment_id BIGINT NOT NULL UNIQUE` → `shipments(id)` with `ON DELETE CASCADE`
- **Prediction**:
  - `predicted_delay_hours INT`
  - `confidence_score DECIMAL(5,2)`
  - `prediction_reason TEXT`
  - `is_delayed BOOLEAN DEFAULT FALSE`
- `predicted_at`, `updated_at`

Indexes:
- `idx_prediction_shipment(shipment_id)`
- `idx_prediction_delayed(is_delayed)`

#### 16.2.7 `notifications`
- **Primary key**: `id`
- **FK**: `user_id` → `users(id)` with `ON DELETE CASCADE`
- **Content**: `title`, `message`, `type ENUM('INFO','WARNING','ALERT','SUCCESS')`
- **Read tracking**: `is_read BOOLEAN DEFAULT FALSE`
- **Optional relations**:
  - `related_shipment_id` → `shipments(id)` with `ON DELETE SET NULL`
  - `related_supplier_id` → `suppliers(id)` with `ON DELETE SET NULL`
- `created_at`

Indexes:
- `idx_notifications_user(user_id)`
- `idx_notifications_read(is_read)`
- `idx_notifications_type(type)`

#### 16.2.8 `shipment_summary` view (from `data.sql`)
The view joins:
- `shipments` + `suppliers` (inner join)
- `risk_scores` (left join)
- `delay_predictions` (left join)

Selected fields:
- shipment core + supplier core
- `risk_level`, `risk_score`
- `predicted_delay_hours`, `prediction_delayed`

### 16.3 Relationship diagram (cardinalities)
- `users (1) → (N) notifications`
- `suppliers (1) → (N) shipments`
- `shipments (1) → (N) shipment_routes`
- `shipments (1) → (0..1) risk_scores` (enforced as 1-to-1 via UNIQUE shipment_id)
- `shipments (1) → (0..1) delay_predictions` (enforced as 1-to-1 via UNIQUE shipment_id)
- `notifications` optionally references `shipments` and/or `suppliers`

---

## 17. End-to-End Execution Logic (Code Walkthrough)

### 17.1 Authentication flow (frontend ↔ backend)
- Frontend stores JWT in `localStorage`.
- `nexusflow-frontend/src/services/api.js` interceptor:
  - reads `localStorage.getItem('token')`
  - sets `Authorization: Bearer <token>` on every request.

Backend:
- `POST /api/auth/login` → `AuthController.login()`
- `AuthController` calls `UserService.authenticate(email,password)`
- `UserService.authenticate()`:
  - loads user by email
  - rejects if inactive
  - verifies password using `BCryptPasswordEncoder.matches`
  - if it matches as plain-text (legacy fallback), upgrades to BCrypt
- `AuthController` uses `JwtUtil.generateToken(email,userId)`
- Response contains token and user metadata (`AuthResponse`).

### 17.2 Core operational pipeline: Shipment → Risk → Prediction → Analytics → Notifications
1) **Shipment creation/updates**
   - `ShipmentController` exposes CRUD-like endpoints.
   - `ShipmentService.createShipment()`:
     - loads `Supplier` by `supplierId`
     - sets fields + generated tracking number
     - persists `Shipment`.

2) **Risk calculation**
   - `POST /api/risk/calculate/{shipmentId}`
   - `RiskController.calculateRiskScore()` → `RiskService.calculateRiskScore()`

   `RiskService.calculateRiskScore()` steps:
   - loads `Shipment` and `Supplier`
   - factor computations:
     - Supplier reliability: `(5 - min(reliability,5))/5 * 30`
     - Priority factor: URGENT=20, HIGH=15, MEDIUM=10, LOW=5
     - Time factor: days until estimated delivery
       - <3:15, <7:10, <14:5, else:0
     - International factor: origin_country != destination_country ? 15 : 0
     - Cargo value factor: based on `value_usd` thresholds
     - Historical delay rate: `supplier.getDelayRate()/100 * 10`
   - totalRisk = sum of all factors
   - risk level thresholding: <=25 LOW, <=50 MEDIUM, <=75 HIGH else CRITICAL
   - delayProbability currently uses totalRisk directly (implemented as `delayProbability = totalRisk`)
   - persists/upserts `RiskScore` with factors string.

3) **Delay prediction**
   - `POST /api/risk/predict/{shipmentId}`
   - `RiskService.predictDelay()`:
     - baseProbability = supplier.getDelayRate()/100
     - timeAdjustment based on days until estimated delivery
     - statusAdjustment based on shipment status
     - finalProbability = clamp(base+time+status, 0..1)
     - predictedDelayHours:
       - if finalProbability > 0.5:
         - if past delivery: abs(days)*24 + 24
         - else random between 12 and 60-ish (uses Math.random() * 48 + 12)
     - confidenceScore baseline 60; increases with totalShipments and time window
     - builds a human-readable predictionReason
   - upserts `DelayPrediction`.

4) **Analytics**
   - `AnalyticsController.getDashboardAnalytics()` returns `AnalyticsDTO` from `AnalyticsService`.
   - `AnalyticsService.getDashboardAnalytics()` uses repositories to compute:
     - shipment counts by status
     - supplier counts and average reliability
     - risk counts and average risk score
     - predicted delay counts and average confidence/delay hours
     - chart datasets by calling custom repository group/count queries.

5) **Notifications**
   - Notifications are managed via `NotificationService` + `NotificationController`.
   - `NotificationService` responsibilities:
     - create notifications for a user (and optionally link shipment/supplier IDs)
     - read/unread listing and marking read
     - broadcast alerts to users by role (helper method exists)

---

## 18. API Contract Summary (Actual routes in code)
All API endpoints are grouped under `server.servlet.context-path=/api`.

### 18.1 Auth
- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/validate`

### 18.2 Shipments (`ShipmentController`)
- `GET /api/shipments`
- `GET /api/shipments/{id}`
- `GET /api/shipments/tracking/{trackingNumber}`
- `GET /api/shipments/search?query=...`
- `GET /api/shipments/status/{status}`
- `GET /api/shipments/delayed`
- `GET /api/shipments/supplier/{supplierId}`
- `GET /api/shipments/date-range?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`
- `POST /api/shipments` (body: Map with supplierId/origin/destination/dates/cargo/priority/notes)
- `PUT /api/shipments/{id}` (body similar to create)
- `PATCH /api/shipments/{id}/status` (body: {"status": "IN_TRANSIT"...})
- `DELETE /api/shipments/{id}`
- `GET /api/shipments/stats/summary`

### 18.3 Suppliers (`SupplierController`)
- `GET /api/suppliers`
- `GET /api/suppliers/{id}`
- `GET /api/suppliers/search?query=...`
- `GET /api/suppliers/country/{country}`
- `GET /api/suppliers/low-reliability?threshold=...`
- `POST /api/suppliers`
- `PUT /api/suppliers/{id}`
- `DELETE /api/suppliers/{id}` (soft deactivate)
- `GET /api/suppliers/stats/summary`
- `GET /api/suppliers/stats/by-country`

### 18.4 Risk (`RiskController`)
- `POST /api/risk/calculate/{shipmentId}`
- `POST /api/risk/predict/{shipmentId}`
- `POST /api/risk/calculate-all`
- `GET /api/risk/high-risk`
- `GET /api/risk/critical-risk`
- `GET /api/risk/predicted-delays`
- `GET /api/risk/score/{shipmentId}`
- `GET /api/risk/prediction/{shipmentId}`
- `GET /api/risk/stats/summary`
- `GET /api/risk/stats/distribution`

### 18.5 Analytics (`AnalyticsController`)
- `GET /api/analytics/dashboard`
- `GET /api/analytics/quick-stats?userId=...`
- `GET /api/analytics/shipments/summary`
- `GET /api/analytics/suppliers/summary`
- `GET /api/analytics/risk/summary`
- `GET /api/analytics/notifications/unread?userId=...`
- `GET /api/analytics/charts/shipment-status`
- `GET /api/analytics/charts/monthly-shipments`
- `GET /api/analytics/charts/risk-distribution`
- `GET /api/analytics/charts/suppliers-by-country`

### 18.6 Notifications (`NotificationController`)
- `GET /api/notifications/user/{userId}`
- `GET /api/notifications/user/{userId}/unread`
- `GET /api/notifications/user/{userId}/recent?limit=...`
- `PATCH /api/notifications/{id}/read`
- `PATCH /api/notifications/user/{userId}/read-all`
- `DELETE /api/notifications/{id}`
- `GET /api/notifications/user/{userId}/unread-count`
- `POST /api/notifications`

---

## 19. Interview Q&A (Maximum Expansion in this session)
### 19.1 Shortlist: 100 Questions (Q + Answer)
> NOTE: Full 500–1000 Q&A in one single document can exceed practical editing limits in this environment. The following section provides a large set of realistic, code-based interview questions with answers, grounded in the actual implementation.

1) **What is NexusFlow?**
   - A supply chain risk intelligence dashboard using risk scoring + delay predictions.

2) **Why does the project exist?**
   - Disruptions are costly; teams need proactive visibility and predictions.

3) **What are the core entities?**
   - `User`, `Supplier`, `Shipment`, `ShipmentRoute`, `RiskScore`, `DelayPrediction`, `Notification`.

4) **How do shipments relate to suppliers?**
   - `shipments.supplier_id` FK → `suppliers.id` (1 supplier → many shipments).

5) **How is risk score linked to shipments?**
   - `risk_scores.shipment_id` is UNIQUE FK → `shipments(id)` (1-to-1).

6) **How is delay prediction linked?**
   - `delay_predictions.shipment_id` is UNIQUE FK → `shipments(id)` (1-to-1).

7) **What is the risk scoring algorithm implemented?**
   - Weighted sum of 6 factors (supplier reliability, priority, time window, international lane, cargo value, historical delay rate).

8) **Where is risk logic implemented?**
   - `RiskService.calculateRiskScore()`.

9) **How is risk level determined?**
   - totalRisk ≤25 LOW, ≤50 MEDIUM, ≤75 HIGH, else CRITICAL.

10) **Where is delay prediction logic implemented?**
   - `RiskService.predictDelay()`.

11) **How does prediction probability get adjusted?**
   - baseProbability (supplier delay rate) + timeAdjustment + statusAdjustment (then clamp to ≤1).

12) **How is confidence score computed?**
   - baseline 60, increased by supplier shipment count and time until delivery.

13) **What endpoints trigger risk scoring/prediction?**
   - `POST /api/risk/calculate/{shipmentId}` and `POST /api/risk/predict/{shipmentId}`.

14) **How do you calculate historical delay rate for a supplier?**
   - `Supplier.getDelayRate()` = delayedShipments/totalShipments*100.

15) **Where is Supplier reliability used?**
   - `RiskService.calculateRiskScore()` uses `supplier.getReliabilityScore()`.

16) **How does the UI fetch data?**
   - Frontend uses Axios wrapper `nexusflow-frontend/src/services/api.js`.

17) **How is JWT attached to requests?**
   - Axios request interceptor adds `Authorization` header from `localStorage.token`.

18) **What does JWT contain?**
   - claims: email and userId; subject=email.

19) **Where is JWT generation code?**
   - `JwtUtil.generateToken()`.

20) **Where is JWT verification used?**
   - `JwtUtil.validateToken()`; backend currently does not enforce roles strictly for endpoints.

21) **What is `SecurityConfig` doing?**
   - CORS configured; CSRF disabled; stateless sessions.

22) **Does the code restrict endpoints by role?**
   - Current config uses `anyRequest().permitAll()`, so role-based restrictions are not enforced at Spring Security level.

23) **Which layer owns the business logic?**
   - Service layer (e.g., `RiskService`, `ShipmentService`).

24) **Which layer owns persistence?**
   - Repository interfaces (e.g., `ShipmentRepository`, `RiskScoreRepository`).

25) **Which layer maps data to DB tables?**
   - Entity classes annotated with JPA (`@Entity`, `@Table`).

26) **What is the purpose of DTOs in this project?**
   - Conceptually to decouple API payloads; however many controllers still return `Map<String,Object>`.

27) **Where are DTOs used more strongly?**
   - `AnalyticsController` returns `AnalyticsDTO`.

28) **Why might controllers return `Map` instead of DTOs?**
   - To quickly construct response shapes for the frontend without strict DTO wiring.

29) **What is `AnalyticsDTO`?**
   - A structured object holding dashboard metrics and chart datasets.

30) **Where do chart datasets come from?**
   - `AnalyticsService` calls repository group/count queries.

31) **How is shipment status chart computed?**
   - `ShipmentRepository.countByStatusGrouped()`.

32) **How is monthly shipment chart computed?**
   - `ShipmentRepository.getMonthlyShipmentCount()`.

33) **How is risk distribution chart computed?**
   - `RiskScoreRepository.countByRiskLevelGrouped()`.

34) **How is suppliers-by-country chart computed?**
   - `SupplierRepository.countSuppliersByCountry()`.

35) **What is the purpose of `calculate-all` risk endpoint?**
   - Recomputes risk scores and predictions for all shipments.

36) **How does `calculate-all` handle failures?**
   - Each shipment is processed inside try/catch; errors are logged and loop continues.

37) **Where is that implemented?**
   - `RiskService.calculateAllRisks()`.

38) **What is `ShipmentService.searchShipments()` based on?**
   - Repository query matching tracking number and origin/destination city.

39) **How are delayed shipments detected?**
   - Query checks status and dates (estimated < today, actualDeliveryDate null, status in transit/pending).

40) **What does “shipment status update” do?**
   - `ShipmentService.updateStatus()` sets status and sets actualDeliveryDate when status becomes DELIVERED.

41) **How does shipment creation generate tracking numbers?**
   - `ShipmentService.generateTrackingNumber()` uses year + random 3-digit suffix.

42) **What is potential issue with tracking number generation?**
   - Randomness could collide; DB UNIQUE constraint can surface conflicts.

43) **How are shipments represented in DB?**
   - `shipments` table columns mirror entity fields.

44) **How are shipment routes represented?**
   - `shipment_routes` table stores checkpoints per shipment.

45) **Is `ShipmentRoute` currently exposed by API?**
   - Entities/repository exist, but controllers shown do not implement route CRUD.

46) **How are notifications created?**
   - `NotificationService.createNotification()` assigns user + title/message/type.

47) **How are unread notifications fetched?**
   - `NotificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc()`.

48) **Where is “mark all as read” implemented?**
   - `NotificationService.markAllAsRead()`.

49) **Does `NotificationService` broadcast by role?**
   - Yes: `sendAlertToUsers(title,message,type,roles)`.

50) **Where are database seed/users ensured?**
   - `database-setup.sql` inserts users with bcrypt hashes.

51) **What about runtime seeding in Java?**
   - `StartupConfig` (CommandLineRunner) and/or `NexusFlowApplication` ensure default admin exists.

52) **Why might login fail even if user exists?**
   - bcrypt mismatch or password hash not aligned; debug diagnostics exist in StartupConfig.

53) **How does bcrypt mismatch get detected?**
   - StartupConfig prints whether stored password looks like bcrypt.

54) **What is `GlobalExceptionHandler` for?**
   - Centralizes error → HTTP response mapping.

55) **What is the frontend login page doing?**
   - Collects email/password and calls `authAPI.login()`.

56) **What stores the JWT token?**
   - Frontend code stores `token` in `localStorage` (in `Login.js`).

57) **What routes exist on frontend?**
   - Login, Dashboard, Shipments, Suppliers, Risk, Analytics.

58) **Where is API base URL defined?**
   - `API_BASE_URL = 'http://localhost:8080/api'` in `api.js`.

59) **What is the shape of shipment responses?**
   - ShipmentController converts Shipment entity to map including supplierName, status, priority, etc.

60) **What is the shape of risk responses?**
   - RiskController returns convertRiskToMap: riskLevel, riskScore, delayProbability, factors.

61) **What is the shape of prediction responses?**
   - convertPredictionToMap: predictedDelayHours, confidenceScore, predictionReason, isDelayed.

62) **How do you interpret riskScore vs delayProbability?**
   - Currently delayProbability is set to totalRisk in code.

63) **Is this statistically “correct”?**
   - It’s a heuristic; for a real ML system delayProbability should be probabilistically calibrated.

64) **What are the main Spring components?**
   - Controllers, Services, Repositories, Entities, Config.

65) **Why use repositories?**
   - Leverage Spring Data JPA and query derivation / JPQL.

66) **Why use transaction annotations?**
   - Ensure consistency of write operations.

67) **What’s the main risk of missing DTO mapping?**
   - May expose internal structure; but here responses are largely `Map`.

68) **How does the view `shipment_summary` help?**
   - Provides pre-joined view for convenience and possibly faster dashboard queries.

69) **How do you update supplier reliability?**
   - SupplierService has `updateReliabilityScore(id,newScore)` (no controller route shown in snippet).

70) **How are suppliers deactivated?**
   - SupplierController `DELETE` calls SupplierService.deactivateSupplier() which sets `isActive=false`.

71) **Does deactivation affect shipments?**
   - Shipments remain, since supplier FK exists, but deletion is soft (no cascade delete).

72) **What indexes exist to support queries?**
   - Shipment status/dates/tracking and supplier reliability/country and notifications user/read/type.

73) **How does analytics compute predicted delays?**
   - counts `delay_predictions.is_delayed=true`.

74) **How do risk stats compute averages?**
   - RiskScoreRepository.getAverageRiskScore().

75) **How do delay stats compute average predicted delay hours?**
   - DelayPredictionRepository.getAveragePredictedDelayHours().

76) **Where is monthly chart month name mapping?**
   - AnalyticsService monthNames array with Jan..Dec.

77) **Is `shipment_routes` used in risk logic?**
   - Not currently; RiskService does not query ShipmentRoute.

78) **What is `Shipment.isDelayed()` method doing?**
   - Uses estimatedDeliveryDate vs now and status; not currently central to prediction.

79) **Where does frontend show “delayed shipments”?**
   - Shipments page calls shipmentAPI.getDelayed().

80) **Where does “high risk” come from?**
   - Risk page calls riskAPI.getHighRisk() and getCriticalRisk().

81) **Where does “predicted delays” come from?**
   - Risk page calls riskAPI.getPredictedDelays().

82) **How are notifications displayed?**
   - Dashboard likely calls analytics/unread endpoints and notifications API.

83) **What is `AnalyticsController.getQuickStats()`?**
   - Returns totalShipments/delayedShipments/highRiskShipments/unreadNotifications/predictedDelays.

84) **What default userId is used?**
   - Controller defaults to 1L when missing.

85) **What is a key inconsistency in docs vs schema?**
   - Earlier docs referenced `delay_rate` column in suppliers; authoritative schema uses counters and computes delay rate in code.

86) **Why should that be documented?**
   - Interviewers test whether you know the real implementation.

87) **What security weakness exists?**
   - SecurityConfig currently permits all endpoints; JWT is not enforced on API routes.

88) **How could you fix role-based access?**
   - Add Spring Security authentication filter + authorization rules per endpoint.

89) **How could you add a JWT filter?**
   - Create OncePerRequestFilter to validate Bearer token and set SecurityContext.

90) **Why disable formLogin and httpBasic?**
   - Because auth is stateless JWT, not session-based.

91) **What does CORS configuration do?**
   - Allows React origins and sets allowed headers/methods.

92) **Where are CORS settings applied?**
   - In SecurityConfig.corsConfigurationSource().

93) **What is `spring.sql.init.mode=always`?**
   - Re-runs seed scripts each startup (good for demos, bad for production).

94) **Where does SQL seeding live?**
   - `spring.sql.init.data-locations=classpath:data.sql`.

95) **Why might `database-setup.sql` and `data.sql` both exist?**
   - `database-setup.sql` is corrected schema; `data.sql` contains sample data and a view.

96) **What should be the “single source of truth” in production?**
   - A single migration tool (Flyway/Liquibase) and one schema definition.

97) **What’s the biggest technical debt item here?**
   - Controller responses use `Map` rather than strict DTOs; security enforcement is incomplete.

98) **What’s a good next improvement?**
   - Enforce JWT + roles, and refactor controllers to return DTOs.

99) **What’s the value proposition of the risk algorithm?**
   - Simple, explainable heuristic mapping business factors to scores.

100) **Why are the algorithms “AI-inspired” not ML?**
   - They are deterministic heuristics with randomization for delay hours.

---

## 20. What to Improve If Interviewers Push Further
- Replace heuristic risk/delay logic with trained ML model (calibration + features).
- Enforce JWT auth and role authorization in Spring Security.
- Refactor controller responses to DTOs consistently.
- Add persistence of route checkpoints and incorporate route-level disruption signals.
- Add integration tests for endpoints.

---

This document is now expanded with code-accurate schema + full workflow + a large Q&A starter set.



### 15.1 Short-term
- Email notifications
- Export reports to PDF/Excel
- Advanced search filters
- Mobile responsive improvements

### 15.2 Long-term
- Machine learning model improvements
- Real-time tracking integration
- Multi-language support
- API rate limiting
- Audit logging

---

This documentation provides a comprehensive guide to understanding the NexusFlow system architecture, design decisions, and implementation details.