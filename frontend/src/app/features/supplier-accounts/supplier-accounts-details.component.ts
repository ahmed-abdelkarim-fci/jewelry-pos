import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';

import { SupplierAccountService, SupplierAccount } from '../../core/services/supplier-account.service';
import { SupplierService } from '../../core/services/supplier.service';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

function requireWeightOrFees(group: FormGroup) {
  const weight = Number(group.get('weight')?.value ?? 0);
  const fees = Number(group.get('fees')?.value ?? 0);
  return weight > 0 || fees > 0 ? null : { weightOrFeesRequired: true };
}

@Component({
  selector: 'app-supplier-accounts-details',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
    MatTableModule,
    MatPaginatorModule,
    TPipe
  ],
  templateUrl: './supplier-accounts-details.component.html',
  styleUrls: ['./supplier-accounts-details.component.scss']
})
export class SupplierAccountsDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private supplierAccountService = inject(SupplierAccountService);
  private supplierService = inject(SupplierService);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);

  supplierId = '';
  supplierName = '';

  transactions: SupplierAccount[] = [];
  displayedColumns = ['transactionDate', 'statement', 'transactionType', 'weight', 'fees', 'numberOfPieces', 'actions'];

  totalElements = 0;
  pageSize = 20;
  currentPage = 0;
  loading = false;

  showAddForm = true;

  transactionForm: FormGroup = this.fb.group(
    {
      transactionDate: [new Date(), Validators.required],
      statement: ['', Validators.required],
      transactionType: ['RECEIVABLE', Validators.required],
      weight: [0, [Validators.min(0)]],
      fees: [0, [Validators.min(0)]],
      numberOfPieces: [null, [Validators.required, Validators.min(1)]]
    },
    { validators: requireWeightOrFees }
  );

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('supplierId');
    if (!id) {
      this.router.navigate(['/supplier-accounts']);
      return;
    }

    this.supplierId = id;

    const navState = this.router.getCurrentNavigation()?.extras?.state as any;
    const historyState = history.state as any;
    this.supplierName = navState?.supplierName || historyState?.supplierName || '';

    if (!this.supplierName) {
      this.supplierService.getSupplierById(this.supplierId).subscribe({
        next: (s) => (this.supplierName = s.name),
        error: () => (this.supplierName = this.supplierId)
      });
    }

    this.loadTransactions();
  }

  back(): void {
    this.router.navigate(['/supplier-accounts']);
  }

  loadTransactions(page: number = 0): void {
    this.loading = true;
    this.supplierAccountService.getTransactionsBySupplier(this.supplierId, page, this.pageSize).subscribe({
      next: (resp) => {
        this.transactions = resp.content;
        this.totalElements = resp.totalElements;
        this.currentPage = resp.number;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snackBar.open(this.i18n.t('supplierAccounts.noData'), this.i18n.t('common.close'), { duration: 3000 });
      }
    });
  }

  onPageChange(e: PageEvent): void {
    this.pageSize = e.pageSize;
    this.loadTransactions(e.pageIndex);
  }

  onSubmit(): void {
    if (!this.transactionForm.valid) return;

    const v = this.transactionForm.value;
    const request = {
      supplierId: this.supplierId,
      transactionDate: new Date(v.transactionDate).toISOString(),
      statement: v.statement,
      transactionType: v.transactionType,
      weight: v.weight ?? 0,
      fees: v.fees ?? 0,
      numberOfPieces: v.numberOfPieces
    };

    this.supplierAccountService.createTransaction(request).subscribe({
      next: () => {
        this.snackBar.open(this.i18n.t('supplierAccounts.createSuccess'), this.i18n.t('common.close'), { duration: 3000 });
        this.transactionForm.reset({
          transactionDate: new Date(),
          transactionType: 'RECEIVABLE',
          weight: 0,
          fees: 0,
          numberOfPieces: null,
          statement: ''
        });
        this.loadTransactions(this.currentPage);
      },
      error: () => {
        this.snackBar.open(this.i18n.t('supplierAccounts.createError'), this.i18n.t('common.close'), { duration: 3000 });
      }
    });
  }

  deleteTransaction(id: string): void {
    const ok = confirm(this.i18n.t('common.delete'));
    if (!ok) return;

    this.supplierAccountService.deleteTransaction(id).subscribe({
      next: () => {
        this.snackBar.open(this.i18n.t('supplierAccounts.deleteSuccess'), this.i18n.t('common.close'), { duration: 3000 });
        this.loadTransactions(this.currentPage);
      },
      error: () => {
        this.snackBar.open(this.i18n.t('supplierAccounts.deleteError'), this.i18n.t('common.close'), { duration: 3000 });
      }
    });
  }

  formatDateTime(value: string): string {
    if (!value) return '';
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    return new Date(value).toLocaleString(locale);
  }

  formatCurrency(value: number): string {
    return (value ?? 0).toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
  }

  formatWeight(value: number): string {
    return (value ?? 0).toLocaleString('en-US', { minimumFractionDigits: 3, maximumFractionDigits: 3 });
  }
}
