# Jewelry POS System - Complete Startup Guide

## Prerequisites Check

Before starting, ensure you have:
- ✅ Java 21 installed
- ✅ Maven installed (or use the included `mvnw` wrapper)
- ✅ Node.js 20.x installed
- ✅ npm 10.x installed

## Step-by-Step Startup Instructions

### Option 1: Start Backend and Frontend Separately (Recommended for Development)

This approach allows you to develop and see changes in real-time.

---

#### **STEP 1: Start the Spring Boot Backend**

1. **Open Terminal/PowerShell** in the project root directory:
   ```powershell
   cd C:\Silicon\Examples\jewelry_shop\jewelry
   ```

2. **Build the backend** (this will download dependencies):
   ```powershell
   # Using Maven wrapper (recommended)
   .\mvnw clean install -DskipTests
   
   # OR using system Maven
   mvn clean install -DskipTests
   ```
   
   **Expected output**: `BUILD SUCCESS`

3. **Start the Spring Boot application**:
   ```powershell
   # Using Maven wrapper
   .\mvnw spring-boot:run
   
   # OR using system Maven
   mvn spring-boot:run
   ```

4. **Verify backend is running**:
   - You should see: `Started JewelryShopApplication in X seconds`
   - Backend URL: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`
   - Swagger UI: `http://localhost:8080/swagger-ui/index.html`

5. **Test the login endpoint**:
   ```powershell
   curl -X POST http://localhost:8080/api/auth/login `
     -H "Content-Type: application/json" `
     -d '{\"username\":\"admin\",\"password\":\"password\"}'
   ```
   
   **Expected response**: JSON with token, username, and role

---

#### **STEP 2: Start the Angular Frontend**

1. **Open a NEW Terminal/PowerShell** window

2. **Navigate to frontend directory**:
   ```powershell
   cd C:\Silicon\Examples\jewelry_shop\jewelry\frontend
   ```

3. **Install Node.js dependencies** (first time only):
   ```powershell
   npm install
   ```
   
   **Wait time**: 2-5 minutes depending on internet speed
   
   **Expected output**: `added XXX packages`

4. **Start the Angular development server**:
   ```powershell
   npm start
   ```
   
   **Wait time**: 30-60 seconds for compilation
   
   **Expected output**: 
   ```
   ** Angular Live Development Server is listening on localhost:4200 **
   ✔ Compiled successfully.
   ```

5. **Open your browser**:
   - Navigate to: `http://localhost:4200`
   - You should see the **Nano Banana** login screen with gold diamond icon

---

#### **STEP 3: Login and Test**

1. **Default credentials** (check your DataSeeder.java for actual credentials):
   - Username: `admin` or `manager` or `cashier`
   - Password: `password` (or whatever you set in DataSeeder)

2. **After login**, you should see:
   - Gold ticker scrolling at the top with gold rates
   - Left sidebar with navigation menu
   - Dashboard (if logged in as Manager)
   - POS Terminal (if logged in as Cashier)

3. **Test different screens**:
   - **Dashboard**: View metrics and scrap inventory
   - **POS**: Scan barcodes and create sales
   - **Inventory**: Manage products
   - **Old Gold**: Buy cash and purification
   - **Reports**: View transactions

---

### Option 2: Build Everything Together (Production Build)

This builds both frontend and backend into a single JAR file.

1. **Build the complete application**:
   ```powershell
   cd C:\Silicon\Examples\jewelry_shop\jewelry
   .\mvnw clean package
   ```
   
   **What happens**:
   - Maven downloads Node.js and npm (first time only)
   - Installs Angular dependencies
   - Builds Angular production bundle
   - Copies Angular files to Spring Boot's static resources
   - Builds Spring Boot JAR
   
   **Wait time**: 5-10 minutes (first time)

2. **Run the JAR file**:
   ```powershell
   java -jar target\jewelry.pos-0.0.1-SNAPSHOT.jar
   ```

