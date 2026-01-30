import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SalesService, Sale } from '../../core/services/sales.service';
import { SaleDetailsDialogComponent } from './sale-details-dialog/sale-details-dialog.component';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

@Component({
  selector: 'app-sales-history',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogModule,
    MatSnackBarModule,
    MatPaginatorModule,
    MatTooltipModule,
    TPipe
  ],
  templateUrl: './sales-history.component.html',
  styleUrls: ['./sales-history.component.scss']
})
export class SalesHistoryComponent implements OnInit {
  private salesService = inject(SalesService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);

  sales: Sale[] = [];
  displayedColumns = ['id', 'date', 'customerName', 'customerPhone', 'totalAmount', 'netCashPaid', 'createdBy', 'actions'];
  loading = true;

  // Pagination
  totalElements = 0;
  pageSize = 20;
  currentPage = 0;

  // Filters
  searchQuery = '';
  fromDate: Date | null = null;
  toDate: Date | null = null;

  ngOnInit(): void {
    this.loadSales();
  }

  loadSales(): void {
    this.loading = true;
    const fromDateStr = this.fromDate ? this.formatDate(this.fromDate) : undefined;
    const toDateStr = this.toDate ? this.formatDate(this.toDate) : undefined;

    this.salesService.getAllSales(
      this.currentPage,
      this.pageSize,
      this.searchQuery || undefined,
      fromDateStr,
      toDateStr
    ).subscribe({
      next: (response) => {
        this.sales = response.content;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('sales.loadError'), this.i18n.t('common.close'), { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadSales();
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadSales();
  }

  clearFilters(): void {
    this.searchQuery = '';
    this.fromDate = null;
    this.toDate = null;
    this.currentPage = 0;
    this.loadSales();
  }

  viewSaleDetails(sale: Sale): void {
    this.dialog.open(SaleDetailsDialogComponent, {
      width: '700px',
      data: sale
    });
  }

  voidSale(sale: Sale): void {
    if (confirm(this.i18n.t('sales.confirmVoid', { id: sale.id }))) {
      this.salesService.voidSale(sale.id).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('sales.voidSuccess'), this.i18n.t('common.close'), { duration: 3000 });
          this.loadSales();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('sales.voidError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  formatCurrency(value: number): string {
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    return value.toLocaleString(locale, { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
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
}
