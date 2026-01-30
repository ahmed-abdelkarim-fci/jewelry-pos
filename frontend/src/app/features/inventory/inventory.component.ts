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
import { InventoryService, Product } from '../../core/services/inventory.service';
import { ReportService } from '../../core/services/report.service';
import { AddProductDialogComponent } from './add-product-dialog/add-product-dialog.component';

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
    MatTooltipModule
  ],
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})
export class InventoryComponent implements OnInit {
  private inventoryService = inject(InventoryService);
  private reportService = inject(ReportService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  products: Product[] = [];
  displayedColumns = ['barcode', 'modelName', 'type', 'purityEnum', 'grossWeight', 'estimatedPrice', 'status', 'actions'];
  loading = true;
  totalElements = 0;
  pageSize = 20;
  currentPage = 0;

  searchQuery = '';
  searching = false;

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(page: number = 0): void {
    this.loading = true;

    if (this.searching && this.searchQuery.trim()) {
      // Backend search endpoint is non-paginated
      this.inventoryService.searchProducts(this.searchQuery.trim()).subscribe({
        next: (products) => {
          this.products = products;
          this.totalElements = products.length;
          this.currentPage = 0;
          this.loading = false;
        },
        error: () => {
          this.snackBar.open('Error searching products', 'Close', { duration: 3000 });
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
        this.snackBar.open('Error loading products', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.searching = this.searchQuery.trim().length > 0;
    this.loadProducts(0);
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.searching = false;
    this.loadProducts(0);
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
        this.loadProducts();
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
        this.loadProducts();
      }
    });
  }

  deleteProduct(product: Product): void {
    if (confirm(`Delete product ${product.modelName}?`)) {
      this.inventoryService.deleteProduct(product.id).subscribe({
        next: () => {
          this.snackBar.open('Product deleted', 'Close', { duration: 3000 });
          this.loadProducts();
        },
        error: () => {
          this.snackBar.open('Error deleting product', 'Close', { duration: 3000 });
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
        this.snackBar.open('Label ZPL downloaded', 'Close', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Error generating label', 'Close', { duration: 3000 });
      }
    });
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }
}
