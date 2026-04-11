# Local Blood Donor Registry System (LBDRS)

Module: CIS096-1 - Principles of Programming and Data Structures  
Student: Priyanshu Kumar Mandal (2542275)  
University: University of Bedfordshire

## Overview

LBDRS is a JavaFX desktop application for managing blood donor records and donation history with role-based access.

Core capabilities:
- Authentication for Admin and Medical Staff users
- Donor registration, search, filtering, edit, and delete
- Donation history tracking per donor
- Eligibility check using the 56-day rule
- Secure SQL access through prepared statements

## Technology Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| UI | JavaFX (FXML + CSS) | 21.0.2 |
| Build | Maven | 3.9+ |
| Database | MySQL | 8.x |
| JDBC | mysql-connector-j | 8.0.33 |
| Security | jBCrypt | 0.4 |
| Testing | JUnit 5 + Mockito | 5.x |

## Documentation Index

- Main setup and usage: `README.md`

## Prerequisites

- JDK 21
- Apache Maven 3.9+
- MySQL 8

Verify tools:

```bash
java -version
mvn -version
mysql --version
```

## Quick Start

### 1) Configure Database Credentials

Update `src/main/resources/db.properties` first (defaults in this repo are `root` / `root`):

```properties
db.url=jdbc:mysql://localhost:3306/lbdrs_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=root
```

### 2) Initialize the Database

Run:

```bash
mysql -u <your_user> -p < src/main/resources/db/schema.sql
```

Important:
- `schema.sql` starts with `DROP DATABASE IF EXISTS lbdrs_db;`
- It recreates the schema and seeds users, donors, and donation history
- It inserts 5000 donor rows for testing/demo scale

### 3) Run the Application

```bash
mvn clean javafx:run
```

### 4) Login (Seed Accounts)

The current `schema.sql` seeds these development credentials:

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin` |
| Medical Staff | `staff` | `staff` |

Note:
- `UserRepository` supports BCrypt verification
- For legacy/plain seed values, the code includes a fallback plain-text match for development
- For production-style users, create accounts with BCrypt hashes via `UserRepository.createUser(...)`

## Build and Package

Create the shaded executable jar:

```bash
mvn clean package
```

Output artifact:
- `target/lbdrs-1.0-SNAPSHOT.jar`

Run packaged jar (if JavaFX modules are not auto-provided by your runtime):

```bash
java --module-path /path/to/javafx/lib \
    --add-modules javafx.controls,javafx.fxml \
    -jar target/lbdrs-1.0-SNAPSHOT.jar
```

## Testing

Run all tests:

```bash
mvn test
```

Run specific tests:

```bash
mvn -Dtest=DonorTest test
mvn -Dtest=DonationHistoryTest test
mvn -Dtest=ValidationUtilTest test
```

## Project Structure

```text
lbdrs/
|- pom.xml
|- README.md
|- README_BUILD_GUIDE.md
|- README_OOP_ARCHITECTURE.md
|- README_SYSTEM_ARCHITECTURE.md
|- README_PROJECT_GUIDE.md
|- README_VIVA_DEMO.md
|- PROJECT_REPORT.md
`- src/
   |- main/
   |  |- java/com/lbdrs/
   |  |  |- Main.java
   |  |  |- controller/
   |  |  |  |- LoginController.java
   |  |  |  |- DashboardController.java
   |  |  |  |- DonorFormController.java
   |  |  |  `- DonationController.java
   |  |  |- dao/
   |  |  |  |- UserRepository.java
   |  |  |  |- DonorRepository.java
   |  |  |  `- DonationHistoryRepository.java
   |  |  |- db/
   |  |  |  `- DatabaseConnection.java
   |  |  |- model/
   |  |  |  |- User.java
   |  |  |  |- Admin.java
   |  |  |  |- MedicalStaff.java
   |  |  |  |- Donor.java
   |  |  |  |- DonationHistory.java
   |  |  |  `- Session.java
   |  |  `- util/
   |  |     `- ValidationUtil.java
   |  `- resources/
   |     |- db.properties
   |     |- db/schema.sql
   |     |- fxml/
   |     |  |- login.fxml
   |     |  |- dashboard.fxml
   |     |  |- donor_form.fxml
   |     |  `- donation_history.fxml
   |     `- css/style.css
   `- test/java/com/lbdrs/model/
     |- DonorTest.java
     |- DonationHistoryTest.java
     `- ValidationUtilTest.java
```

## Architecture and Design Patterns

- MVC: JavaFX Controllers + FXML Views + model classes
- DAO: Repository classes isolate SQL and mapping logic
- Singleton: `DatabaseConnection` and `Session`
- OOP principles:
  - Abstraction via abstract `User`
  - Inheritance via `Admin` and `MedicalStaff`
  - Encapsulation across model classes

## Functional Highlights

- Role-based login and UI behavior
- Donor CRUD with admin restrictions
- Donor search and blood-group filtering
- Donation history recording and retrieval
- Eligibility logic in `Donor.isEligible()` (56-day interval)
- Prepared statements in DAO layer for SQL safety

## License

Academic project for coursework submission (University of Bedfordshire, 2026).
