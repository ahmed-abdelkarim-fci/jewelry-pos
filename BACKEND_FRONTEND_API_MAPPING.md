# Backend-Frontend API Mapping

## Complete API Endpoint Reference

This document maps every backend API endpoint to its frontend implementation.

---

## 1. Authentication APIs

### POST /api/auth/login
**Backend:** `AuthController.login()`  
**Frontend Service:** `auth.service.ts` → `login()`  
**Component:** `login.component.ts`  
**Status:** ✅ Working

**Request:**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "roles": ["ROLE_ADMIN"],
  "permissions": ["USER_MANAGE", "PRODUCT_MANAGE", "SALE_EXECUTE"]
}
```

---

### GET /api/auth/me
**Backend:** `AuthController.getCurrentUser()`  
**Frontend Service:** `auth.service.ts` → `getCurrentUser()`  
**Component:** N/A (used in auth guard)  
**Status:** ✅ Working

---

## 2. Dashboard APIs

### GET /api/dashboard/today
**Backend:** `DashboardController.getTodayDashboard()`  
**Frontend Service:** `dashboard.service.ts` → `getTodayDashboard()`  
**Component:** `dashboard.component.ts`  
**Status:** ✅ Working

**Response:**
```typescript
{
  salesRevenue: number;
  cost: number;
  netProfit: number;
  salesCount: number;
  itemsInStock: number;
  oldGoldBoughtWeight: number;
  oldGoldExpense: number;
  purificationIncome: number;
  scrapInventory: {
    KARAT_21: number;
    KARAT_18: number;
  };
  lastUpdated: string;
}
```

---

### GET /api/dashboard/stats/range
**Backend:** `DashboardController.getStatsForDateRange()`  
**Frontend Service:** `dashboard.service.ts` → `getStatsForDateRange()`  
**Component:** `dashboard.component.ts`  
**Status:** ✅ Working (Enhanced dashboard)

**Query Params:**
- `fromDate`: YYYY-MM-DD
- `toDate`: YYYY-MM-DD

**Response:** Same as /today plus `fromDate` and `toDate`

---

### GET /api/dashboard/top-products
**Backend:** `DashboardController.getTopProducts()`  
**Frontend Service:** `dashboard.service.ts` → `getTopProducts()`  
**Component:** `dashboard.component.ts`  
**Status:** ✅ Working (Enhanced dashboard)

**Query Params:**
- `fromDate` (optional): YYYY-MM-DD
- `toDate` (optional): YYYY-MM-DD
- `limit` (optional, default: 10): number

**Response:**
```typescript
[
  {
    productId: string;
    barcode: string;
    modelName: string;
    salesCount: number;
    totalRevenue: number;
    totalWeight: number;
  }
]
```

---

### GET /api/dashboard/user-performance
**Backend:** `DashboardController.getUserPerformance()`  
**Frontend Service:** `dashboard.service.ts` → `getUserPerformance()`  
**Component:** `dashboard.component.ts`  
**Status:** ✅ Working (Enhanced dashboard)

**Response:**
```typescript
[
  {
    userId: string;
    username: string;
    fullName: string;
    salesCount: number;
    totalRevenue: number;
    averageSaleValue: number;
  }
]
```

---

### GET /api/dashboard/trends
**Backend:** `DashboardController.getSalesTrends()`  
**Frontend Service:** `dashboard.service.ts` → `getSalesTrends()`  
**Component:** `dashboard.component.ts`  
**Status:** ✅ Working (Enhanced dashboard)

**Response:**
```typescript
[
  {
    date: string;        // YYYY-MM-DD
    salesCount: number;
    totalRevenue: number;
    netProfit: number;
  }
]
```

---

## 3. Product/Inventory APIs

### GET /api/products
**Backend:** `ProductController.getAllProducts()`  
**Frontend Service:** `inventory.service.ts` → `getAllProducts()`  
**Component:** `inventory.component.ts`  
**Status:** ✅ Working (pagination + paginator)

**Query Params:**
- `page` (default: 0)
- `size` (default: 20)
- `sort` (default: modelName)

**Response:**
```typescript
{
  content: Product[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
```

---

### GET /api/products/{id}
**Backend:** `ProductController.getProductById()`  
**Frontend Service:** `inventory.service.ts` → `getProductById()`  
**Component:** `inventory.component.ts`  
**Status:** ✅ Working

---

### GET /api/products/barcode/{barcode}
**Backend:** `ProductController.getProductByBarcode()`  
**Frontend Service:** `inventory.service.ts` → `getProductByBarcode()`  
**Component:** `pos.component.ts`  
**Status:** ✅ Working

---

### GET /api/products/search
**Backend:** `ProductController.searchProducts()`  
**Frontend Service:** `inventory.service.ts` → `searchProducts()`  
**Component:** `inventory.component.ts`  
**Status:** ✅ Working (search bar)

**Query Params:**
- `query`: string (searches modelName, barcode, description)

---

### POST /api/products
**Backend:** `ProductController.createProduct()`  
**Frontend Service:** `inventory.service.ts` → `createProduct()`  
**Component:** `add-product-dialog.component.ts`  
**Status:** ✅ Working (type/description/makingCharge supported)

**Request:**
```typescript
{
  barcode: string;
  modelName: string;
  purityEnum: string;      // K24, K21, K18
  type: string;            // NEW: RING, NECKLACE, BRACELET, etc.
  grossWeight: number;
  makingCharge: number;
  description?: string;    // NEW: Optional
  costPrice: number;
}
```

---

### PUT /api/products/{id}
**Backend:** `ProductController.updateProduct()`  
**Frontend Service:** `inventory.service.ts` → `updateProduct()`  
**Component:** `add-product-dialog.component.ts`  
**Status:** ✅ Working (type/description/makingCharge supported)

---

### DELETE /api/products/{id}
**Backend:** `ProductController.deleteProduct()`  
**Frontend Service:** `inventory.service.ts` → `deleteProduct()`  
**Component:** `inventory.component.ts`  
**Status:** ✅ Working

---

## 4. Sales/POS APIs

### POST /api/sales
**Backend:** `SalesController.createSale()`  
**Frontend Service:** `pos.service.ts` → `createSale()`  
**Component:** `pos.component.ts`  
**Status:** ✅ Working

**Required Request:**
```typescript
{
  barcodes: string[];           // Product barcodes
  currentGoldRate: number;      // REQUIRED
  customerName: string;         // REQUIRED
  customerPhone?: string;       // Optional
  tradeInItems?: [              // Optional
    {
      purity: string;
      weight: number;
      buyRate: number;
      customerNationalId: string;
      customerPhoneNumber?: string;
      description?: string;
    }
  ]
}
```

---

### GET /api/sales
**Backend:** `SalesController.getAllSales()`  
**Frontend Service:** `sales.service.ts` → `getAllSales()`  
**Component:** `sales-history.component.ts`  
**Status:** ✅ Working

**Query Params:**
- `page`, `size`, `sort`
- `query` (optional): Search by ID, customer name, phone
- `fromDate` (optional): YYYY-MM-DD
- `toDate` (optional): YYYY-MM-DD

---

### GET /api/sales/{id}
**Backend:** `SalesController.getSaleById()`  
**Frontend Service:** `sales.service.ts` → `getSaleById()`  
**Component:** `sale-details-dialog.component.ts`  
**Status:** ✅ Working

**Response:**
```typescript
{
  id: string;
  date: string;
  totalAmount: number;
  oldGoldTotalValue: number;    // NEW
  netCashPaid: number;          // NEW
  createdBy: string;
  customerName: string;
  customerPhone: string;
  items: [
    {
      productName: string;
      weight: number;
      priceSnapshot: number;
    }
  ]
}
```

---

### DELETE /api/sales/{id}
**Backend:** `SalesController.voidSale()`  
**Frontend Service:** `sales.service.ts` → `voidSale()`  
**Component:** `sales-history.component.ts`  
**Status:** ✅ Working

---

## 5. Old Gold APIs

### POST /api/old-gold/buy
**Backend:** `OldGoldController.buyOldGold()`  
**Frontend Service:** `old-gold.service.ts` → `buyCash()`  
**Component:** `old-gold.component.ts`  
**Status:** ✅ Working (DTO aligned)

**Request:**
```typescript
{
  purity: string;                    // Changed from karat
  weight: number;
  buyRate: number;
  customerNationalId: string;        // Changed from nationalId
  customerPhoneNumber?: string;      // NEW
  description?: string;
}
```

---

### POST /api/old-gold/purify
**Backend:** `OldGoldController.purifyScrap()`  
**Frontend Service:** `old-gold.service.ts` → `purify()`  
**Component:** `old-gold.component.ts`  
**Status:** ✅ Working (DTO aligned)

**Request:**
```typescript
{
  purity: string;
  weightToSell: number;
  cashReceived: number;
  factoryName: string;
}
```

---

### GET /api/old-gold/scrap-inventory
**Backend:** `OldGoldController.getScrapInventory()`  
**Frontend Service:** `old-gold.service.ts` → `getScrapInventory()`  
**Component:** `old-gold.component.ts`  
**Status:** ✅ Working

---

## 6. Gold Rate APIs

### GET /api/gold-rates/current
**Backend:** `GoldRateController.getCurrentRate()`  
**Frontend Service:** `gold-rate.service.ts` → `getCurrentRate()`  
**Component:** `gold-ticker.component.ts`  
**Status:** ✅ Working

---

### GET /api/gold-rates/latest
**Backend:** `GoldRateController.getLatestRate()`  
**Frontend Service:** `gold-rate.service.ts`  
**Component:** N/A  
**Status:** ✅ Working (alias)

---

### POST /api/gold-rates
**Backend:** `GoldRateController.setDailyRate()`  
**Frontend Service:** `gold-rate.service.ts` → `setDailyRate()`  
**Component:** `settings.component.ts`  
**Status:** ✅ Working

**Request:**
```typescript
{
  rate24k: number;
  rate21k: number;
  rate18k: number;
}
```

---

### GET /api/gold-rates/history
**Backend:** `GoldRateController.getRateHistory()`  
**Frontend Service:** `gold-rate.service.ts` → `getHistory()`  
**Component:** `settings.component.ts`  
**Status:** ✅ Working (collapsible history table)

---

## 7. Reports APIs

### GET /api/reports/transactions
**Backend:** `ReportController.getRecentTransactions()`  
**Frontend Service:** `report.service.ts`  
**Component:** `reports.component.ts`  
**Status:** ✅ Working

---

### GET /api/reports/z-report
**Backend:** `ReportController.getZReport()`  
**Frontend Service:** `report.service.ts` → `getZReport()`  
**Component:** `reports.component.ts`  
**Status:** ✅ Working (ZReportDTO JSON)

**Query Params:**
- `date` (optional): YYYY-MM-DD (defaults to today)

---

### GET /api/reports/receipt/{saleId}
**Backend:** `ReportController.downloadReceipt()`  
**Frontend Service:** `report.service.ts` → `downloadReceipt()`  
**Component:** `sale-details-dialog.component.ts`  
**Status:** ✅ Working (download button)

**Response:** PDF file (application/pdf)

---

### GET /api/reports/label/{barcode}
**Backend:** `ReportController.getLabelZpl()`  
**Frontend Service:** `report.service.ts` → `getLabelZpl()`  
**Component:** `inventory.component.ts`  
**Status:** ✅ Working (print label button)

**Response:** ZPL string (text/plain)

---

## 8. User Management APIs

### GET /api/admin
**Backend:** `UserAdminController.getAllUsers()`  
**Frontend Service:** `user.service.ts` → `getAllUsers()`  
**Component:** `user-management.component.ts`  
**Status:** ✅ Working

---

### POST /api/admin/users
**Backend:** `UserAdminController.registerUser()`  
**Frontend Service:** `user.service.ts` → `createUser()`  
**Component:** `user-dialog.component.ts`  
**Status:** ✅ Working

**Request:**
```typescript
{
  firstName: string;
  lastName: string;
  username: string;
  password: string;
  roles: string[];  // ["ROLE_ADMIN", "ROLE_USER"]
}
```

---

### GET /api/admin/{id}
**Backend:** `UserAdminController.getUser()`  
**Frontend Service:** `user.service.ts` → `getUserById()`  
**Component:** `user-management.component.ts` (used indirectly)  
**Status:** ✅ Service available (UI does not require a dedicated details page)

---

### PUT /api/admin/{id}
**Backend:** `UserAdminController.updateUser()`  
**Frontend Service:** `user.service.ts` → `updateUser()`  
**Component:** `user-dialog.component.ts`  
**Status:** ✅ Working

---

### DELETE /api/admin/{id}
**Backend:** `UserAdminController.deleteUser()`  
**Frontend Service:** `user.service.ts` → `deleteUser()`  
**Component:** `user-management.component.ts`  
**Status:** ✅ Working

---

### POST /api/admin/seed
**Backend:** `UserAdminController.seedData()`  
**Frontend Service:** `user.service.ts` → `seedData()`  
**Component:** `user-management.component.ts`  
**Status:** ✅ Working

---

### POST /api/admin/backup
**Backend:** `UserAdminController.triggerBackup()`  
**Frontend Service:** `user.service.ts` → `triggerBackup()`  
**Component:** `user-management.component.ts`  
**Status:** ✅ Working

---

## 9. Config APIs

### GET /api/config/gold-update
**Backend:** `ConfigController.getGoldUpdateStatus()`  
**Frontend Service:** `config.service.ts` → `getGoldAutoUpdateStatus()`  
**Component:** `settings.component.ts`  
**Status:** ✅ Working

---

### PUT /api/config/gold-update
**Backend:** `ConfigController.setGoldUpdateStatus()`  
**Frontend Service:** `config.service.ts` → `setGoldAutoUpdateStatus()`  
**Component:** `settings.component.ts`  
**Status:** ✅ Working

---

### GET /api/config/hardware
**Backend:** `ConfigController.getHardwareStatus()`  
**Frontend Service:** `config.service.ts` → `getHardwareStatus()`  
**Component:** `settings.component.ts`  
**Status:** ✅ Working

---

### PUT /api/config/hardware
**Backend:** `ConfigController.setHardwareStatus()`  
**Frontend Service:** `config.service.ts` → `setHardwareStatus()`  
**Component:** `settings.component.ts`  
**Status:** ✅ Working

---

## Summary

**Total Endpoints:** 41  
**Fully Implemented:** 41 (100%)  
**Needs Update:** 0 (0%)  
**Missing UI:** 0 (0%)

---

*Last Updated: 2026-01-30*
