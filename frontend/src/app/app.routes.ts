import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: '',
    loadComponent: () => import('./shared/layout/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'pos',
        loadComponent: () => import('./features/pos/pos.component').then(m => m.PosComponent)
      },
      {
        path: 'inventory',
        loadComponent: () => import('./features/inventory/inventory.component').then(m => m.InventoryComponent)
      },
      {
        path: 'old-gold',
        loadComponent: () => import('./features/old-gold/old-gold.component').then(m => m.OldGoldComponent)
      },
      {
        path: 'sales-history',
        loadComponent: () => import('./features/sales-history/sales-history.component').then(m => m.SalesHistoryComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('./features/user-management/user-management.component').then(m => m.UserManagementComponent)
      },
      {
        path: 'reports',
        loadComponent: () => import('./features/reports/reports.component').then(m => m.ReportsComponent)
      },
      {
        path: 'settings',
        loadComponent: () => import('./features/settings/settings.component').then(m => m.SettingsComponent)
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
      },
      {
        path: 'personal-accounts',
        loadComponent: () => import('./features/personal-accounts/personal-accounts.component').then(m => m.PersonalAccountsComponent)
      },
      {
        path: 'personal-persons',
        loadComponent: () => import('./features/personal-persons/personal-persons.component').then(m => m.PersonalPersonsComponent)
      },
      {
        path: 'supplier-accounts',
        loadComponent: () => import('./features/supplier-accounts/supplier-accounts.component').then(m => m.SupplierAccountsComponent)
      },
      {
        path: 'supplier-accounts/:supplierId',
        loadComponent: () => import('./features/supplier-accounts/supplier-accounts-details.component').then(m => m.SupplierAccountsDetailsComponent)
      },
      {
        path: 'home-expenses',
        loadComponent: () => import('./features/home-expenses/home-expenses.component').then(m => m.HomeExpensesComponent)
      },
      {
        path: 'personal-accounts/:personId',
        loadComponent: () => import('./features/personal-accounts/personal-accounts-details.component').then(m => m.PersonalAccountsDetailsComponent)
      },
      {
        path: 'suppliers',
        loadComponent: () => import('./features/suppliers/suppliers.component').then(m => m.SuppliersComponent)
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
