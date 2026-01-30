# JWT Authentication Implementation

## Overview
The Spring Boot application has been updated from HTTP Basic Authentication to JWT (JSON Web Token) authentication to match the Angular frontend implementation.

## Changes Made

### 1. Dependencies Added (`pom.xml`)
```xml
<jjwt.version>0.12.3</jjwt.version>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>${jjwt.version}</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>${jjwt.version}</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>${jjwt.version}</version>
    <scope>runtime</scope>
</dependency>
```

### 2. New Classes Created

#### `JwtUtil.java`
- Generates JWT tokens with user details and roles
- Validates JWT tokens
- Extracts username and claims from tokens
- Default expiration: 24 hours (86400000ms)
- Secret key: Configurable via `jwt.secret` property

#### `JwtAuthenticationFilter.java`
- Intercepts all HTTP requests
- Extracts JWT token from `Authorization: Bearer <token>` header
- Validates token and sets authentication in SecurityContext
- Extends `OncePerRequestFilter` to ensure single execution per request

#### `LoginRequest.java` & `LoginResponse.java`
- DTOs for login endpoint
- `LoginResponse` contains: token, username, and primary role

### 3. Updated Classes

#### `AuthController.java`
- **NEW**: `POST /api/auth/login` endpoint
  - Accepts username and password
  - Returns JWT token with user details
  - Returns 401 for invalid credentials
- **EXISTING**: `GET /api/auth/me` endpoint (unchanged)

#### `SecurityConfig.java`
- Removed: `.httpBasic(withDefaults())`
- Added: JWT authentication filter before `UsernamePasswordAuthenticationFilter`
- Added: Stateless session management (`SessionCreationPolicy.STATELESS`)
- Added: `/api/auth/login` to public endpoints (permitAll)
- Injected: `JwtAuthenticationFilter`

## Configuration

### application.properties (Optional)
```properties
# JWT Configuration (optional - defaults provided)
jwt.secret=MySecretKeyForJewelryPOSSystemThatIsAtLeast256BitsLongForHS256Algorithm
jwt.expiration=86400000
```

**Note**: The secret key must be at least 256 bits (32 characters) for HS256 algorithm.

## API Usage

### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "ROLE_MANAGER"
}
```

### Authenticated Requests
```bash
GET /api/dashboard/today
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Frontend Integration

The Angular application automatically:
1. Stores JWT token in `localStorage` upon successful login
2. Attaches token to all API requests via `authInterceptor`
3. Redirects to login page if token is missing or invalid

## Security Features

- ✅ Stateless authentication (no server-side sessions)
- ✅ Token expiration (24 hours by default)
- ✅ Role-based access control (RBAC) preserved
- ✅ Permission-based authorization preserved
- ✅ CSRF protection disabled (not needed for stateless JWT)
- ✅ CORS-ready for Angular frontend

## Testing

### Test Login Endpoint
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

### Test Protected Endpoint
```bash
curl http://localhost:8080/api/dashboard/today \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Migration Notes

- **No database changes required** - existing user/role/permission structure unchanged
- **Backward compatible** - all existing endpoints work the same way
- **H2 Console** - Still accessible with HTTP Basic for SUPER_ADMIN role
- **Swagger UI** - Still publicly accessible at `/swagger-ui/`
