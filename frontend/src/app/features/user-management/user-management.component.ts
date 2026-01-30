import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { UserService, User } from '../../core/services/user.service';
import { UserDialogComponent } from './user-dialog/user-dialog.component';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatPaginatorModule,
    MatChipsModule,
    TPipe
  ],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss']
})
export class UserManagementComponent implements OnInit {
  private userService = inject(UserService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);

  users: User[] = [];
  displayedColumns = ['username', 'fullName', 'roles', 'enabled', 'createdDate', 'actions'];

  loading = true;
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAllUsers(this.currentPage, this.pageSize).subscribe({
      next: (page) => {
        this.users = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('users.loadError'), this.i18n.t('common.close'), { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadUsers();
  }

  openCreateUser(): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '600px',
      data: null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers();
      }
    });
  }

  openEditUser(user: User): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '600px',
      data: user
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers();
      }
    });
  }

  deleteUser(user: User): void {
    if (confirm(this.i18n.t('users.confirmDelete', { username: user.username }))) {
      this.userService.deleteUser(user.id).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('users.deleted'), this.i18n.t('common.close'), { duration: 3000 });
          this.loadUsers();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('users.deleteError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  seedData(): void {
    if (confirm(this.i18n.t('users.seedConfirm'))) {
      this.userService.seedData().subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('users.seedOk'), this.i18n.t('common.close'), { duration: 3000 });
          this.loadUsers();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('users.seedFail'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  triggerBackup(): void {
    this.userService.triggerBackup().subscribe({
      next: (msg) => {
        this.snackBar.open(msg, this.i18n.t('common.close'), { duration: 5000 });
      },
      error: () => {
        this.snackBar.open(this.i18n.t('users.backupFail'), this.i18n.t('common.close'), { duration: 3000 });
      }
    });
  }

  formatDateTime(dateStr: string): string {
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    return new Date(dateStr).toLocaleString(locale, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  roleLabel(role: string): string {
    return role.replace('ROLE_', '').replace('_', ' ');
  }
}
