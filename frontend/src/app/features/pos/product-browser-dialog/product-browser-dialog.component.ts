import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { PosService, Product } from '../../../core/services/pos.service';
import { I18nService } from '../../../core/services/i18n.service';
import { TPipe } from '../../../shared/pipes/t.pipe';

@Component({
  selector: 'app-product-browser-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatPaginatorModule,
    MatSnackBarModule,
    TPipe
  ],
  templateUrl: './product-browser-dialog.component.html',
  styleUrls: ['./product-browser-dialog.component.scss']
})
export class ProductBrowserDialogComponent implements OnInit {
  private posService = inject(PosService);
  private snackBar = inject(MatSnackBar);
  private dialogRef = inject(MatDialogRef<ProductBrowserDialogComponent>);
  private i18n = inject(I18nService);

  products: Product[] = [];
  displayedColumns = ['barcode', 'modelName', 'type', 'purityEnum', 'grossWeight', 'estimatedPrice', 'actions'];
  loading = false;
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;

  searchQuery = '';
  selectedPurity: string | null = null;
  selectedType: string | null = null;
  minWeight: number | null = null;
  maxWeight: number | null = null;
  createdFrom: Date | null = null;
  createdTo: Date | null = null;

  purities = ['K24', 'K21', 'K18'];
  types = [
    'RING',
    'NECKLACE',
    'PENDANT',
    'CHAIN',
    'BRACELET',
    'BANGLE',
    'EARRING',
    'ANKLET',
    'BROOCH',
    'CHARM',
    'SET',
    'CUFFLINKS',
    'OTHER'
  ];

  ngOnInit(): void {
    this.searchProducts();
  }

  searchProducts(page: number = 0): void {
    this.loading = true;

    let createdFrom: string | null = null;
    let createdTo: string | null = null;

    if (this.createdFrom) {
      const fromDate = new Date(this.createdFrom);
      createdFrom = `${fromDate.getFullYear()}-${String(fromDate.getMonth() + 1).padStart(2, '0')}-${String(fromDate.getDate()).padStart(2, '0')}T00:00:00`;
    }
    if (this.createdTo) {
      const toDate = new Date(this.createdTo);
      createdTo = `${toDate.getFullYear()}-${String(toDate.getMonth() + 1).padStart(2, '0')}-${String(toDate.getDate()).padStart(2, '0')}T23:59:59`;
    }

    this.posService.searchProducts(
      this.searchQuery.trim() || undefined,
      this.selectedPurity || undefined,
      this.selectedType || undefined,
      this.minWeight || undefined,
      this.maxWeight || undefined,
      createdFrom || undefined,
      createdTo || undefined,
      page,
      this.pageSize
    ).subscribe({
      next: (response) => {
        this.products = response.content;
        this.totalElements = response.totalElements;
        this.currentPage = response.number;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('pos.browser.errorLoading'), this.i18n.t('common.close'), { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.searchProducts(this.currentPage);
  }

  clearFilters(): void {
    this.searchQuery = '';
    this.selectedPurity = null;
    this.selectedType = null;
    this.minWeight = null;
    this.maxWeight = null;
    this.createdFrom = null;
    this.createdTo = null;
    this.searchProducts();
  }

  selectProduct(product: Product): void {
    this.dialogRef.close(product);
  }

  cancel(): void {
    this.dialogRef.close();
  }

  getTypeLabel(type: string): string {
    return this.i18n.t(`jewelryType.${type}`);
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }
}
