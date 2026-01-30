# Jewelry POS Frontend - Nano Banana Theme

Angular 17+ standalone components application for the Jewelry POS System.

## Features

- **Dashboard**: Manager-only view with revenue metrics and scrap box status
- **POS Terminal**: Split-screen barcode scanner with cart and trade-in functionality
- **Inventory Manager**: Product management with sortable table and dialogs
- **Old Gold & Purification**: Tabbed interface for cash purchases and factory purification
- **Reports & History**: Transaction list and Z-Report generation

## Tech Stack

- Angular 17+ (Standalone Components)
- Angular Material
- RxJS
- TypeScript

## Theme: Nano Banana

- Primary Background: Deep Navy Blue (#0a192f)
- Secondary Background: Lighter Navy (#112240)
- Accent: Gold (#ffd700)
- Text: White (#e6f1ff) / Light Grey (#8892b0)

## Development

### Prerequisites

- Node.js 20.x
- npm 10.x

### Install Dependencies

```bash
npm install
```

### Run Development Server

```bash
npm start
```

The application will be available at `http://localhost:4200` with API proxy to `http://localhost:8080`.

### Build for Production

```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.

## API Integration

All API calls are proxied through `/api` to the Spring Boot backend at `localhost:8080`.

Authentication uses JWT tokens stored in localStorage and automatically attached to requests via HTTP interceptor.

## Project Structure

```
src/
├── app/
│   ├── core/
│   │   ├── guards/          # Route guards
│   │   ├── interceptors/    # HTTP interceptors
│   │   └── services/        # API services
│   ├── features/            # Feature modules
│   │   ├── auth/
│   │   ├── dashboard/
│   │   ├── pos/
│   │   ├── inventory/
│   │   ├── old-gold/
│   │   ├── reports/
│   │   └── settings/
│   ├── shared/              # Shared components
│   │   └── layout/
│   ├── app.component.ts
│   ├── app.config.ts
│   └── app.routes.ts
├── styles.scss              # Global styles
└── index.html
```

## Building with Maven

The frontend is integrated with the Spring Boot Maven build:

```bash
# From the root project directory
mvn clean package
```

This will:
1. Install Node.js and npm
2. Run `npm install`
3. Build the Angular application
4. Copy the build artifacts to Spring Boot's `static` resources
