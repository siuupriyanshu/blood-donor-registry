#!/bin/bash
# Quick Setup Script for Blood Donor Registry System

echo "=========================================="
echo "Blood Donor Registry System - Setup Script"
echo "=========================================="
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed. Please install Maven 3.8.1 or higher."
    exit 1
fi

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    echo "ERROR: MySQL is not installed. Please install MySQL 8.0 or higher."
    exit 1
fi

echo "✓ Maven and MySQL found"
echo ""

# Step 1: Create database
echo "Step 1: Creating MySQL database..."
read -p "Enter MySQL username (default: root): " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}

read -sp "Enter MySQL password: " MYSQL_PASSWORD
echo ""

mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" < src/main/resources/db/schema.sql

if [ $? -eq 0 ]; then
    echo "✓ Database created successfully"
else
    echo "✗ Failed to create database"
    exit 1
fi

# Step 2: Update DatabaseConnection.java with credentials (optional)
echo ""
echo "Step 2: Update database credentials (if needed)"
echo "Edit src/main/java/com/blooddonor/dao/DatabaseConnection.java"
echo "Update: USER = \"$MYSQL_USER\""
echo "Update: PASSWORD = (enter your password in the file)"
echo ""
read -p "Press Enter to continue after updating credentials..."

# Step 3: Build the project
echo ""
echo "Step 3: Building project with Maven..."
mvn clean package

if [ $? -eq 0 ]; then
    echo "✓ Build successful"
else
    echo "✗ Build failed"
    exit 1
fi

echo ""
echo "=========================================="
echo "Setup Complete!"
echo "=========================================="
echo ""
echo "To run the application:"
echo "  mvn javafx:run"
echo ""
echo "Or directly:"
echo "  java -jar target/blood-donor-registry-1.0.0.jar"
echo ""
