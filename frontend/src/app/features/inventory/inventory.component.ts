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
  displayedColumns = ['barcode', 'modelName', 'type', 'purityEnum', 'grossWeight', 'estimatedPrice', 'status', 'actions'];
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
      // Use advanced search with filters
      this.inventoryService.searchProductsAdvanced(
        this.searchQuery.trim() || undefined,
        this.selectedPurity || undefined,
        this.selectedType || undefined,
        this.minWeight || undefined,
        this.maxWeight || undefined
      ).subscribe({
        next: (products) => {
          this.products = products;
          this.totalElements = products.length;
          this.currentPage = 0;
          this.loading = false;
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

  onSearch(): void {
    this.searching = this.searchQuery.trim().length > 0 || 
                     this.selectedPurity !== null || 
                     this.selectedType !== null || 
                     this.minWeight !== null || 
                     this.maxWeight !== null;
    this.loadProducts(0);
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.selectedPurity = null;
    this.selectedType = null;
    this.minWeight = null;
    this.maxWeight = null;
    this.searching = false;
    this.loadProducts(0);
  }
  
  getTypeLabel(type: string): string {
    return this.i18n.t(`jewelryType.${type}`);
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
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    return value.toLocaleString(locale, { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }
}
