# ExpenseTrackerFX - Personal Expense Manager

A desktop application to track and manage your personal expenses using JavaFX and PostgreSQL.

## What it does

- Track your daily expenses
- View spending analytics with charts
- Export data to CSV files
- Switch between light and dark themes

## Features

- **Dashboard**: See your monthly spending overview
- **Add Expenses**: Record new expenses with category, amount, date, and notes
- **Analytics**: View charts showing spending by category and monthly trends
- **Export**: Save your expense data as CSV files
- **Themes**: Choose between light and dark mode

## Requirements

- Java 21 or higher
- PostgreSQL database
- Maven (for building)

## Setup

1. **Install PostgreSQL** and create a database:
   ```sql
   CREATE DATABASE expense_tracker;
   ```

2. **Create the expenses table**:
   ```sql
   CREATE TABLE expenses (
       id SERIAL PRIMARY KEY,
       category VARCHAR(100) NOT NULL,
       amount DECIMAL(10, 2) NOT NULL,
       date DATE NOT NULL,
       merchant VARCHAR(100),
       notes TEXT
   );
   ```

3. **Update database connection** in `DatabaseConfig.java`:
   - Change username and password to match your PostgreSQL setup

## How to Run

1. **Download or clone** the project
2. **Open terminal** in the project folder
3. **Run these commands**:
   ```bash
   mvn clean install
   mvn javafx:run
   ```

Or open the project in your IDE (IntelliJ IDEA, Eclipse) and run `MainApp.java`.

## How to Use

1. **Dashboard**: Opens automatically - shows your spending summary
2. **Add Expense**: Click "Add Expense" to record new spending
3. **Analytics**: Click "Analytics" to see charts and graphs
4. **Export**: Click "Export" to save data as CSV file
5. **Settings**: Change between light/dark theme

## Project Structure

```
ExpenseTracker/
├── src/main/java/              # Java source code
├── src/main/resources/         # FXML layouts and CSS
├── src/main/resources/css/     # Styling files
└── pom.xml                     # Maven dependencies
```

## Technology Used

- **Java 21** - Programming language
- **JavaFX 21** - User interface
- **PostgreSQL** - Database
- **Maven** - Build tool
- **CSS** - Styling

## Need Help?

- Make sure PostgreSQL is running
- Check database connection settings
- Verify Java 21 is installed
- Contact me if you have issues
