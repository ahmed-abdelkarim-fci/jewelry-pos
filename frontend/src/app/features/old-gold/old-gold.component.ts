import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { OldGoldService, ScrapInventory, OldGoldPurchase, ScrapPurification } from '../../core/services/old-gold.service';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

@Component({
  selector: 'app-old-gold',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatTableModule,
    MatPaginatorModule,
    TPipe
  ],
  templateUrl: './old-gold.component.html',
  styleUrls: ['./old-gold.component.scss']
})
export class OldGoldComponent implements OnInit {
  private fb = inject(FormBuilder);
  private oldGoldService = inject(OldGoldService);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);

  purities = ['KARAT_24', 'KARAT_21', 'KARAT_18'];
  scrapInventory: ScrapInventory[] = [];
  
  // Purchase history
  purchases: OldGoldPurchase[] = [];
  displayedColumns = ['transactionDate', 'purity', 'weight', 'buyRate', 'totalValue', 'customerNationalId'];
  totalElements = 0;
  pageSize = 20;
  currentPage = 0;
  loadingPurchases = false;

  // Purification history
  purifications: ScrapPurification[] = [];
  purificationColumns = ['transactionDate', 'purity', 'weightOut', 'cashReceived', 'factoryName'];
  purificationTotalElements = 0;
  purificationPageSize = 20;
  purificationCurrentPage = 0;
  loadingPurifications = false;

  buyCashForm: FormGroup = this.fb.group({
    purity: ['', Validators.required],
    weight: ['', [Validators.required, Validators.min(0.1)]],
    buyRate: ['', [Validators.required, Validators.min(1)]],
    customerNationalId: ['', [Validators.required, Validators.pattern(/^\d{14}$/)]],
    customerPhoneNumber: [''],
    description: ['']
  });

  purificationForm: FormGroup = this.fb.group({
    purity: ['', Validators.required],
    weightToSell: ['', [Validators.required, Validators.min(0.1)]],
    cashReceived: ['', [Validators.required, Validators.min(1)]],
    factoryName: ['', Validators.required],
    description: ['']
  });

  ngOnInit(): void {
    this.loadScrapInventory();
    this.loadPurchases();
    this.loadPurifications();
  }

  loadScrapInventory(): void {
    this.oldGoldService.getScrapInventory().subscribe({
      next: (inventory) => {
        this.scrapInventory = inventory;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('oldGold.loadScrapError'), this.i18n.t('common.close'), { duration: 3000 });
      }
    });
  }

  getAvailableWeight(karat: string): number {
    const item = this.scrapInventory.find(s => s.purity === karat);
    return item?.totalWeight || 0;
  }
  
  loadPurchases(page: number = 0): void {
    this.loadingPurchases = true;
    this.oldGoldService.getAllPurchases(page, this.pageSize).subscribe({
      next: (response) => {
        this.purchases = response.content;
        this.totalElements = response.totalElements;
        this.currentPage = response.number;
        this.loadingPurchases = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('oldGold.loadPurchasesError'), this.i18n.t('common.close'), { duration: 3000 });
        this.loadingPurchases = false;
      }
    });
  }
  
  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.loadPurchases(event.pageIndex);
  }

  loadPurifications(page: number = 0): void {
    this.loadingPurifications = true;
    this.oldGoldService.getAllPurifications(page, this.purificationPageSize).subscribe({
      next: (response) => {
        this.purifications = response.content;
        this.purificationTotalElements = response.totalElements;
        this.purificationCurrentPage = response.number;
        this.loadingPurifications = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('oldGold.loadPurificationsError'), this.i18n.t('common.close'), { duration: 3000 });
        this.loadingPurifications = false;
      }
    });
  }

  onPurificationPageChange(event: PageEvent): void {
    this.purificationPageSize = event.pageSize;
    this.loadPurifications(event.pageIndex);
  }

  formatDateTime(value: string): string {
    if (!value) return '';
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    const date = new Date(value);
    return date.toLocaleString(locale);
  }

  onBuyCash(): void {
    if (this.buyCashForm.valid) {
      this.oldGoldService.buyCash(this.buyCashForm.value).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('oldGold.buySuccess'), this.i18n.t('common.close'), { duration: 3000 });
          this.buyCashForm.reset();
          this.loadScrapInventory();
          this.loadPurchases(this.currentPage);
        },
        error: () => {
          this.snackBar.open(this.i18n.t('oldGold.buyError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  onPurify(): void {
    if (this.purificationForm.valid) {
      const weightToSell = this.purificationForm.get('weightToSell')?.value;
      const purity = this.purificationForm.get('purity')?.value;
      const available = this.getAvailableWeight(purity);

      if (weightToSell > available) {
        this.snackBar.open(this.i18n.t('oldGold.scrapError', { available }), this.i18n.t('common.close'), { duration: 3000 });
        return;
      }

      this.oldGoldService.purify(this.purificationForm.value).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('oldGold.purifySuccess'), this.i18n.t('common.close'), { duration: 3000 });
          this.purificationForm.reset();
          this.loadScrapInventory();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('oldGold.purifyError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  getBuyCashTotal(): number {
    const weight = this.buyCashForm.get('weight')?.value || 0;
    const buyRate = this.buyCashForm.get('buyRate')?.value || 0;
    return weight * buyRate;
  }

  getPurificationTotal(): number {
    const cashReceived = this.purificationForm.get('cashReceived')?.value || 0;
    return cashReceived;
  }

  formatCurrency(value: number): string {
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    return value.toLocaleString(locale, { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }
}
