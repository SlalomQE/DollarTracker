# Dollar Tracker - Complete Requirements Document

## Project Overview
**Project Name:** Dollar Tracker  
**Framework:** Java Spring Boot with Maven  
**Architecture:** Single-port web application with integrated frontend, backend, and database  
**Purpose:** Personal expense and revenue tracking application with user authentication, expense categorization, and financial analytics.

---

## 1. Technical Stack

### Backend
- **Framework:** Spring Boot 3.x
- **Build Tool:** Maven
- **Database:** MySQL/PostgreSQL (JPA/Hibernate ORM)
- **Security:** Spring Security with JWT tokens
- **Validation:** Spring Validation framework
- **API:** REST endpoints returning JSON

### Frontend
- **Technology:** Thymeleaf or React (embedded in Spring Boot)
- **Styling:** Bootstrap/CSS
- **Date Picker:** HTML5 date input with custom validation
- **Forms:** Form validation on both client and server side

### Database
- Relational database with normalized schema
- Timestamp fields stored as MM/DD/YYYY HH:MM:SS
- Indexes on frequently queried columns (userId, dates, category)

---

## 2. Database Schema

### User Table
- `userId` (Primary Key, UUID)
- `firstName` (VARCHAR 50, NOT NULL, alphabets only)
- `lastName` (VARCHAR 50, NOT NULL, alphabets only)
- `email` (VARCHAR 75, NOT NULL, UNIQUE, INDEX)
- `passwordHash` (VARCHAR 255, NOT NULL)
- `createdDate` (DATETIME, format: MM/DD/YYYY HH:MM:SS)
- `modifiedDate` (DATETIME, format: MM/DD/YYYY HH:MM:SS)
- `isActive` (BOOLEAN, DEFAULT TRUE)

### Expense Table
- `expenseId` (Primary Key, UUID)
- `userId` (Foreign Key to User, INDEX)
- `category` (ENUM: MORTGAGE_RENT, GROCERIES, CAR_EMI, DAYCARE, FAMILY_DINNER, MISCELLANEOUS)
- `amount` (DECIMAL 10,2, format: 00000.00, non-negative)
- `comments` (TEXT, MAX 100 words)
- `transactionDate` (DATETIME, format: MM/DD/YYYY HH:MM:SS, no future dates)
- `createdDate` (DATETIME, format: MM/DD/YYYY HH:MM:SS)
- `modifiedDate` (DATETIME, format: MM/DD/YYYY HH:MM:SS, updated on modifications)
- `isRecurring` (BOOLEAN, DEFAULT FALSE)
- `recurringFrequency` (ENUM: DAILY, WEEKLY, MONTHLY, YEARLY)

### Revenue Table
- `revenueId` (Primary Key, UUID)
- `userId` (Foreign Key to User, INDEX)
- `source` (ENUM: SALARY, RENTAL_INCOME, FROM_BUSINESS, OTHER)
- `amount` (DECIMAL 10,2, format: 0000.00, non-negative)
- `transactionDate` (DATETIME, format: MM/DD/YYYY HH:MM:SS, future dates allowed)
- `createdDate` (DATETIME, format: MM/DD/YYYY HH:MM:SS)
- `modifiedDate` (DATETIME, format: MM/DD/YYYY HH:MM:SS)
- `isRecurring` (BOOLEAN, DEFAULT FALSE)
- `recurringFrequency` (ENUM: DAILY, WEEKLY, MONTHLY, YEARLY)
- `notes` (TEXT)

---

## 3. Authentication & User Management

### 3.1 User Registration
**Endpoint:** `POST /api/auth/register`

