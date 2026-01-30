import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, RouterLink } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { GoldTickerComponent } from '../gold-ticker/gold-ticker.component';
import { AuthService } from '../../../core/services/auth.service';
import { TPipe } from '../../pipes/t.pipe';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatToolbarModule,
    MatMenuModule,
    MatBadgeModule,
    GoldTickerComponent,
    TPipe
  ],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent implements OnInit {
  authService = inject(AuthService);
  router = inject(Router);
  
  username: string = '';
  role: string = '';
  
  menuItems = [
    { icon: 'dashboard', labelKey: 'nav.dashboard', route: '/dashboard', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'MANAGER'] },
    { icon: 'point_of_sale', labelKey: 'nav.pos', route: '/pos', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_USER', 'CASHIER', 'MANAGER'] },
    { icon: 'inventory_2', labelKey: 'nav.inventory', route: '/inventory', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'MANAGER'] },
    { icon: 'sell', labelKey: 'nav.oldGold', route: '/old-gold', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'MANAGER'] },
    { icon: 'receipt_long', labelKey: 'nav.salesHistory', route: '/sales-history', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'MANAGER'] },
    { icon: 'assessment', labelKey: 'nav.reports', route: '/reports', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'MANAGER'] },
    { icon: 'manage_accounts', labelKey: 'nav.users', route: '/users', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN'] },
    { icon: 'settings', labelKey: 'nav.settings', route: '/settings', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'MANAGER'] }
  ];

  ngOnInit(): void {
    this.username = this.authService.getUsername() || 'User';
    this.role = this.authService.getRole() || 'ROLE_USER';
  }

  getFilteredMenuItems() {
    return this.menuItems.filter(item => item.roles.includes(this.role));
  }

  isActive(route: string): boolean {
    return this.router.url === route;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