3. **Access the application**:
   - Open browser: `http://localhost:8080`
   - The Angular app is now served by Spring Boot

---

## Troubleshooting

### Backend Issues

**Problem**: Port 8080 already in use
```
Solution: Kill the process or change port in application.properties:
server.port=8081
```

**Problem**: H2 database errors
```
Solution: Delete the database file and restart:
rm -rf data/
```

**Problem**: JWT secret key error
```
Solution: Add to application.properties:
jwt.secret=MySecretKeyForJewelryPOSSystemThatIsAtLeast256BitsLongForHS256Algorithm
```

### Frontend Issues

**Problem**: Port 4200 already in use
```
Solution: Kill the process:
Get-Process -Id (Get-NetTCPConnection -LocalPort 4200).OwningProcess | Stop-Process -Force
```

**Problem**: npm install fails
```
Solution: Clear npm cache and retry:
npm cache clean --force
npm install
```

**Problem**: Angular compilation errors
```
Solution: Delete node_modules and reinstall:
rm -rf node_modules
npm install
```

**Problem**: API calls fail (CORS or 401 errors)
```
Solution: 
1. Ensure backend is running on port 8080
2. Check proxy.conf.json is correct
3. Verify JWT token is being sent in headers
```

---

## Verification Checklist

After starting both applications, verify:

- [ ] Backend running on `http://localhost:8080`
- [ ] Frontend running on `http://localhost:4200`
- [ ] Login page loads with Nano Banana theme (navy blue + gold)
- [ ] Can login with credentials
- [ ] JWT token stored in browser localStorage
- [ ] Gold ticker shows rates at the top
- [ ] Sidebar navigation works
- [ ] API calls succeed (check browser DevTools Network tab)

---

## Development Workflow

### Making Backend Changes

1. Edit Java files
2. Spring Boot will auto-reload (if using spring-boot-devtools)
3. Or restart: `Ctrl+C` then `.\mvnw spring-boot:run`

### Making Frontend Changes

1. Edit TypeScript/HTML/SCSS files
2. Angular will auto-reload in browser
3. Check browser console for errors

### Database Changes

- H2 database is in-memory by default
- Data resets on each restart
- To persist data, configure file-based H2 in application.properties

---

## Quick Commands Reference

```powershell
# Backend
cd C:\Silicon\Examples\jewelry_shop\jewelry
.\mvnw spring-boot:run                    # Start backend
.\mvnw clean install -DskipTests          # Build backend
.\mvnw test                               # Run tests

# Frontend
cd C:\Silicon\Examples\jewelry_shop\jewelry\frontend
npm install                               # Install dependencies
npm start                                 # Start dev server
npm run build                             # Build production

# Full Build
cd C:\Silicon\Examples\jewelry_shop\jewelry
.\mvnw clean package                      # Build everything
java -jar target\jewelry.pos-0.0.1-SNAPSHOT.jar  # Run JAR
```

---

## Next Steps After Successful Startup

1. **Explore the UI** - Navigate through all screens
2. **Test POS functionality** - Create a sale with trade-in
3. **Check Dashboard** - View metrics (Manager only)
4. **Test Inventory** - Add/edit products
5. **Review Swagger API** - `http://localhost:8080/swagger-ui/index.html`
6. **Check H2 Console** - `http://localhost:8080/h2-console` (SUPER_ADMIN only)

---

## Important Notes

- **First startup takes longer** due to dependency downloads
- **Keep both terminals open** when running in development mode
- **Check browser console** for frontend errors
- **Check terminal output** for backend errors
- **JWT tokens expire after 24 hours** - you'll need to login again

---

## Getting Help

If you encounter issues:
1. Check the terminal output for error messages
2. Check browser DevTools Console (F12)
3. Check browser DevTools Network tab for failed API calls
4. Review the JWT_IMPLEMENTATION.md for authentication details
5. Review the frontend README.md for Angular-specific issues