**Validation Rules:**
- **First Name:** Required, max 50 chars, alphabets only → Error: "First Name is required"
- **Last Name:** Required, max 50 chars, alphabets only → Error: "Last Name is required"
- **Email:** Required, max 75 chars, valid format (XXX@XXX.XXX or XXX.XX@XXX.XXX.XX) → Error: "Email id is required" / "Invalid email format"
- **Email Uniqueness:** → Error: "Email id exists, please log in"
- **Password:** Min 8 chars, 1+ special char (!@#$%^&*), alphanumeric, not all same, max 2 consecutive digits
- **Confirm Password:** Must match password field
- **Register Button:** Enabled ONLY when all fields populated AND passwords match

**Response Codes:** 201 (Success), 400 (Validation), 409 (Email Conflict)

---

### 3.2 User Login
**Endpoint:** `POST /api/auth/login`

**Validation:**
- Username: Email format, max 75 chars
- Password: Bcrypt hash comparison
- Response: JWT token on success, 401 on failure

---

### 3.3 Password Reset
**Endpoint:** `POST /api/auth/reset-password`

**Validation:**
- Email must exist in system
- New password follows registration rules
- Confirm password must match new password
- Reset button enabled ONLY when: all fields populated AND passwords match

---

## 4. Expense Management

### 4.1 Create Expense
**Endpoint:** `POST /api/expenses`

**Fields:**
- Category (Dropdown: MORTGAGE_RENT, GROCERIES, CAR_EMI, DAYCARE, FAMILY_DINNER, MISCELLANEOUS) - Required
- Amount (Format: 00000.00, numeric only) - Required
- Comments (Max 100 words) - Optional
- Transaction Date (Calendar picker or MM/YY input, stored as MM/DD/YYYY HH:MM:SS) - Cannot be future date

**Database:** Store createdDate and modifiedDate

---

### 4.2 Modify Expense
**Endpoint:** `PUT /api/expenses/{expenseId}`

**Behavior:** Allow modification of all fields, update modifiedDate on every change

---

### 4.3 Search Expenses (Modify Tab)
**Endpoint:** `GET /api/expenses/search?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`

**Requirements:**
- Start date: REQUIRED, cannot be future
- End date: OPTIONAL (defaults to current date/time)
- Search button: Enabled ONLY after start date entered
- Default view: Last 7 calendar days
- Display: Show all matching entries with modified date if applicable
- Highlight: Recurring charges (e.g., CAR_EMI)
- Modify button: At end of each row allowing field modifications
- Constraint: Don't allow future dates

---

### 4.4 View Last 7 Days (View Tab)
**Endpoint:** `GET /api/expenses/last-7-days`

**Behavior:**
- Auto-load on tab switch
- Display last 7 calendar days entries
- Sum all expenses: Show "Total (7 days expenses): $XXXX.XX" at bottom
- Modify button: At end of each row

---

## 5. Revenue Management

### 5.1 Create Revenue
**Endpoint:** `POST /api/revenues`

**Fields:**
- Source (Dropdown: SALARY, RENTAL_INCOME, FROM_BUSINESS, OTHER) - Required
- Amount (Format: 0000.00, numeric only) - Required
- Transaction Date (Calendar picker or MM/YY input, future dates ALLOWED)
- Recurring (Checkbox) - Optional
- Recurring Frequency (DAILY, WEEKLY, MONTHLY, YEARLY) - Required if Recurring = true
- Notes - Optional

**Behavior:** Allow future dates (unlike expenses)

---

### 5.2 Revenue Summary
**Endpoint:** `GET /api/revenues/current-month` and `GET /api/financial-summary/current-month`

**Display:**
- Total Revenue (Current Month)
- Total Expenses (Current Month)
- Remaining Funds = Total Revenue - Total Expenses (highlight green if positive, red if negative)

---

## 6. Frontend Pages

### 6.1 Landing Page (Login)
- Route: `/`
- Email input (username, max 75 chars)
- Password input (masked)
- "Login" button
- "Register here" link → `/register`
- "Forgot Password?" link → `/reset-password`

### 6.2 Registration Page
- Route: `/register`
- First Name (max 50 chars, alphabets only)
- Last Name (max 50 chars, alphabets only)
- Email (max 75 chars, format validation, real-time duplicate check)
- Password (strength indicator, min 8 chars)
- Confirm Password (match indicator)
- Register button (enabled ONLY when conditions met)
- Duplicate email: Show "This email already exists, do you want to [Log In](/login) or [Reset Password](/reset-password)?"

### 6.3 Reset Password Page
- Route: `/reset-password`
- Email input (max 75 chars)
- New Password (max 50 chars, same validation as registration)
- Confirm New Password (max 50 chars)
- Reset button (enabled ONLY when all fields populated AND passwords match)

### 6.4 Home Page
- Route: `/home`
- Tab Navigation: New Expense | Modify | Revenue | View
- User profile dropdown (top right)
- Logout button

### 6.5 New Expense Tab
**Fields:**
- Category dropdown (required)
- Amount (format 00000.00, numeric only, auto-insert decimal)
- Transaction Date (calendar widget disables future dates, or MM/YY manual input)
- Comments (max 100 words, real-time counter)
- Submit button: "Add Expense" (enabled after category & amount)
- Success/Error notifications

### 6.6 Modify Tab
**Search:**
- Start Date (required, calendar or MM/YY input)
- End Date (optional, calendar or MM/YY input)
- Search button (enabled ONLY after start date)
- Default: Load last 7 days on page load

**Results Table:**
- Columns: Category | Amount | Transaction Date | Created Date | Modified Date | Status | Actions
- Highlight recurring charges
- Actions: Modify & Delete buttons
- Edit Modal: Allow field modifications

### 6.7 View Tab
**Display:**
- Auto-load last 7 calendar days
- Table columns: Category | Amount | Transaction Date | Created Date | Actions
- Total section: "Total (7 days expenses): $XXXX.XX"
- Modify button: At end of each row

### 6.8 Revenue Tab
**Form:**
- Revenue Source dropdown (required)
- Amount (format 0000.00, numeric only)
- Transaction Date (allows future dates)
- Recurring checkbox (when checked, show frequency dropdown)
- Recurring Frequency dropdown (required if recurring checked)
- Notes (optional, max 100 words)
- Submit button: "Add Revenue"

**Summary:**
```
Total Revenue (This Month): $XXXX.XX
Total Expenses (This Month): $XXXX.XX
Remaining Funds: $XXXX.XX
```

---

## 7. Date/Time Handling
- **Storage Format:** MM/DD/YYYY HH:MM:SS
- **Input Format:** Calendar picker or MM/YY (converts to MM/DD/YYYY 00:00:00)
- **Display Format:** MM/DD/YYYY HH:MM:SS (24-hour)
- **Timezone:** UTC (consistent)
- **Constraints:** Expenses: no future dates | Revenue: allows future dates

---

## 8. Password Validation Rules

| Requirement | Rule | Valid Example | Invalid Example |
|-------------|------|---------------|-----------------|
| Length | Min 8 characters | Pass@123 | Pass@12 |
| Alphanumeric | Letters + numbers | Pass@123 | Pass@!! |
| Special Char | At least 1 required | Pass@123 | Pass1234 |
| All Same | Not allowed | Pass@123 | aaaaaaaa |
| Consecutive Digits | Max 2 in series | Pass@123 | Pass@1234 |
| Spelled Numbers | Exception applies | PassOneTwoThree | Pass123 (rejected) |

---

## 9. Email Format Validation
- **Valid:** XXX@XXX.XXX (user@example.com) or XXX.XX@XXX.XXX.XX (john.doe@example.co.uk)
- **Alphanumeric:** Letters and numbers only, dots allowed in local/domain parts
- **Max Length:** 75 characters

---

## 10. Security Requirements
- JWT token-based authentication
- httpOnly cookies for token storage
- Bcrypt password hashing with salt
- Server-side input validation
- SQL injection prevention (parameterized queries)
- Rate limiting on auth endpoints
- Error handling: Don't expose sensitive information

---

## 11. API Response Format
**Success (200, 201):**
```json
{
  "success": true,
  "data": {},
  "message": "Action completed",
  "timestamp": "MM/DD/YYYY HH:MM:SS"
}
```

**Error (400, 401, 404, 409, 500):**
```json
{
  "success": false,
  "error": "Error message",
  "timestamp": "MM/DD/YYYY HH:MM:SS"
}
```

---

## 12. Database Configuration

### MySQL Setup (Production)
- **Database:** dollar_tracker
- **User:** tracker_user
- **Password:** tracker_password
- **Host:** localhost:3306
- **DDL Mode:** update (data persists across restarts)
- **Connection Pool:** HikariCP with 5-10 connections, 30s timeout
- **Dialect:** MySQL8Dialect
- **Installation:** `brew install mysql@8.0` on macOS

### Database Creation Script
```sql
CREATE DATABASE dollar_tracker;
CREATE USER 'tracker_user'@'localhost' IDENTIFIED BY 'tracker_password';
GRANT ALL PRIVILEGES ON dollar_tracker.* TO 'tracker_user'@'localhost';
FLUSH PRIVILEGES;
```

**Auto-Schema:** Hibernate with `spring.jpa.hibernate.ddl-auto: update` automatically creates/updates tables on application startup.

---

## 13. Build & Testing

### Build System
- **Build Tool:** Maven 3.9.11
- **Java Version:** Java 17
- **Compilation:** `mvn clean install`
- **Package Size:** ~56MB JAR file (dollar-tracker-1.0.0.jar)

### Unit Testing Framework
- **Test Framework:** JUnit 5 (Jupiter)
- **Mocking:** Mockito 5.x for dependency injection
- **Code Coverage:** JaCoCo 0.8.11 (Java 17 compatible)
- **Coverage Target:** 85%+ overall

### Test Suite (24 Tests - All Passing)

#### ExpenseService Tests (13 tests - 78% coverage)
- ✅ createExpense_Success
- ✅ createExpense_InvalidCategory
- ✅ createExpense_NegativeAmount
- ✅ createExpense_FutureDate
- ✅ createExpense_ExcessiveComments
- ✅ getExpensesByDateRange_Valid
- ✅ updateExpense_Success
- ✅ updateExpense_EmptyComments
- ✅ getCurrentMonthExpenses
- ✅ getLast7DaysExpenses
- ✅ deleteExpense_Authorized
- ✅ deleteExpense_Unauthorized
- ✅ getTotalExpensesByMonth

#### RevenueService Tests (11 tests - 82% coverage)
- ✅ createRevenue_Success
- ✅ createRevenue_InvalidSource
- ✅ createRevenue_NegativeAmount
- ✅ getRevenuesByMonth_Valid
- ✅ updateRevenue_Success
- ✅ getCurrentMonthRevenues
- ✅ getTotalRevenue_AllRecords
- ✅ deleteRevenue_Authorized
- ✅ deleteRevenue_Unauthorized
- ✅ getRevenuesByDateRange
- ✅ handleRecurringRevenue

### Running Tests
```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=ExpenseServiceTest

# Generate coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Coverage Reports Location
- **HTML:** `target/site/jacoco/index.html`
- **CSV:** `target/site/jacoco/jacoco.csv`
- **XML:** `target/site/jacoco/jacoco.xml`

**Current Coverage Metrics:**
- Service Layer: 80% (ExpenseService 78%, RevenueService 82%)
- Overall: ~65% (path to 85%+ target via UserService + Security tests)

---

## 14. Development Phases Completed

### Phase 1: Build & Infrastructure ✅
- Fixed 24 Java files with package declaration corruption
- Removed Lombok annotations, replaced with explicit getters/setters
- Updated JJWT from 0.12.3 to 0.11.5 for Java 17 compatibility
- Successfully compiled 56MB JAR

### Phase 2: Database Migration ✅
- Installed MySQL 8.0 via Homebrew
- Created dollar_tracker database with persistence (update mode)
- Configured tracker_user account with proper permissions
- Added HikariCP connection pooling for performance

### Phase 3: Authentication & Core Features ✅
- JWT token generation and validation
- User registration with email/password validation
- User login with Bcrypt password hashing
- Password reset functionality
- Logout with proper session cleanup

### Phase 4: Expense & Revenue Management ✅
- Complete CRUD operations for expenses and revenues
- Category and source enumerations
- Date range filtering with timezone handling
- Current month aggregations
- Last 7 days expense view

### Phase 5: Financial Summary ✅
- FinancialSummaryController for current month calculations
- Total expenses, total revenue, remaining funds
- Transaction lists with detailed breakdowns

### Phase 6: Bug Fixes & Optimization ✅
- Fixed logout button event handling
- Fixed search date parsing (LocalDate/LocalDateTime conversion)
- Fixed expense update persistence issues
- Fixed modal closing behavior with multiple fallback methods
- Fixed empty comments validation in service layer
- Added field validation before API calls

### Phase 7: Unit Testing & Coverage ✅
- Created 24 comprehensive unit tests
- Achieved 78-82% coverage on service layer
- Configured JaCoCo 0.8.11 for automated code coverage
- Generated detailed coverage reports with metrics

---

## 15. API Endpoints Reference

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/reset-password` - Password reset

### Expense Endpoints
- `POST /api/expenses` - Create expense
- `GET /api/expenses/search?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` - Search expenses
- `GET /api/expenses/last-7-days` - Get last 7 days expenses
- `PUT /api/expenses/{expenseId}` - Update expense
- `DELETE /api/expenses/{expenseId}` - Delete expense

### Revenue Endpoints
- `POST /api/revenues` - Create revenue
- `GET /api/revenues/current-month` - Get current month revenues
- `PUT /api/revenues/{revenueId}` - Update revenue
- `DELETE /api/revenues/{revenueId}` - Delete revenue

### Financial Summary Endpoints
- `GET /api/financial-summary/current-month` - Current month summary with transaction lists

---

## 16. Frontend Components

### Pages (Thymeleaf Templates)
- **login.html** - Authentication login form
- **register.html** - User registration form
- **reset-password.html** - Password reset form
- **home.html** - Main dashboard with tabs (New Expense, Modify, Revenue, View)

### JavaScript (home.js - Enhanced)
- `createExpense()` - Form submission with validation
- `searchExpenses()` - Date range filtering
- `editExpense()` - Load expense into edit modal
- `saveEditedExpense()` - Update expense with error handling and modal closing
- `deleteExpense()` - Remove expense with confirmation
- `createRevenue()` - Revenue form submission
- `loadFinancialSummary()` - Fetch and display current month data
- `loadLast7DaysExpenses()` - Auto-load on View tab
- `logout()` - Proper session cleanup and redirect
- `getToken()` - JWT retrieval from localStorage

### Styling
- **Bootstrap 5** - Responsive UI framework
- **CSS** - Custom styling for dashboard and forms

---

## 17. Project Files Structure

```
/Volumes/Projects/Catalyst/DollarTracker/
├── pom.xml                                    # Maven configuration (Java 17, Spring Boot 3.2.0)
├── applicationprompt.md                       # Complete requirements document
├── QUICK_START.md                             # Getting started guide
├── README.md                                  # Project overview
├── TEST_COVERAGE_REPORT.md                    # Unit test documentation
├── FILE_MANIFEST.md                           # File inventory
├── IMPLEMENTATION_SUMMARY.md                  # Implementation details
├── VERIFICATION_CHECKLIST.md                  # Quality verification
├── src/main/java/com/dollartracker/
│   ├── DollarTrackerApplication.java         # Spring Boot entry point
│   ├── config/SecurityConfig.java            # Spring Security JWT config
│   ├── controller/
│   │   ├── AuthController.java               # Register, login, password reset
│   │   ├── ExpenseController.java            # Expense CRUD + search
│   │   ├── RevenueController.java            # Revenue CRUD
│   │   ├── FinancialSummaryController.java   # Monthly aggregations
│   │   └── ViewController.java               # Page routing
│   ├── service/
│   │   ├── UserService.java                  # User management
│   │   ├── ExpenseService.java               # Expense business logic (TESTED: 78%)
│   │   └── RevenueService.java               # Revenue business logic (TESTED: 82%)
│   ├── repository/
│   │   ├── UserRepository.java               # Database queries for users
│   │   ├── ExpenseRepository.java            # Database queries for expenses
│   │   └── RevenueRepository.java            # Database queries for revenues
│   ├── entity/
│   │   ├── User.java                         # User JPA entity
│   │   ├── Expense.java                      # Expense JPA entity
│   │   └── Revenue.java                      # Revenue JPA entity
│   ├── dto/
│   │   ├── UserRegistrationDto.java          # Registration payload
│   │   ├── LoginDto.java                     # Login payload
│   │   ├── ResetPasswordDto.java             # Password reset payload
│   │   ├── ExpenseDto.java                   # Expense payload
│   │   ├── RevenueDto.java                   # Revenue payload
│   │   └── ApiResponse.java                  # Standard API response
│   ├── security/
│   │   ├── JwtTokenProvider.java             # JWT generation/validation
│   │   └── JwtAuthenticationFilter.java      # Request JWT interceptor
│   └── validator/
│       ├── EmailValidator.java               # Email validation logic
│       └── PasswordValidator.java            # Password validation logic
├── src/main/resources/
│   ├── application.yml                       # MySQL configuration (update mode)
│   ├── static/js/home.js                     # Frontend JavaScript
│   └── templates/
│       ├── login.html                        # Login template
│       ├── register.html                     # Registration template
│       ├── reset-password.html               # Password reset template
│       └── home.html                         # Dashboard template
├── src/test/java/com/dollartracker/
│   ├── service/
│   │   ├── ExpenseServiceTest.java           # 13 unit tests (PASSING ✅)
│   │   └── RevenueServiceTest.java           # 11 unit tests (PASSING ✅)
│   └── validator/
│       ├── EmailValidatorTest.java           # Email validation tests
│       └── PasswordValidatorTest.java        # Password validation tests
└── target/
    ├── dollar-tracker-1.0.0.jar              # Compiled JAR artifact
    ├── classes/                              # Compiled bytecode
    ├── test-classes/                         # Compiled test bytecode
    └── site/jacoco/                          # JaCoCo coverage reports
```

---

## 18. Deployment Notes

### Local Development
- **Single Port:** 8080
- **Frontend:** Thymeleaf templates served at `/`
- **API Endpoints:** `/api/` prefix with REST conventions
- **Database:** MySQL localhost:3306
- **Build:** `mvn clean install`
- **Run:** `mvn spring-boot:run`
- **JAR Execution:** `java -jar target/dollar-tracker-1.0.0.jar`

### Build Output
- **Compilation Time:** ~1.7 seconds
- **JAR Size:** ~56MB (dollar-tracker-1.0.0.jar.original in target/)
- **All Tests:** 24/24 passing (0 failures)
- **Coverage:** Generated JaCoCo reports in target/site/jacoco/

### Database Startup
MySQL service must be running before application start:
```bash
brew services start mysql@8.0
# Verify: mysql -u tracker_user -p dollar_tracker
```

### Application Health Check
```bash
curl http://localhost:8080/login
# Should return login.html template (HTTP 200)
```

---

## 19. Next Steps for 85%+ Coverage

### Remaining Work
1. **UserService Unit Tests** (5-8 tests)
   - Registration validation with mocked validators
   - Password reset flow
   - Email uniqueness checks
   - Target: 85%+ coverage on UserService

2. **Security & JWT Tests** (5-7 tests)
   - Token generation and validation
   - JWT expiration and refresh
   - Authentication filter behavior
   - Authorization checks

3. **Validator Unit Tests** (4-5 tests each)
   - EmailValidator with various formats
   - PasswordValidator with edge cases
   - Combined validation rules

4. **Controller Integration Tests** (12-16 tests)
   - Full request/response cycles
   - Error handling at API boundary
   - Data persistence verification

### Expected Final Coverage
- Service Layer: 90%+
- Validators: 95%+
- Controllers: 85%+
- **Overall Target:** 85%+ across entire application

---

## 20. Technology Versions

| Component | Version | Purpose |
|-----------|---------|---------|
| Java | 17 | Runtime environment |
| Spring Boot | 3.2.0 | Framework |
| Spring Security | 6.x | Authentication |
| Spring Data JPA | 3.2.0 | ORM |
| MySQL | 8.0+ | Database |
| MySQL Connector/J | 8.0.33 | JDBC driver |
| HikariCP | 5.x | Connection pooling |
| JJWT | 0.11.5 | JWT tokens |
| Thymeleaf | 3.1.x | Template engine |
| Bootstrap | 5.x | CSS framework |
| Hibernate | 6.x | JPA provider |
| JUnit 5 | 5.9.x | Testing framework |
| Mockito | 5.x | Mocking library |
| JaCoCo | 0.8.11 | Coverage analyzer |
| Maven | 3.9.11 | Build system |





