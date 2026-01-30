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
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { GoldTickerComponent } from '../gold-ticker/gold-ticker.component';
import { AuthService } from '../../../core/services/auth.service';
import { I18nService } from '../../../core/services/i18n.service';
import { NotificationService, AppNotification } from '../../../core/services/notification.service';
import { TPipe } from '../../pipes/t.pipe';
import { map } from 'rxjs';

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
    MatSnackBarModule,
    GoldTickerComponent,
    TPipe
  ],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent implements OnInit {
  authService = inject(AuthService);
  router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);
  private notificationService = inject(NotificationService);

  notifications$ = this.notificationService.notifications$;
  unreadCount$ = this.notifications$.pipe(map(items => items.filter(n => !n.read).length));
  
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

    this.scheduleDailyZReportReminder();
  }

  private scheduleDailyZReportReminder(): void {
    const now = new Date();
    const next = new Date(now);
    next.setHours(14, 0, 0, 0);

    if (now.getTime() >= next.getTime()) {
      next.setDate(next.getDate() + 1);
    }

    const ms = next.getTime() - now.getTime();
    window.setTimeout(() => {
      this.fireZReportReminder();
      window.setInterval(() => this.fireZReportReminder(), 24 * 60 * 60 * 1000);
    }, ms);
  }

  private fireZReportReminder(): void {
    const todayKey = new Date().toISOString().slice(0, 10); // YYYY-MM-DD
    const id = `z-report-${todayKey}`;

    const notification: AppNotification = {
      id,
      type: 'REMINDER',
      messageKey: 'notifications.zReportReminder',
      actionLabelKey: 'notifications.openReports',
      actionRoute: '/reports',
      createdAt: new Date().toISOString(),
      read: false
    };

    this.notificationService.add(notification);

    const snack = this.snackBar.open(
      this.i18n.t(notification.messageKey),
      this.i18n.t(notification.actionLabelKey ?? 'notifications.openReports'),
      { duration: 15000 }
    );
    snack.onAction().subscribe(() => {
      this.notificationService.markRead(notification.id);
      if (notification.actionRoute) {
        this.router.navigate([notification.actionRoute]);
      }
    });
  }

  onNotificationClick(n: AppNotification): void {
    this.notificationService.markRead(n.id);
    if (n.actionRoute) {
      this.router.navigate([n.actionRoute]);
    }
  }

  markAllNotificationsRead(): void {
    this.notificationService.markAllRead();
  }

  getFilteredMenuItems() {
    return this.menuItems.filter(item => item.roles.includes(this.role));
  }

  isActive(route: string): boolean {
    return this.router.url === route;
  }

  openProfile(): void {
    this.router.navigate(['/profile']);
  }

  openSettings(): void {
    this.router.navigate(['/settings']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
