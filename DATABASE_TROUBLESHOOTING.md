# NexusFlow Database Troubleshooting Guide

## Issues Fixed

The following issues were identified and fixed:

### 1. **SQL Script Not Executing**
- **Problem**: `database-setup.sql` was not being executed by Spring Boot
- **Solution**: Updated `application.properties` to explicitly point to `database-setup.sql`

### 2. **Database Creation Conflict**
- **Problem**: Script tried to `DROP DATABASE` and `CREATE DATABASE`, which conflicts with Spring Boot's connection
- **Solution**: Changed to drop tables only (not database), since database is created by connection URL

### 3. **Conflicting Data Files**
- **Problem**: Both `data.sql` and `database-setup.sql` existed with different data
- **Solution**: Disabled `data.sql` and use only `database-setup.sql` for complete data

### 4. **Hibernate DDL Conflict**
- **Problem**: `ddl-auto=update` could modify tables created by SQL script
- **Solution**: Changed to `ddl-auto=validate` to only validate schema

## How to Verify Database Setup

### Step 1: Stop the Application
If running, stop the Spring Boot application (Ctrl+C in terminal)

### Step 2: Clean the Database
Connect to MySQL and drop the existing database to start fresh:

```sql
-- Connect to MySQL
mysql -u root -p

-- Drop existing database
DROP DATABASE IF EXISTS nexusflow_db;

-- Exit
EXIT;
```

### Step 3: Restart Application
Start the Spring Boot application:

```bash
cd nexusflow-backend
mvn spring-boot:run
```

### Step 4: Verify Data Was Inserted
Connect to MySQL and check the data:

```sql
-- Connect to MySQL
mysql -u root -p

-- Use the database
USE nexusflow_db;

-- Check users (should have 3 users)
SELECT COUNT(*) as user_count FROM users;
SELECT id, email, first_name, last_name, role FROM users;

-- Check suppliers (should have 8 suppliers)
SELECT COUNT(*) as supplier_count FROM suppliers;
SELECT id, name, country, reliability_score FROM suppliers;

-- Check shipments (should have 12 shipments)
SELECT COUNT(*) as shipment_count FROM shipments;
SELECT id, tracking_number, status, origin_city, destination_city FROM shipments;

-- Check risk scores (should have 12 risk scores)
SELECT COUNT(*) as risk_score_count FROM risk_scores;

-- Check delay predictions (should have 12 predictions)
SELECT COUNT(*) as prediction_count FROM delay_predictions;

-- Check notifications (should have 8 notifications)
SELECT COUNT(*) as notification_count FROM notifications;
```

### Step 5: Check Application Logs
Look for these log messages when the application starts:

```
Initialized database with schema and data
...
Hibernate: create table ...
...
NexusFlowApplication started successfully
```

## Common Issues and Solutions

### Issue 1: "Table doesn't exist" errors
**Cause**: SQL script didn't execute properly
**Solution**: 
1. Check `application.properties` has correct settings
2. Delete the database and restart application
3. Check application logs for SQL errors

### Issue 2: "Duplicate entry" errors
**Cause**: Data already exists from previous run
**Solution**: Drop the database before restarting (see Step 2 above)

### Issue 3: Connection refused
**Cause**: MySQL server not running
**Solution**: 
```bash
# On Windows
net start MySQL80

# On Mac
brew services start mysql

# On Linux
sudo systemctl start mysql
```

### Issue 4: Access denied for user
**Cause**: Wrong MySQL username/password
**Solution**: Update credentials in `application.properties`:
```properties
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

### Issue 5: Data still not visible
**Cause**: Application might be using a different database
**Solution**: Verify database name in connection URL:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nexusflow_db
```

## Manual Database Setup (Alternative)

If automatic setup still doesn't work, you can manually run the SQL script:

```bash
# Run the SQL script manually
mysql -u root -p < nexusflow-backend/src/main/resources/database-setup.sql

# Then start the application
cd nexusflow-backend
mvn spring-boot:run
```

## Default Login Credentials

After successful setup, you can login with:

| Email | Password | Role |
|-------|----------|------|
| admin@nexusflow.com | admin123 | ADMIN |
| analyst@nexusflow.com | analyst123 | ANALYST |
| manager@nexusflow.com | manager123 | MANAGER |

## Quick Verification Commands

Run these one-liners to quickly check if data exists:

```sql
-- One-line verification
SELECT 'Users: ' as '', COUNT(*) as count FROM users UNION ALL SELECT 'Suppliers: ', COUNT(*) FROM suppliers UNION ALL SELECT 'Shipments: ', COUNT(*) FROM shipments UNION ALL SELECT 'Risk Scores: ', COUNT(*) FROM risk_scores UNION ALL SELECT 'Predictions: ', COUNT(*) FROM delay_predictions UNION ALL SELECT 'Notifications: ', COUNT(*) FROM notifications;
```

Expected output:
```
+---------------+-------+
|               | count |
+---------------+-------+
| Users:        |     3 |
| Suppliers:    |     8 |
| Shipments:    |    12 |
| Risk Scores:  |    12 |
| Predictions:  |    12 |
| Notifications:|     8 |
+---------------+-------+
```

## Still Having Issues?

If you're still not seeing data after following these steps:

1. **Check MySQL is running**: `mysqladmin -u root -p ping`
2. **Check database exists**: `SHOW DATABASES LIKE 'nexusflow_db';`
3. **Check tables exist**: `USE nexusflow_db; SHOW TABLES;`
4. **Check for errors in application.log**: Look for SQL exceptions
5. **Try running application with debug logging**: Add `-Ddebug=true` to JVM arguments