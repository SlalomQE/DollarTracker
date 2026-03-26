# Dollar Tracker - Quick Start Guide

## Complete Implementation Ready for Use

The Dollar Tracker application has been fully implemented with all required features. Here's how to get it running:

---

## Step 1: Prerequisites Installation

### Install Java 17
```bash
# macOS
brew install openjdk@17

# Windows - Download from https://www.oracle.com/java/technologies/downloads/

# Linux
sudo apt-get install openjdk-17-jdk
```

### Install Maven
```bash
# macOS
brew install maven

# Windows - Download from https://maven.apache.org/download.cgi

# Linux
sudo apt-get install maven
```

### Install MySQL or PostgreSQL
Choose one:

**Option A: MySQL**
```bash
# macOS
brew install mysql
brew services start mysql

# Linux
sudo apt-get install mysql-server
sudo service mysql start
```

**Option B: PostgreSQL**
```bash
# macOS
brew install postgresql
brew services start postgresql

# Linux
sudo apt-get install postgresql
sudo service postgresql start
```

---

## Step 2: Create Database

### For MySQL
```bash
mysql -u root -p
```

```sql
CREATE DATABASE dollar_tracker;
EXIT;
```

### For PostgreSQL
```bash
psql -U postgres
```

```sql
CREATE DATABASE dollar_tracker;
\q
```

---

## Step 3: Configure Application

Edit `/Volumes/Projects/Catalyst/DollarTracker/src/main/resources/application.yml`

### For MySQL (Default)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dollar_tracker
    username: root
    password: root  # Change if different
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### For PostgreSQL
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/dollar_tracker
    username: postgres
    password: postgres  # Change if different
    driver-class-name: org.postgresql.Driver
```

---

## Step 4: Build Project

```bash
cd /Volumes/Projects/Catalyst/DollarTracker
mvn clean install
```

**Expected Output:**
```
...
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXs
```

---

## Step 5: Run Application

```bash
mvn spring-boot:run
```

**Expected Output:**
```
...
2026-03-23 XX:XX:XX - Starting DollarTrackerApplication
...
2026-03-23 XX:XX:XX - Tomcat started on port(s): 8080 (http)
2026-03-23 XX:XX:XX - Started DollarTrackerApplication in X.XXs
```

---

## Step 6: Access Application

Open browser and navigate to:
```
http://localhost:8080
```

---

## User Workflow

### Registration (First Time)
1. Click "Register here" link
2. Enter:
   - First Name: `John` (alphabets only)
   - Last Name: `Doe` (alphabets only)
   - Email: `john@example.com` (valid email format)
   - Password: `SecurePass@123` (min 8 chars, special char required)
   - Confirm Password: `SecurePass@123`
3. Click "Create Account"
4. **✅ Automatically logged in and redirected to home page**

### Home Page Features

#### Tab 1: New Expense
- Select category: Mortgage/Rent, Groceries, Car EMI, Daycare, Family Dinner, Miscellaneous
- Enter amount: `1000.00`
- Select date: Use calendar (no future dates)
- Add comments (optional, max 100 words)
- Click "Add Expense"

#### Tab 2: Modify
- Enter start date (required)
- Enter end date (optional, defaults to today)
- Click "Search"
- Edit or delete expenses
- Default shows last 7 days

#### Tab 3: Revenue
- Select source: Salary, Rental Income, From Business, Other
- Enter amount: `5000.00`
- Select date (future dates allowed)
- Optionally mark as recurring
- Click "Add Revenue"
- View financial summary below

#### Tab 4: View
- Automatically shows last 7 days expenses
- Shows total for the period
- Quick edit access

---

## Key Validations

### Email Format
✅ `user@example.com` - VALID  
✅ `john.doe@company.co.uk` - VALID  
❌ `user@example` - INVALID  
❌ `user@.com` - INVALID  

### Password Requirements
✅ `SecurePass@123` - VALID (8+ chars, special char, mixed case)  
✅ `MyPass!2024` - VALID  
❌ `weakpass` - INVALID (no special char)  
❌ `Pass@111111` - INVALID (all same char)  
❌ `Pass@1234` - INVALID (3+ consecutive digits)  

### Amounts
✅ `1000.00` - VALID  
✅ `50.50` - VALID  
❌ `-100` - INVALID (negative)  
❌ `abc` - INVALID (non-numeric)  

---

## Troubleshooting

### Issue: Database Connection Error
**Solution:**
```
1. Verify database is running:
   - MySQL: mysql -u root -p (if password works, it's running)
   - PostgreSQL: psql -U postgres
2. Check credentials in application.yml
3. Verify database exists: CREATE DATABASE dollar_tracker;
```

### Issue: Port 8080 Already in Use
**Solution:**
```
1. Edit application.yml:
   server:
     port: 8081
2. Run: mvn spring-boot:run
3. Access: http://localhost:8081
```

### Issue: Maven Build Fails
**Solution:**
```
1. Clear Maven cache:
   mvn clean
2. Update dependencies:
   mvn dependency:resolve
3. Build again:
   mvn install
```

### Issue: Can't Login After Registration
**Solution:**
```
1. Try password reset on login page
2. Clear browser cookies and try again
3. Check database connection in logs
```

---

## Database Tables Created Automatically

When application starts, these tables are created:
- ✅ `users` - User accounts
- ✅ `expenses` - Expense entries
- ✅ `revenues` - Revenue entries

Hibernate creates schema automatically based on JPA entities.

---

## API Testing (Optional)

### Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john@example.com","password":"SecurePass@123"}'
```

### Test Create Expense
```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "category":"GROCERIES",
    "amount":50.00,
    "transactionDate":"2026-03-20",
    "comments":"Weekly groceries"
  }'
```

---

## Production Deployment

### Build WAR File
```bash
mvn clean package
```

Creates: `target/dollar-tracker-1.0.0.jar`

### Run in Production
```bash
java -jar target/dollar-tracker-1.0.0.jar
```

---

## Support Documentation

- **Full Requirements:** See `applicationprompt.md`
- **Implementation Details:** See `IMPLEMENTATION_SUMMARY.md`
- **Complete Setup Guide:** See `README.md`

---

## Success Indicators

✅ Application starts without errors  
✅ Can register new user  
✅ Can login with credentials  
✅ Can add expenses and revenue  
✅ Can modify and delete entries  
✅ Can view financial summary  
✅ Responsive UI on desktop and mobile  

---

## Next Actions

1. **Start Application:** `mvn spring-boot:run`
2. **Register User:** Visit `http://localhost:8080`
3. **Add Expenses:** Use New Expense tab
4. **Track Revenue:** Use Revenue tab
5. **View Summaries:** Check View and Revenue tabs

---

**Ready to use!** 🚀

The application is fully functional and ready for immediate use after setup.

For detailed information, refer to README.md and applicationprompt.md.
