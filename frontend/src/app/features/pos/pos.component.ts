import { Component, inject, ViewChild, ElementRef, AfterViewInit, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { PosService, Product, OldGoldItem } from '../../core/services/pos.service';
import { GoldRateService, GoldRate } from '../../core/services/gold-rate.service';
import { AddOldGoldDialogComponent } from './add-old-gold-dialog/add-old-gold-dialog.component';

interface CartItem extends Product {
  quantity: number;
}

@Component({
  selector: 'app-pos',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatDialogModule,
    MatSnackBarModule
  ],
  templateUrl: './pos.component.html',
  styleUrls: ['./pos.component.scss']
})
export class PosComponent implements OnInit, AfterViewInit {
  @ViewChild('barcodeInput') barcodeInput!: ElementRef<HTMLInputElement>;
  
  private posService = inject(PosService);
  private goldRateService = inject(GoldRateService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  barcode = '';
  cartItems: CartItem[] = [];
  oldGoldItems: OldGoldItem[] = [];
  lastScannedProduct?: Product;
  displayedColumns = ['model', 'weight', 'price', 'actions'];
  
  // Customer information
  customerName = '';
  customerPhone = '';
  
  // Gold rate (should be fetched from gold rate service)
  currentGoldRate = 0;

  ngOnInit(): void {
    this.loadGoldRate();
  }

  private loadGoldRate(): void {
    this.goldRateService.getCurrentRates().subscribe({
      next: (rates: GoldRate[]) => {
        const rate21 = rates?.find(r => r.karat === 'KARAT_21');
        // Use SELL rate for pricing (backend currently returns same buy/sell)
        this.currentGoldRate = rate21?.sellRate ?? rate21?.buyRate ?? 0;
      },
      error: () => {
        // Leave as 0; checkout will prevent sale until rates are configured
        this.currentGoldRate = 0;
      }
    });
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.barcodeInput?.nativeElement.focus(), 100);
  }

  onBarcodeSubmit(): void {
    if (!this.barcode.trim()) return;

    this.posService.getProductByBarcode(this.barcode).subscribe({
      next: (product) => {
        this.addToCart(product);
        this.lastScannedProduct = product;
        this.barcode = '';
        this.barcodeInput.nativeElement.focus();
      },
      error: () => {
        this.snackBar.open('Product not found', 'Close', { duration: 3000 });
        this.barcode = '';
      }
    });
  }

  addToCart(product: Product): void {
    const existingItem = this.cartItems.find(item => item.id === product.id);
    if (existingItem) {
      existingItem.quantity++;
    } else {
      this.cartItems.push({ ...product, quantity: 1 });
    }
  }

  removeFromCart(item: CartItem): void {
    const index = this.cartItems.indexOf(item);
    if (index > -1) {
      this.cartItems.splice(index, 1);
    }
  }

  openAddOldGoldDialog(): void {
    const dialogRef = this.dialog.open(AddOldGoldDialogComponent, {
      width: '500px'
    });

    dialogRef.afterClosed().subscribe((result: OldGoldItem) => {
      if (result) {
        this.oldGoldItems.push(result);
      }
    });
  }

  removeOldGold(item: OldGoldItem): void {
    const index = this.oldGoldItems.indexOf(item);
    if (index > -1) {
      this.oldGoldItems.splice(index, 1);
    }
  }

  getSubtotal(): number {
    return this.cartItems.reduce((sum, item) => sum + (item.estimatedPrice * item.quantity), 0);
  }

  getOldGoldTotal(): number {
    return this.oldGoldItems.reduce((sum, item) => sum + (item.weight * item.buyRate), 0);
  }

  getNetTotal(): number {
    return this.getSubtotal() - this.getOldGoldTotal();
  }

  checkout(): void {
    if (this.cartItems.length === 0) {
      this.snackBar.open('Cart is empty', 'Close', { duration: 3000 });
      return;
    }

    // Validate customer information
    if (!this.customerName) {
      this.snackBar.open('Please enter customer name', 'Close', { duration: 3000 });
      return;
    }

    if (!this.currentGoldRate || this.currentGoldRate <= 0) {
      this.snackBar.open('Gold rate is not set. Please configure gold rates in Settings.', 'Close', { duration: 4000 });
      return;
    }

    // Prepare correct sale request format
    const saleRequest = {
      barcodes: this.cartItems.map(item => item.barcode),
      currentGoldRate: this.currentGoldRate,
      customerName: this.customerName,
      customerPhone: this.customerPhone || undefined,
      tradeInItems: this.oldGoldItems.length > 0 ? this.oldGoldItems : undefined
    };

    this.posService.createSale(saleRequest).subscribe({
      next: () => {
        this.snackBar.open('Sale completed successfully!', 'Close', { duration: 3000 });
        this.clearCart();
      },
      error: (error) => {
        console.error('Sale error:', error);
        this.snackBar.open('Error processing sale', 'Close', { duration: 3000 });
      }
    });
  }

  clearCart(): void {
    this.cartItems = [];
    this.oldGoldItems = [];
    this.lastScannedProduct = undefined;
    this.customerName = '';
    this.customerPhone = '';
    this.barcodeInput.nativeElement.focus();
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }
}
