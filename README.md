# Local Blood Donor Registry System

A desktop application for managing blood donors and facilitating blood donation coordination in local communities.

## Project Overview

This is a JavaFX-based desktop application that simplifies the process of managing eligible blood donors and reducing the time to find a match during local medical emergencies.

### Technologies Used

- **Java 21** - Core programming language
- **JavaFX 21** - GUI framework
- **MySQL 8.0** - Database
- **JDBC** - Database connectivity
- **Maven** - Build automation tool
- **OOP Principles** - Encapsulation, Inheritance, Polymorphism

## Features

### Core Functionality

1. **Donor Management**
   - Create, Read, Update, Delete (CRUD) donor records
   - Store personal information (name, email, phone, address)
   - Track blood type and donation availability
   - Record medical conditions affecting donation eligibility

2. **Search & Filtering**
   - Search donors by blood type
   - Filter donors by location
   - Combined search by blood type and location
   - View only immediately eligible donors

3. **Donation History**
   - Record donation dates and volumes
   - Track donation status (Completed, Pending, Failed, Cancelled)
   - View donor donation history
   - Calculate days until next eligible donation (56 days after last donation)

4. **Dashboard & Reports**
   - View total donor statistics
   - See breakdown by blood type
   - See breakdown by location
   - Track donation metrics

### Business Rules

- **Age Eligibility:** Donors must be between 18-65 years old
- **Donation Interval:** Whole blood donors can donate every 56 days
- **Validation:** Email and phone validation for data integrity
- **Medical Conditions:** Track conditions that affect donation eligibility

## Project Structure

```
blood-donor-registry/
├── pom.xml                           # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/com/blooddonor/
│   │   │   ├── app/
│   │   │   │   └── MainApp.java     # Application entry point
│   │   │   ├── models/              # Data models
│   │   │   │   ├── Donor.java
│   │   │   │   ├── BloodType.java
│   │   │   │   ├── Location.java
│   │   │   │   ├── DonationRecord.java
│   │   │   │   └── MedicalCondition.java
│   │   │   ├── dao/                 # Data Access Objects
│   │   │   │   ├── DatabaseConnection.java
│   │   │   │   ├── DonorDAO.java
│   │   │   │   ├── BloodTypeDAO.java
│   │   │   │   ├── LocationDAO.java
│   │   │   │   ├── DonationRecordDAO.java
│   │   │   │   └── MedicalConditionDAO.java
│   │   │   ├── service/             # Business logic services
│   │   │   │   ├── DonorService.java
│   │   │   │   └── SearchService.java
│   │   │   ├── controller/          # JavaFX controllers
│   │   │   │   ├── MainController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── DonorManagementController.java
│   │   │   │   ├── SearchController.java
│   │   │   │   ├── DonationHistoryController.java
│   │   │   │   ├── DonorEditorController.java
│   │   │   │   └── DonationRecorderController.java
│   │   │   └── util/                # Utility classes
│   │   │       ├── ValidationUtils.java
│   │   │       └── DateUtils.java
│   │   └── resources/
│   │       ├── fxml/                # FXML layout files
│   │       │   ├── main.fxml
│   │       │   ├── dashboard.fxml
│   │       │   ├── donor_management.fxml
│   │       │   ├── search.fxml
│   │       │   ├── donation_history.fxml
│   │       │   ├── donor_editor.fxml
│   │       │   └── donation_recorder.fxml
│   │       └── db/
│   │           └── schema.sql       # Database initialization script
│   └── test/
│       └── java/com/blooddonor/     # Unit tests
└── README.md
```

## Setup Instructions

### Prerequisites

- JDK 21 or higher installed
- MySQL 8.0 or higher installed and running
- Maven 3.8.1 or higher
- Git (optional, for version control)

### Database Setup

1. **Create the database:**
   ```bash
   mysql -u root -p < src/main/resources/db/schema.sql
   ```
   (Enter your MySQL password when prompted)

2. **Verify the database creation:**
   ```bash
   mysql -u root -p
   USE blood_donor_registry;
   SHOW TABLES;
   ```

