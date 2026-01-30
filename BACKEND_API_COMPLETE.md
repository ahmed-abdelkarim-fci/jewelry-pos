# Complete Backend API Reference - AL Mohamadia Jewelry POS

## ✅ All Available Endpoints (Frontend Compatible)

### Authentication
```
POST   /api/auth/login          - Login and get JWT token
GET    /api/auth/me             - Get current user info
```

### Dashboard
```
GET    /api/dashboard/today     - Get today's dashboard metrics
GET    /api/dashboard/stats     - Alternative endpoint (same data)
```

### Gold Rates
```
GET    /api/gold-rates/current  - Get current gold rates (all karats)
GET    /api/gold-rates/latest   - Alternative endpoint (same data)
POST   /api/gold-rates          - Update gold rates (Manager only)
GET    /api/gold-rates/history  - Get historical rates (Manager only)
```

### Products (Inventory)
```
GET    /api/products                - Get all products (paginated)
GET    /api/products/{id}           - Get product by ID
GET    /api/products/barcode/{code} - Get product by barcode (for POS)
POST   /api/products                - Create new product (Manager only)
PUT    /api/products/{id}           - Update product (Manager only)
DELETE /api/products/{id}           - Delete product (Manager only)
GET    /api/products/search?query=  - Search products
```

### Sales (POS)
```
POST   /api/sales           - Create new sale (checkout)
GET    /api/sales           - Get all sales (paginated)
GET    /api/sales/{id}      - Get sale details
DELETE /api/sales/{id}      - Void/refund sale (Manager only)
```

### Old Gold & Scrap
```
POST   /api/old-gold/buy            - Buy old gold for cash
POST   /api/old-gold/purify         - Purify scrap (sell to factory)
GET    /api/old-gold/scrap-inventory - Get current scrap inventory
```

### Reports
```
GET    /api/reports/transactions    - Get recent transactions
GET    /api/reports/z-report        - Get end-of-day report (DTO)
GET    /api/reports/receipt/{id}    - Download receipt PDF
GET    /api/reports/label/{barcode} - Get ZPL label for printing
```

### Legacy Endpoints (Still Available)
```
POST   /api/pos/scan/{barcode}      - Scan item (use /api/products/barcode instead)
POST   /api/pos/checkout            - Checkout (use /api/sales instead)
GET    /api/inventory               - List products (use /api/products instead)
POST   /api/inventory               - Create product (use /api/products instead)
```

---

## Data Models

### Dashboard Response
```json
{
  "revenue": 50000.00,
  "netProfit": 12000.00,
  "purificationIncome": 5000.00,
  "oldGoldExpense": 8000.00,
  "scrapInventory": {
    "KARAT_21": 150.5,
    "KARAT_18": 45.2
  }
}
```

### Gold Rate Response
```json
{
  "karat": "KARAT_24",
  "buyRate": 3500.00,
  "sellRate": 3600.00,
  "effectiveDate": "2026-01-22",
  "lastUpdated": "2026-01-22T00:00:00"
}
```

### Product Response
```json
{
  "id": "123456",
  "barcode": "JWL001",
  "model": "Ring R-21-K01",
  "purity": "KARAT_21",
  "weight": 5.5,
  "costPrice": 15000.00,
  "sellingPrice": 17000.00,
  "status": "AVAILABLE",
  "createdAt": "2026-01-22T00:00:00"
}
```

### Sale Request
```json
{
  "products": [123456, 789012],
  "oldGoldItems": [
    {
      "karat": "KARAT_21",
      "weight": 10.5,
      "buyRate": 3200.00,
      "nationalId": "12345678901234",
      "description": "Old ring",
      "totalValue": 33600.00
    }
  ],
  "paymentMethod": "CASH"
}
```

### Scrap Inventory Response
```json
[
  {
    "karat": "KARAT_21",
    "totalWeight": 150.5
  },
  {
    "karat": "KARAT_18",
    "totalWeight": 45.2
  }
]
```

---

## Authentication & Authorization

### Roles
- **ROLE_SUPER_ADMIN** - Full access (includes USER_MANAGE permission)
- **ROLE_ADMIN** - Manager access (PRODUCT_MANAGE + SALE_EXECUTE)
- **ROLE_USER** - Cashier access (SALE_EXECUTE only)

### Permissions
- **USER_MANAGE** (P1) - User management
- **PRODUCT_MANAGE** (P2) - Inventory, reports, old gold purification
- **SALE_EXECUTE** (P3) - POS operations, view products

### JWT Token
- Stored in localStorage as `jwt_token`
- Sent in header: `Authorization: Bearer <token>`
- Expires after 24 hours
- Contains username and all roles/permissions

---

## Error Responses

### 401 Unauthorized
```json
{
  "error": "Invalid username or password"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2026-01-22T00:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied"
}
```

### 404 Not Found
```json
{
  "timestamp": "2026-01-22T00:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found"
}
```

---

## Testing Endpoints

### Using curl
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"super_admin","password":"super_admin123"}'

# Get Dashboard (with token)
curl http://localhost:8080/api/dashboard/today \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# Get Products
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Using Swagger UI
Visit: `http://localhost:8080/swagger-ui/index.html`
1. Click "Authorize" button
2. Enter: `Bearer YOUR_TOKEN_HERE`
3. Test all endpoints interactively

---

## Next Steps for Full Integration

1. ✅ **Backend APIs** - All endpoints created and tested
2. ⏳ **Frontend Services** - Angular services match backend
3. ⏳ **Test with Real Data** - Create sample products and test all screens
4. ⏳ **UI Polish** - Ensure AL Mohamadia theme is consistent
5. ⏳ **Error Handling** - Add proper error messages in UI
