# Quick Start Guide

## Prerequisites
- Java 21 JDK
- MySQL 8.0+
- Maven 3.8.1+

## Step-by-Step Setup (5 minutes)

### 1. Initialize Database
```bash
# Run this command from project root
mysql -u root -p < src/main/resources/db/schema.sql
```
(Enter your MySQL password when prompted)

### 2. Configure Database Connection
Edit: `src/main/java/com/blooddonor/dao/DatabaseConnection.java`

Update these lines with your MySQL credentials:
```java
private static final String USER = "root";        // Your MySQL username
private static final String PASSWORD = "root";    // Your MySQL password
```

### 3. Build the Project
```bash
mvn clean package
```

### 4. Run the Application
```bash
mvn javafx:run
```

## Default Sample Data

The database schema includes 5 pre-loaded locations:
- New York, NY, USA
- Los Angeles, CA, USA
- Chicago, IL, USA
- Houston, TX, USA
- Phoenix, AZ, USA

All 8 blood types are pre-loaded:
- O+, O-, A+, A-, B+, B-, AB+, AB-

## First Time Usage

1. **Create a Donor**
   - Go to "Manage Donors" tab
   - Click "Create New Donor"
   - Fill in all required fields
   - Select blood type and location
   - Save

2. **Record a Donation**
   - Go to "Donation History" tab
   - Select donor from dropdown
   - Click "Load History"
   - Click "Add New Donation"
   - Fill donation details
   - Save

3. **Search for Donors**
   - Go to "Search Donors" tab
   - Select blood type and location
   - Check "Only eligible to donate" for immediate donors
   - Click "Search"

## Troubleshooting

### Connection Error
```
Failed to connect to database: Access denied for user 'root'@'localhost'
```
→ Check MySQL password in DatabaseConnection.java

### FXML Not Found
```
Failed to load sub-controllers: /fxml/main.fxml
```
→ Ensure FXML files are in `src/main/resources/fxml/`

### Build Failed
```
mvn clean package
```
→ Check Java version: `java -version` (must be 21.x)

## Key Files to Know

| File | Purpose |
|------|---------|
| `pom.xml` | Maven configuration and dependencies |
| `src/main/resources/db/schema.sql` | Database schema |
| `src/main/java/com/blooddonor/app/MainApp.java` | Application entry point |
| `src/main/java/com/blooddonor/dao/DatabaseConnection.java` | Database credentials |

## Database Connection Details

After setup, the application connects to:
```
Database: blood_donor_registry
Host: localhost:3306
User: root (by default)
```

## Common Tasks

### Reset Database
```bash
mysql -u root -p
DROP DATABASE blood_donor_registry;
```
Then re-run the schema.sql file

### Check Database Connection
```bash
mysql -u root -p -e "USE blood_donor_registry; SHOW TABLES;"
```

### View Sample Data
```bash
mysql -u root -p
USE blood_donor_registry;
SELECT * FROM blood_types;
SELECT * FROM locations;
```

## Need Help?

- Check README.md for full documentation
- Review project structure in README.md
- Check error messages in application dialogs
- Verify database connectivity first

---

Ready to start? Run: `mvn javafx:run`
