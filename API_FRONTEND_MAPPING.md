# API to Frontend Mapping - AL Mohamadia Jewelry POS

## Backend API Endpoints vs Frontend Expectations

### ✅ Authentication APIs
| Backend Endpoint | Frontend Service | Status |
|-----------------|------------------|--------|
| `POST /api/auth/login` | `auth.service.ts` | ✅ Match |
| `GET /api/auth/me` | `auth.service.ts` | ✅ Match |

---

### ✅ Dashboard APIs
| Backend Endpoint | Frontend Service | Status | Notes |
|-----------------|------------------|--------|-------|
| `GET /api/dashboard/today` | `dashboard.service.ts` | ✅ Match | Added endpoint |
| `GET /api/dashboard/stats` | Not used | ⚠️ Extra | Alternative endpoint |

**Frontend Expects:**
```typescript
interface DashboardData {
  revenue: number;
  netProfit: number;
  purificationIncome: number;
  oldGoldExpense: number;
  scrapInventory: {
    KARAT_21: number;
    KARAT_18: number;
  };
}
```

**Backend Returns:** `Map<String, Object>` from `getTodayStats()`

---

### ✅ Gold Rate APIs
| Backend Endpoint | Frontend Service | Status | Notes |
|-----------------|------------------|--------|-------|
| `GET /api/gold-rates/current` | `gold-rate.service.ts` | ✅ Match | Added endpoint |
| `GET /api/gold-rates/latest` | Not used | ⚠️ Extra | Alternative endpoint |
| `POST /api/gold-rates` | `gold-rate.service.ts` | ❌ Mismatch | Frontend expects PUT |
| `GET /api/gold-rates/history` | Not used | ⚠️ Extra | Backend only |

**Frontend Expects:**
```typescript
interface GoldRate {
  karat: string;
  buyRate: number;
  sellRate: number;
  lastUpdated: string;
}
```

**Backend Returns:** `GoldRate` entity

---

### ⚠️ POS/Sales APIs
| Backend Endpoint | Frontend Service | Status | Notes |
|-----------------|------------------|--------|-------|
| `POST /api/pos/scan/{barcode}` | Not used | ❌ Missing | Frontend uses different approach |
| `POST /api/pos/checkout` | Not used | ❌ Missing | Frontend expects `/api/sales` |
| `GET /api/pos/sales` | Not used | ❌ Missing | Frontend expects `/api/sales` |
| `GET /api/pos/sales/{id}` | Not used | ❌ Missing | Frontend expects `/api/sales/{id}` |
| `DELETE /api/pos/sales/{id}` | Not used | ❌ Missing | Frontend expects `/api/sales/{id}` |

**Frontend Expects:**
```typescript
POST /api/sales
GET /api/sales
GET /api/products/barcode/{barcode}
```

---

### ⚠️ Inventory/Products APIs
| Backend Endpoint | Frontend Service | Status | Notes |
|-----------------|------------------|--------|-------|
| `GET /api/inventory` | `inventory.service.ts` | ❌ Mismatch | Frontend expects `/api/products` |
| `POST /api/inventory` | `inventory.service.ts` | ❌ Mismatch | Frontend expects `/api/products` |
| `PUT /api/inventory/{id}` | `inventory.service.ts` | ❌ Mismatch | Frontend expects `/api/products/{id}` |
| `DELETE /api/inventory/{id}` | `inventory.service.ts` | ❌ Mismatch | Frontend expects `/api/products/{id}` |
| `GET /api/inventory/search` | Not used | ⚠️ Extra | Backend only |

**Frontend Expects:**
```typescript
GET /api/products
POST /api/products
GET /api/products/{id}
PUT /api/products/{id}
DELETE /api/products/{id}
GET /api/products/barcode/{barcode}
```

---

### ✅ Old Gold APIs
| Backend Endpoint | Frontend Service | Status | Notes |
|-----------------|------------------|--------|-------|
| `POST /api/old-gold/buy` | `old-gold.service.ts` | ✅ Match | |
| `POST /api/old-gold/purify` | `old-gold.service.ts` | ✅ Match | |
| `GET /api/old-gold/scrap-inventory` | `old-gold.service.ts` | ❌ Missing | Frontend expects this |

---

### ⚠️ Reports APIs
| Backend Endpoint | Frontend Service | Status | Notes |
|-----------------|------------------|--------|-------|
| `GET /api/reports/z-report` | `report.service.ts` | ✅ Match | Returns DTO, not PDF |
| `GET /api/reports/transactions` | `report.service.ts` | ❌ Missing | Frontend expects this |
| `GET /api/reports/receipt/{saleId}` | Not used | ⚠️ Extra | Backend only |
| `GET /api/reports/label/{barcode}` | Not used | ⚠️ Extra | Backend only |

---

## Required Backend Changes

### 1. Add Sales Controller Aliases
```java
@RestController
@RequestMapping("/api/sales")  // Add this controller
```

### 2. Add Products Controller Aliases
```java
@RestController
@RequestMapping("/api/products")  // Add this controller or alias
```

### 3. Add Scrap Inventory Endpoint
```java
@GetMapping("/api/old-gold/scrap-inventory")
public ResponseEntity<List<ScrapInventory>> getScrapInventory()
```

### 4. Add Transactions Endpoint
```java
@GetMapping("/api/reports/transactions")
public ResponseEntity<List<Transaction>> getRecentTransactions()
```

### 5. Fix Z-Report to Return PDF
```java
@GetMapping(value = "/api/reports/z-report", produces = MediaType.APPLICATION_PDF_VALUE)
public ResponseEntity<byte[]> generateZReport()
```

---

## Priority Fixes

1. **HIGH**: Add `/api/sales` controller (POS won't work)
2. **HIGH**: Add `/api/products` endpoints (Inventory won't work)
3. **MEDIUM**: Add `/api/old-gold/scrap-inventory` (Dashboard scrap status won't work)
4. **MEDIUM**: Add `/api/reports/transactions` (Reports screen won't work)
5. **LOW**: Fix gold rate update method (PUT vs POST)