### Application Setup

1. **Configure Database Connection:**
   - Open `src/main/java/com/blooddonor/dao/DatabaseConnection.java`
   - Update the following constants with your MySQL credentials:
     ```java
     private static final String URL = "jdbc:mysql://localhost:3306/blood_donor_registry";
     private static final String USER = "root";
     private static final String PASSWORD = "your_password";
     ```

2. **Build the project:**
   ```bash
   mvn clean package
   ```

3. **Run the application:**
   ```bash
   mvn javafx:run
   ```

   Or directly with Java:
   ```bash
   java -m javafx.controls -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) com.blooddonor.app.MainApp
   ```

## Usage Guide

### Dashboard Tab
- View overall statistics
- See donor distribution by blood type
- Monitor donations by location

### Manage Donors Tab
- View all registered donors
- Filter by blood type, location, or availability
- Create, update, or delete donor records
- Quick access to donor information

### Search Donors Tab
- Advanced search by blood type and location
- Filter for immediately eligible donors
- View days until next donation eligibility

### Donation History Tab
- Select a donor to view their donation history
- Record new donations
- View summary statistics (total donations, volume, last donation date)
- Track donation status

## Database Schema

### Tables

1. **blood_types** - Standard blood type information (A+, A-, B+, B-, AB+, AB-, O+, O-)
2. **locations** - Geographic locations of donors
3. **donors** - Donor profiles with personal information
4. **donation_history** - Record of each donation
5. **medical_conditions** - Medical conditions affecting donation eligibility

### Key Relationships

- `donors.blood_type_id` → `blood_types.id`
- `donors.location_id` → `locations.id`
- `donation_history.donor_id` → `donors.id`
- `medical_conditions.donor_id` → `donors.id`

## Validation Rules

### Donor Information
- **Name:** 2-150 characters required
- **Email:** Valid email format (optional but validated if provided)
- **Phone:** Valid phone format (optional but validated if provided)
- **Age:** Must be 18-65 years old
- **Gender:** Male, Female, or Other
- **Blood Type:** Must be selected from standard types
- **Location:** Must be selected from available locations

### Donation Records
- **Volume:** 400-550 milliliters
- **Donation Interval:** Minimum 56 days between donations
- **Status:** Completed, Pending, Failed, or Cancelled

## OOP Concepts Implemented

### Encapsulation
- Private fields in model classes with public getters/setters
- Data validation in service layer
- Hidden database implementation details in DAO layer

### Inheritance
- Controllers extend appropriate JavaFX base classes
- Services follow inheritance hierarchy patterns

### Polymorphism
- DAO pattern for database abstraction
- Multiple implementations of search criteria
- Event handlers for various user actions

## Data Structures Used

- **ArrayList** - For storing list of donors, donations, locations
- **HashMap** - (Potential) For caching frequently accessed data
- **Queue/Stack** - (For future) Transaction management

## Error Handling

- SQL exceptions handled with user-friendly error messages
- Input validation with specific error feedback
- Database connection failure recovery
- Graceful shutdown on application close

## Future Enhancements

- Advanced blood compatibility checking
- SMS/Email notifications for eligible donors
- Mobile app integration
- Cloud database migration
- Advanced reporting and analytics
- Appointment scheduling
- Blood bank inventory management
- Real-time availability updates

## Testing

To run unit tests:
```bash
mvn test
```

## Troubleshooting

### Database Connection Failed
- Verify MySQL is running: `sudo service mysql status`
- Check credentials in DatabaseConnection.java
- Ensure database was created: `SHOW DATABASES;`

### FXML Loading Error
- Verify FXML files exist in `src/main/resources/fxml/`
- Check file paths in FXMLLoader calls
- Ensure controllers are in correct packages

### JavaFX Module Errors
- Make sure JDK 21 is installed
- Check Maven pom.xml JavaFX version matches JDK version

## License

This project is developed for local community blood donation management.

## Contact & Support

For issues or questions, please refer to project documentation or contact the development team.

---

**Version:** 1.0.0  
**Last Updated:** February 2026  
**Status:** Development
