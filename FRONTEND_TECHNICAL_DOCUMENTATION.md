# Frontend Technical Documentation - Jewelry POS System

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Services Layer](#services-layer)
5. [Components Overview](#components-overview)
6. [State Management](#state-management)
7. [Routing & Navigation](#routing--navigation)
8. [API Integration](#api-integration)
9. [Authentication & Authorization](#authentication--authorization)
10. [UI/UX Patterns](#uiux-patterns)
11. [Development Guide](#development-guide)

---

## Architecture Overview

### Design Pattern
The application follows **Angular Standalone Components** architecture with:
- **Feature-based module organization**
- **Service-oriented architecture** for API communication
- **Reactive programming** using RxJS
- **Material Design** UI components

### Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│    (Components + Templates + Styles)    │
├─────────────────────────────────────────┤
│         Services Layer                  │
│  (Business Logic + API Communication)   │
├─────────────────────────────────────────┤
│         Core Layer                      │
│   (Guards + Interceptors + Models)      │
├─────────────────────────────────────────┤
│         HTTP Client                     │
│    (Angular HttpClient + Proxy)         │
└─────────────────────────────────────────┘
```

---

## Technology Stack

### Core Technologies
- **Angular 17.3** - Frontend framework
- **TypeScript 5.4** - Programming language
- **RxJS 7.8** - Reactive programming
- **Angular Material 17.3** - UI component library

### Build Tools
- **Angular CLI** - Development server & build tool
- **Webpack** - Module bundler (via Angular CLI)
- **TypeScript Compiler** - Code compilation

### Development Tools
- **Jasmine & Karma** - Testing framework
- **ESLint** - Code linting
- **Proxy Configuration** - API proxying to backend

---

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/                    # Core functionality
│   │   │   ├── guards/              # Route guards
│   │   │   │   └── auth.guard.ts
│   │   │   ├── interceptors/        # HTTP interceptors
│   │   │   │   └── auth.interceptor.ts
│   │   │   └── services/            # Core services
│   │   │       ├── auth.service.ts
│   │   │       ├── dashboard.service.ts
│   │   │       ├── inventory.service.ts
│   │   │       ├── pos.service.ts
│   │   │       ├── sales.service.ts
│   │   │       ├── old-gold.service.ts
│   │   │       ├── gold-rate.service.ts
│   │   │       ├── report.service.ts
│   │   │       ├── user.service.ts
│   │   │       └── config.service.ts
│   │   │
│   │   ├── features/                # Feature modules
│   │   │   ├── auth/
│   │   │   │   └── login/
│   │   │   ├── dashboard/
│   │   │   ├── pos/
│   │   │   ├── inventory/
│   │   │   ├── old-gold/
│   │   │   ├── sales-history/
│   │   │   ├── reports/
│   │   │   ├── settings/
│   │   │   └── user-management/
│   │   │
│   │   ├── shared/                  # Shared components
│   │   │   └── layout/
│   │   │       ├── main-layout/
│   │   │       └── gold-ticker/
│   │   │
│   │   ├── app.component.ts         # Root component
│   │   ├── app.config.ts            # App configuration
│   │   └── app.routes.ts            # Route definitions
│   │
│   ├── styles.scss                  # Global styles
│   ├── index.html                   # HTML entry point
│   └── main.ts                      # Application bootstrap
│
├── angular.json                     # Angular configuration
├── package.json                     # Dependencies
├── tsconfig.json                    # TypeScript config
└── proxy.conf.json                  # Proxy configuration
```

---

## Services Layer

### 1. Dashboard Service

**File:** `core/services/dashboard.service.ts`

**Purpose:** Manages dashboard analytics and metrics

**Methods:**

```typescript
// Get today's dashboard data
getTodayDashboard(): Observable<DashboardData>

// Get stats for custom date range
getStatsForDateRange(fromDate: string, toDate: string): Observable<DashboardStatsDTO>

// Get top selling products
getTopProducts(fromDate?: string, toDate?: string, limit?: number): Observable<TopProductDTO[]>

// Get user performance metrics
getUserPerformance(fromDate?: string, toDate?: string): Observable<UserPerformanceDTO[]>

// Get daily sales trends
getSalesTrends(fromDate?: string, toDate?: string): Observable<SalesTrendDTO[]>
```

**Interfaces:**

```typescript
interface DashboardData {
  salesRevenue: number;
  cost: number;
  netProfit: number;
  salesCount: number;
  itemsInStock: number;
  oldGoldBoughtWeight: number;
  oldGoldExpense: number;
  purificationIncome: number;
  scrapInventory: { KARAT_21: number; KARAT_18: number };
  lastUpdated: string;
}

interface TopProductDTO {
  productId: string;
  barcode: string;
  modelName: string;
  salesCount: number;
  totalRevenue: number;
  totalWeight: number;
}

interface UserPerformanceDTO {
  userId: string;
  username: string;
  fullName: string;
  salesCount: number;
  totalRevenue: number;
  averageSaleValue: number;
}

interface SalesTrendDTO {
  date: string;
  salesCount: number;
  totalRevenue: number;
  netProfit: number;
}
```

---

### 2. Inventory Service

**File:** `core/services/inventory.service.ts`

**Purpose:** Manages product inventory operations

**Methods:**

```typescript
// Get all products with pagination
getAllProducts(page?: number, size?: number): Observable<PageResponse<Product>>

// Get single product by ID
getProductById(id: string): Observable<Product>

// Get product by barcode (for POS scanning)
getProductByBarcode(barcode: string): Observable<Product>

// Search products by query
searchProducts(query: string): Observable<Product[]>

// Create new product
createProduct(product: ProductRequest): Observable<void>

// Update existing product
updateProduct(id: string, product: ProductRequest): Observable<void>

// Delete product
deleteProduct(id: string): Observable<void>
```

**Interfaces:**

```typescript
interface Product {
  id: string;
  barcode: string;
  modelName: string;
  purityEnum: string;        // K24, K21, K18
  type: string;              // RING, NECKLACE, BRACELET, etc.
  grossWeight: number;
  makingCharge: number;
  description?: string;
  status: string;            // AVAILABLE, SOLD, RESERVED
  costPrice: number;
  estimatedPrice: number;    // Calculated field
  createdDate: string;
}

interface ProductRequest {
  barcode: string;
  modelName: string;
  purityEnum: string;
  type: string;
  grossWeight: number;
  makingCharge: number;
  description?: string;
  costPrice: number;
}

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
```

---

### 3. POS Service

**File:** `core/services/pos.service.ts`

**Purpose:** Handles point-of-sale operations

**Methods:**

```typescript
// Scan product by barcode
getProductByBarcode(barcode: string): Observable<Product>

// Create sale transaction
createSale(saleRequest: SaleRequest): Observable<void>

// Get sales history
getSales(page?: number, size?: number): Observable<any>

// Get single sale details
getSaleById(id: string): Observable<SaleResponse>

// Void/refund sale
voidSale(id: string): Observable<void>
```

**Interfaces:**

```typescript
interface SaleRequest {
  barcodes: string[];              // Product barcodes to sell
  currentGoldRate: number;         // Gold rate at time of sale
  customerName: string;
  customerPhone?: string;
  tradeInItems?: OldGoldItem[];    // Optional old gold trade-in
}

interface OldGoldItem {
  purity: string;                  // KARAT_21, KARAT_18
  weight: number;
  buyRate: number;
  customerNationalId: string;
  customerPhoneNumber?: string;
  description?: string;
}

interface SaleResponse {
  id: string;
  date: string;
  totalAmount: number;
  oldGoldTotalValue: number;
  netCashPaid: number;
  createdBy: string;
  customerName: string;
  customerPhone: string;
  items: SaleItemDTO[];
}
```

---

### 4. Sales Service

**File:** `core/services/sales.service.ts`

**Purpose:** Manages sales history and reporting

**Methods:**

```typescript
// Get all sales with filters
getAllSales(
  page?: number, 
  size?: number, 
  query?: string,
  fromDate?: string, 
  toDate?: string
): Observable<PageResponse<Sale>>

// Get single sale
getSaleById(id: string): Observable<Sale>

// Void sale
voidSale(id: string): Observable<void>
```

---

### 5. Old Gold Service

**File:** `core/services/old-gold.service.ts`

**Purpose:** Manages old gold purchases and scrap purification

**Methods:**

```typescript
// Buy old gold for cash
buyCash(request: OldGoldPurchaseRequest): Observable<any>

// Purify scrap (sell to factory)
purify(request: PurificationRequest): Observable<any>

// Get current scrap inventory
getScrapInventory(): Observable<ScrapInventory[]>
```

**Interfaces:**

```typescript
interface OldGoldPurchaseRequest {
  purity: string;
  weight: number;
  buyRate: number;
  customerNationalId: string;
  customerPhoneNumber?: string;
  description?: string;
}

interface PurificationRequest {
  purity: string;
  weightToSell: number;
  cashReceived: number;
  factoryName: string;
}
```

---

### 6. User Service

**File:** `core/services/user.service.ts`

**Purpose:** Manages user accounts and permissions

**Methods:**

```typescript
// Get all users with pagination
getAllUsers(page?: number, size?: number): Observable<PageResponse<User>>

// Get single user
getUserById(id: string): Observable<User>

// Create new user
createUser(user: CreateUserRequest): Observable<void>

// Update user
updateUser(id: string, user: UpdateUserRequest): Observable<User>

// Delete user
deleteUser(id: string): Observable<void>

// Seed initial data
seedData(): Observable<string>

// Trigger database backup
triggerBackup(): Observable<string>
```

---

### 7. Config Service

**File:** `core/services/config.service.ts`

**Purpose:** Manage system configuration toggles used by the Settings screen.

**Methods:**

```typescript
getGoldAutoUpdateStatus(): Observable<{ enabled: boolean }>
setGoldAutoUpdateStatus(enabled: boolean): Observable<void>

getHardwareStatus(): Observable<{ enabled: boolean }>
setHardwareStatus(enabled: boolean): Observable<void>
```

---

## Components Overview

### 1. Dashboard Component

**Path:** `features/dashboard/dashboard.component.ts`

**Purpose:** Display business metrics and analytics

**Features:**
- Today's revenue, profit, and sales count
- Old gold expense tracking
- Purification income
- Scrap inventory levels with circular progress indicators
- Date range filtering (implemented)
- Top products table (implemented)
- User performance table (implemented)
- Sales trends table (implemented)

**Key Methods:**
```typescript
loadDashboard(): void              // Load today's data
formatCurrency(value: number): string
getScrapPercentage(weight: number): number
getScrapColor(percentage: number): string
```

---

### 2. POS Component

**Path:** `features/pos/pos.component.ts`

**Purpose:** Point-of-sale interface for processing sales

**Features:**
- Barcode scanning
- Cart management
- Old gold trade-in support
- Real-time total calculation
- Customer information capture
- Checkout processing

**Key Methods:**
```typescript
onBarcodeSubmit(): void           // Scan product
addToCart(product: Product): void
removeFromCart(item: CartItem): void
openAddOldGoldDialog(): void
getSubtotal(): number
getOldGoldTotal(): number
getNetTotal(): number
checkout(): void
clearCart(): void
```

**Important Notes:**
- Uses backend gold rate from `/api/gold-rates/current`
- Customer name is required; customer phone is optional
- Sends `barcodes` array and `tradeInItems` (DTO-aligned)

---

### 3. Inventory Component

**Path:** `features/inventory/inventory.component.ts`

**Purpose:** Manage product inventory

**Features:**
- Product listing with pagination
- Add/Edit/Delete products
- Product search
- Status indicators
- Barcode display

**Key Methods:**
```typescript
loadProducts(): void
openAddProductDialog(): void
openEditDialog(product: Product): void
deleteProduct(product: Product): void
```

**Important Notes:**
- Pagination (`PageResponse<Product>`) implemented
- Search UI integrated using `/api/products/search` (non-paginated)
- Add/Edit dialog supports new product fields

---

### 4. Sales History Component

**Path:** `features/sales-history/sales-history.component.ts`

**Purpose:** View sales list, filter/search, open sale details dialog, and void sales.

**Features:**
- Paginated sales list
- Search by customer name/phone/sale id
- Date range filtering
- Sale details dialog
- Void sale action

---

### 5. User Management Component

**Path:** `features/user-management/user-management.component.ts`

**Purpose:** Super Admin/Admin user CRUD and system maintenance actions.

**Features:**
- User list + pagination
- Create/Edit user dialog
- Delete user
- Seed data
- Trigger backup

---

### 6. Settings Component (Enhanced)

**Path:** `features/settings/settings.component.ts`

**Purpose:** System configuration and gold rate management.

**Features:**
- Gold rate updates (24K/21K/18K)
- Gold rate history table (collapsible, paginated)
- System config toggles (gold auto-update, hardware)
- System information display

---

### 7. Inventory Component (Enhanced)

**Path:** `features/inventory/inventory.component.ts`

**Purpose:** Product inventory management with label printing.

**Features:**
- Product CRUD operations
- Search and pagination
- **Label printing** (downloads ZPL file for each product)

---

### 8. Sale Details Dialog (Enhanced)

**Path:** `features/sales-history/sale-details-dialog/sale-details-dialog.component.ts`

**Purpose:** View sale details with receipt download.

**Features:**
- Sale information display
- **Receipt PDF download** button

---

### 4. Old Gold Component

**Path:** `features/old-gold/old-gold.component.ts`

**Purpose:** Manage old gold purchases and scrap purification

**Features:**
- Buy old gold for cash
- Purify scrap (sell to factory)
- View scrap inventory
- Transaction history

**Important Notes:**
- **NEEDS UPDATE:** Add phone number field to purchase form
- Update field names to match backend (purity instead of karat)

---

## State Management

### Current Approach
- **Component-level state** using class properties
- **Service injection** for data fetching
- **RxJS Observables** for async operations

### Best Practices

```typescript
// ✅ Good: Subscribe in component, unsubscribe on destroy
ngOnInit() {
  this.subscription = this.service.getData().subscribe(
    data => this.data = data
  );
}

ngOnDestroy() {
  this.subscription?.unsubscribe();
}

// ✅ Better: Use async pipe in template
data$ = this.service.getData();

// Template: {{ data$ | async }}
```

---

## Routing & Navigation

### Route Configuration

**File:** `app.routes.ts`

```typescript
const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'pos', component: PosComponent },
      { path: 'inventory', component: InventoryComponent },
      { path: 'old-gold', component: OldGoldComponent },
      { path: 'sales-history', component: SalesHistoryComponent },
      { path: 'reports', component: ReportsComponent },
      { path: 'settings', component: SettingsComponent },
      { path: 'users', component: UserManagementComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];
```

### Navigation Guards

**Auth Guard:** Protects routes requiring authentication

```typescript
// Checks if user has valid JWT token
// Redirects to /login if not authenticated
```

---

## API Integration

### Proxy Configuration

**File:** `proxy.conf.json`

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

### HTTP Interceptor

**File:** `core/interceptors/auth.interceptor.ts`

**Purpose:** Automatically attach JWT token to requests

```typescript
// Adds Authorization header to all API requests
// Format: Bearer <token>
```

### Error Handling Pattern

```typescript
this.service.getData().subscribe({
  next: (data) => {
    // Handle success
    this.data = data;
  },
  error: (error) => {
    // Handle error
    this.snackBar.open('Error loading data', 'Close', { duration: 3000 });
    console.error('API Error:', error);
  }
});
```

---

## Authentication & Authorization

### Login Flow

1. User enters credentials
2. POST `/api/auth/login`
3. Receive JWT token
4. Store token in localStorage
5. Redirect to dashboard

### Token Storage

```typescript
// Store token
localStorage.setItem('jwt_token', token);

// Retrieve token
const token = localStorage.getItem('jwt_token');

// Remove token (logout)
localStorage.removeItem('jwt_token');
```

### Role-Based Access

**Backend Permissions:**
- `USER_MANAGE` (P1) - User management
- `PRODUCT_MANAGE` (P2) - Inventory, reports, settings
- `SALE_EXECUTE` (P3) - POS operations

**Frontend Implementation:**
- Currently basic (auth guard only)
- Menu items are role-gated in `main-layout.component.ts`
- Backend remains authoritative via `@PreAuthorize` checks

---

## UI/UX Patterns

### Material Design Components

**Used Components:**
- `MatCard` - Content containers
- `MatTable` - Data tables
- `MatButton` - Action buttons
- `MatIcon` - Icons
- `MatDialog` - Modal dialogs
- `MatSnackBar` - Toast notifications
- `MatFormField` - Form inputs
- `MatProgressSpinner` - Loading indicators
- `MatPaginator` - Pagination
- `MatSlideToggle` - Settings toggles
- `MatDatepicker` - Date filters (Dashboard/Reports)

### Loading States

```typescript
// Pattern for loading states
loading = true;

this.service.getData().subscribe({
  next: (data) => {
    this.data = data;
    this.loading = false;
  },
  error: () => {
    this.loading = false;
  }
});
```

### Form Validation

```typescript
// Use Angular Reactive Forms
import { FormBuilder, Validators } from '@angular/forms';

form = this.fb.group({
  barcode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9-]+$/)]],
  modelName: ['', [Validators.required, Validators.maxLength(100)]],
  weight: ['', [Validators.required, Validators.min(0.001)]]
});
```

---

## Development Guide

### Setup

```bash
# Install dependencies
npm install

# Start development server
npm start

# Build for production
npm run build

# Run tests
npm test
```

### Development Server

```bash
ng serve --proxy-config proxy.conf.json
```

Access at: `http://localhost:4200`

### Code Generation

```bash
# Generate component
ng generate component features/my-feature

# Generate service
ng generate service core/services/my-service

# Generate guard
ng generate guard core/guards/my-guard
```

### Coding Standards

**TypeScript:**
- Use strict type checking
- Prefer interfaces over types
- Use readonly for immutable properties
- Avoid `any` type

**Angular:**
- Use standalone components
- Inject dependencies via `inject()` function
- Use signals for reactive state (Angular 17+)
- Prefer OnPush change detection

**Naming Conventions:**
- Components: `PascalCase` + `Component` suffix
- Services: `PascalCase` + `Service` suffix
- Interfaces: `PascalCase` (no prefix)
- Files: `kebab-case`

---

## Performance Optimization

### Lazy Loading

```typescript
// Routes use lazy loading
loadComponent: () => import('./features/dashboard/dashboard.component')
  .then(m => m.DashboardComponent)
```

### Change Detection

```typescript
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush
})
```

### TrackBy Functions

```typescript
// Use trackBy in *ngFor
trackByProductId(index: number, product: Product): string {
  return product.id;
}
```

---

## Testing Strategy

### Unit Tests

```typescript
describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let service: jasmine.SpyObj<DashboardService>;

  beforeEach(() => {
    service = jasmine.createSpyObj('DashboardService', ['getTodayDashboard']);
    component = new DashboardComponent(service);
  });

  it('should load dashboard data', () => {
    const mockData = { revenue: 1000, profit: 200 };
    service.getTodayDashboard.and.returnValue(of(mockData));
    
    component.ngOnInit();
    
    expect(component.dashboardData).toEqual(mockData);
  });
});
```

---

## Troubleshooting

### Common Issues

**1. CORS Errors**
- Ensure proxy configuration is correct
- Check backend CORS settings
- Verify API URL in services

**2. Authentication Failures**
- Check token in localStorage
- Verify token format (Bearer prefix)
- Check token expiration

**3. API 404 Errors**
- Verify endpoint URLs match backend
- Check HTTP method (GET/POST/PUT/DELETE)
- Ensure backend server is running

**5. Access Denied (403)**
- Check the logged-in role (Super Admin vs Manager)
- Some endpoints require `USER_MANAGE` or `PRODUCT_MANAGE`

**4. Type Errors**
- Update interfaces to match backend DTOs
- Check for null/undefined values
- Use optional chaining (?.)

---

## Future Enhancements

### Planned Features

1. **Enhanced Dashboard**
   - Date range picker
   - Interactive charts (Chart.js or ng2-charts)
   - Top products widget
   - User performance metrics

2. **Sales History**
   - Dedicated sales history page
   - Advanced filtering
   - Export to PDF/Excel
   - Receipt printing

3. **User Management**
   - User CRUD interface
   - Role assignment
   - Permission management

4. **Reporting**
   - Z-Report viewer
   - Custom date range reports
   - Export functionality

5. **Real-time Updates**
   - WebSocket integration
   - Live dashboard updates
   - Notification system

---

*Last Updated: 2026-01-30*
*Version: 3.0*
*Angular Version: 17.3*
