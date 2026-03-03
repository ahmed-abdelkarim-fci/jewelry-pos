import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { InventoryService, Product } from '../../core/services/inventory.service';
import { ReportService } from '../../core/services/report.service';
import { AddProductDialogComponent } from './add-product-dialog/add-product-dialog.component';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatChipsModule,
    MatFormFieldModule,
    MatInputModule,
    MatPaginatorModule,
    MatTooltipModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    TPipe
  ],
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})
export class InventoryComponent implements OnInit {
  private inventoryService = inject(InventoryService);
  private reportService = inject(ReportService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);

  products: Product[] = [];
  displayedColumns = ['barcode', 'modelName', 'type', 'purityEnum', 'grossWeight', 'estimatedPrice', 'status', 'createdDate', 'actions'];
  loading = true;
  totalElements = 0;
  pageSize = 20;
  currentPage = 0;

  searchQuery = '';
  searching = false;
  
  // Advanced search filters
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
    this.loadProducts();
  }

  loadProducts(page: number = 0): void {
    this.loading = true;

    if (this.searching) {
      let createdFrom: string | null = null;
      let createdTo: string | null = null;

      // Convert Date objects to ISO string format with full day ranges (inclusive)
      if (this.createdFrom) {
        const fromDate = new Date(this.createdFrom);
        createdFrom = `${fromDate.getFullYear()}-${String(fromDate.getMonth() + 1).padStart(2, '0')}-${String(fromDate.getDate()).padStart(2, '0')}T00:00:00`;
      }
      if (this.createdTo) {
        const toDate = new Date(this.createdTo);
        createdTo = `${toDate.getFullYear()}-${String(toDate.getMonth() + 1).padStart(2, '0')}-${String(toDate.getDate()).padStart(2, '0')}T23:59:59`;
      }

      // Use advanced search with filters
      this.inventoryService.searchProductsAdvanced(
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
          // If no results and weight filters are used, try fallback with ±20mg margin
          if (response.content.length === 0 && (this.minWeight !== null || this.maxWeight !== null)) {
            this.loadProductsWithWeightFallback(page, createdFrom, createdTo);
          } else {
            this.products = response.content;
            this.totalElements = response.totalElements;
            this.currentPage = response.number;
            this.loading = false;
          }
        },
        error: () => {
          this.snackBar.open(this.i18n.t('inventory.errorSearching'), this.i18n.t('common.close'), { duration: 3000 });
          this.loading = false;
        }
      });
      return;
    }

    this.inventoryService.getAllProducts(page, this.pageSize).subscribe({
      next: (response) => {
        this.products = response.content;
        this.totalElements = response.totalElements;
        this.currentPage = response.number;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('inventory.errorLoading'), this.i18n.t('common.close'), { duration: 3000 });
        this.loading = false;
      }
    });
  }

  private loadProductsWithWeightFallback(page: number, createdFromParam: string | null, createdToParam: string | null): void {
    const margin = 0.02; // 20 milligrams = 0.02 grams
    let adjustedMinWeight: number | undefined = undefined;
    let adjustedMaxWeight: number | undefined = undefined;

    // Apply margin based on what user entered
    if (this.minWeight !== null && this.maxWeight !== null) {
      // Both min and max: subtract from min, add to max
      adjustedMinWeight = this.minWeight - margin;
      adjustedMaxWeight = this.maxWeight + margin;
    } else if (this.minWeight !== null) {
      // Only min: apply margin to min
      adjustedMinWeight = this.minWeight - margin;
      adjustedMaxWeight = this.minWeight + margin;
    } else if (this.maxWeight !== null) {
      // Only max: apply margin to max
      adjustedMinWeight = this.maxWeight - margin;
      adjustedMaxWeight = this.maxWeight + margin;
    }

    // Ensure date parameters are already converted (they come from loadProducts)
    const createdFrom = createdFromParam;
    const createdTo = createdToParam;

    this.inventoryService.searchProductsAdvanced(
      this.searchQuery.trim() || undefined,
      this.selectedPurity || undefined,
      this.selectedType || undefined,
      adjustedMinWeight,
      adjustedMaxWeight,
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
        this.snackBar.open(this.i18n.t('inventory.errorSearching'), this.i18n.t('common.close'), { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.searching = this.searchQuery.trim().length > 0 || 
                     this.selectedPurity !== null || 
                     this.selectedType !== null || 
                     this.minWeight !== null || 
                     this.maxWeight !== null ||
                     this.createdFrom !== null ||
                     this.createdTo !== null;
    this.loadProducts(0);
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.selectedPurity = null;
    this.selectedType = null;
    this.minWeight = null;
    this.maxWeight = null;
    this.createdFrom = null;
    this.createdTo = null;
    this.searching = false;
    this.loadProducts(0);
  }
  
  getTypeLabel(type: string): string {
    return this.i18n.t(`jewelryType.${type}`);
  }

  getStatusLabel(status: string): string {
    return this.i18n.t(`productStatus.${status}`);
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.loadProducts(event.pageIndex);
  }

  openAddProductDialog(): void {
    const dialogRef = this.dialog.open(AddProductDialogComponent, {
      width: '600px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProducts(this.currentPage);
      }
    });
  }

  openEditDialog(product: Product): void {
    const dialogRef = this.dialog.open(AddProductDialogComponent, {
      width: '600px',
      data: product
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProducts(this.currentPage);
      }
    });
  }

  deleteProduct(product: Product): void {
    if (confirm(this.i18n.t('inventory.confirmDelete', { modelName: product.modelName }))) {
      this.inventoryService.deleteProduct(product.id).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('inventory.deleted'), this.i18n.t('common.close'), { duration: 3000 });
          this.loadProducts(this.currentPage);
        },
        error: () => {
          this.snackBar.open(this.i18n.t('inventory.errorDeleting'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  printLabel(product: Product): void {
    this.reportService.getLabelZpl(product.barcode).subscribe({
      next: (zpl) => {
        // In production, this would send to a ZPL printer
        // For now, show the ZPL content in a dialog or download as text
        const blob = new Blob([zpl], { type: 'text/plain' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `label_${product.barcode}.zpl`;
        link.click();
        window.URL.revokeObjectURL(url);
        this.snackBar.open(this.i18n.t('inventory.labelDownloaded'), this.i18n.t('common.close'), { duration: 3000 });
      },
      error: () => {
        this.snackBar.open(this.i18n.t('inventory.errorLabel'), this.i18n.t('common.close'), { duration: 3000 });
      }
    });
  }

  formatCurrency(value: number): string {
    // Always use English numbers for easy reading
    return value.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  getEstimatedPriceTooltip(): string {
    return this.i18n.t('inventory.tooltip.estPriceFormula');
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString(this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  private toIsoLocalDateTime(value: string | null): string | null {
    if (!value) return null;

    // HTML date input returns: YYYY-MM-DD
    // For date-only filtering, we need to make it inclusive for the full day
    if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
      // This is a date-only value, will be handled in loadProducts
      return value;
    }

    // HTML datetime-local typically returns: YYYY-MM-DDTHH:mm (no seconds)
    // Spring ISO.DATE_TIME parsing is more reliable with seconds included.
    if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(value)) {
      return `${value}:00`;
    }

    return value;
  }
}
