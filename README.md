# Dollar Tracker - Personal Expense & Revenue Tracking Application

## Project Overview
Dollar Tracker is a comprehensive personal finance management application built with Spring Boot 3.x, featuring user authentication, expense tracking with categories, revenue management, and financial analytics.

---

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0+ or PostgreSQL 12+
- Git (optional)

---

## Installation & Setup

### 1. Clone or Download the Project
```bash
git clone <repository-url>
cd dollar-tracker
```

### 2. Create Database
For MySQL:
```sql
CREATE DATABASE dollar_tracker;
```

For PostgreSQL:
```sql
CREATE DATABASE dollar_tracker;
```

### 3. Configure Database Connection
Edit `src/main/resources/application.yml`:

For MySQL:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dollar_tracker
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

For PostgreSQL:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/dollar_tracker
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQL10Dialect
```

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

---

## Usage

### 1. Access the Application
Open your browser and navigate to:
```
http://localhost:8080
```

### 2. User Registration
1. Click "Register here" link
2. Fill in:
   - First Name (alphabets only, max 50 chars)
   - Last Name (alphabets only, max 50 chars)
   - Email (valid format, max 75 chars)
   - Password (min 8 chars, at least 1 special char, max 2 consecutive digits)
   - Confirm Password
3. Click "Create Account"
4. You'll be automatically logged in and redirected to the home page

### 3. Home Page - Tabs

#### New Expense
- Select expense category (Mortgage/Rent, Groceries, Car EMI, Daycare, Family Dinner, Miscellaneous)
- Enter amount (format: 00000.00)
- Select transaction date (no future dates allowed)
- Add optional comments (max 100 words)
- Click "Add Expense"

#### Modify
- Enter start date (required)
- Enter end date (optional, defaults to today)
- Click "Search" to view expenses
- Use "Edit" button to modify any expense field
- Use "Delete" button to remove expenses
- Default view shows last 7 days of expenses

#### Revenue
- Select revenue source (Salary, Rental Income, From Business, Other)
- Enter amount (format: 0000.00)
- Select transaction date (future dates allowed)
- Optionally mark as recurring and select frequency
- Add optional notes (max 100 words)
- Click "Add Revenue"
- View financial summary:
  - Total Revenue (Current Month)
  - Total Expenses (Current Month)
  - Remaining Funds (Revenue - Expenses)

#### View
- Displays last 7 calendar days of expenses
- Shows total expenses for the period
- Allows quick editing of expenses

### 4. Password Reset
1. Click "Forgot Password?" on login page
2. Enter your email
3. Enter new password
4. Confirm new password
5. Click "Reset Password"
6. Redirect to login page with new password

---

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/reset-password` - Reset password
- `POST /api/auth/logout` - Logout user

### Expenses
- `POST /api/expenses` - Create expense
- `PUT /api/expenses/{expenseId}` - Update expense
- `GET /api/expenses/search?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` - Search expenses by date range
- `GET /api/expenses/last-7-days` - Get last 7 days expenses
- `DELETE /api/expenses/{expenseId}` - Delete expense

### Revenue
- `POST /api/revenues` - Create revenue entry
- `PUT /api/revenues/{revenueId}` - Update revenue entry
- `GET /api/revenues/current-month` - Get current month revenues
- `DELETE /api/revenues/{revenueId}` - Delete revenue entry

---

## Database Schema

### Users Table
- `userId` - UUID primary key
- `firstName` - VARCHAR 50, alphabets only
- `lastName` - VARCHAR 50, alphabets only
- `email` - VARCHAR 75, unique, indexed
- `passwordHash` - Bcrypt hashed password
- `createdDate` - Timestamp
- `modifiedDate` - Timestamp
- `isActive` - Boolean

### Expenses Table
- `expenseId` - UUID primary key
- `userId` - Foreign key to users
- `category` - ENUM (MORTGAGE_RENT, GROCERIES, CAR_EMI, DAYCARE, FAMILY_DINNER, MISCELLANEOUS)
- `amount` - DECIMAL 10,2
- `comments` - TEXT (max 100 words)
- `transactionDate` - DATETIME (no future dates)
- `createdDate` - DATETIME
- `modifiedDate` - DATETIME
- `isRecurring` - Boolean
- `recurringFrequency` - ENUM

### Revenues Table
- `revenueId` - UUID primary key
- `userId` - Foreign key to users
- `source` - ENUM (SALARY, RENTAL_INCOME, FROM_BUSINESS, OTHER)
- `amount` - DECIMAL 10,2
- `transactionDate` - DATETIME (future dates allowed)
- `createdDate` - DATETIME
- `modifiedDate` - DATETIME
- `isRecurring` - Boolean
- `recurringFrequency` - ENUM
- `notes` - TEXT

---

## Key Features

### Security
✓ JWT token-based authentication  
✓ Bcrypt password hashing  
✓ Secure httpOnly cookies  
✓ CORS protection  
✓ SQL injection prevention (parameterized queries)  

### Validation
✓ Server-side and client-side validation  
✓ Email format validation (XXX@XXX.XXX)  
✓ Password strength requirements  
✓ Amount and word count validation  
✓ Date range constraints  

### User Experience
✓ Real-time validation feedback  
✓ Password strength indicator  
✓ Word count display  
✓ Modal-based editing  
✓ Responsive Bootstrap design  
✓ Success/error notifications  

### Financial Features
✓ Expense categorization  
✓ Revenue tracking  
✓ Recurring entries support  
✓ Financial summary (monthly totals)  
✓ Last 7 days expense summary  
✓ Date range expense filtering  

---

## Configuration

### JWT Settings
Edit `src/main/resources/application.yml`:
```yaml
jwt:
  secret: dollar-tracker-secret-key-for-jwt-token-generation-min-256-bits-long
  expiration: 86400000 # 24 hours in milliseconds
```

### Logging
```yaml
logging:
  level:
    root: INFO
    com.dollartracker: DEBUG
```

### Server Port
```yaml
server:
  port: 8080
```

---

## Development

### Run Tests
```bash
mvn test
```

### Build WAR File
```bash
mvn clean package
```

### IDE Setup (IntelliJ IDEA / Eclipse)
1. Import project as Maven project
2. Configure JDK 17
3. Enable annotation processing for Lombok

---

## Troubleshooting

### Database Connection Error
- Verify MySQL/PostgreSQL is running
- Check database URL, username, password in application.yml
- Ensure database exists

### Port Already in Use
Change port in `application.yml`:
```yaml
server:
  port: 8081
```

### JWT Token Expired
Clear browser cookies and login again

### Password Validation Issues
- Min 8 characters
- At least 1 special character (!@#$%^&*)
- Not all same characters
- Max 2 consecutive digits

---

## Support
For issues or questions, please check the application logs or contact the development team.

---

## License
Proprietary - Personal Project

---

## Version
1.0.0 - Initial Release
