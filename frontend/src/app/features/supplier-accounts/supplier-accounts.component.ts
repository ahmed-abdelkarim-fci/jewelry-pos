import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { SupplierAccountService, SupplierAccountSummary } from '../../core/services/supplier-account.service';
import { SupplierService, Supplier } from '../../core/services/supplier.service';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

function requireWeightOrFees(group: FormGroup) {
  const weight = Number(group.get('weight')?.value ?? 0);
  const fees = Number(group.get('fees')?.value ?? 0);
  return weight > 0 || fees > 0 ? null : { weightOrFeesRequired: true };
}

@Component({
  selector: 'app-supplier-accounts',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
    TPipe
  ],
  templateUrl: './supplier-accounts.component.html',
  styleUrls: ['./supplier-accounts.component.scss']
})
export class SupplierAccountsComponent implements OnInit {
  private fb = inject(FormBuilder);
  private supplierAccountService = inject(SupplierAccountService);
  private supplierService = inject(SupplierService);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);
  private router = inject(Router);

  summaries: SupplierAccountSummary[] = [];
  suppliers: Supplier[] = [];
  displayedColumns = ['supplierName', 'netFees', 'netWeight', 'feesStatus', 'weightStatus', 'transactionCount', 'actions'];
  loading = false;
  showAddForm = false;

  transactionForm: FormGroup = this.fb.group(
    {
      supplierId: ['', Validators.required],
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
    this.loadSuppliers();
    this.loadSummaries();
  }

  loadSuppliers(): void {
    this.supplierService.getAllSuppliers().subscribe({
      next: (data) => {
        this.suppliers = data;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('suppliers.noData'), this.i18n.t('common.close'), { duration: 3000 });
      }
    });
  }

  loadSummaries(): void {
    this.loading = true;
    this.supplierAccountService.getSupplierSummaries().subscribe({
      next: (data) => {
        this.summaries = data;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('supplierAccounts.noData'), this.i18n.t('common.close'), { duration: 3000 });
        this.loading = false;
      }
    });
  }

  get totalNetFees(): number {
    return (this.summaries ?? []).reduce((sum, s) => sum + (Number(s.netFees) || 0), 0);
  }

  get totalNetWeight(): number {
    return (this.summaries ?? []).reduce((sum, s) => sum + (Number(s.netWeight) || 0), 0);
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.transactionForm.reset({
        transactionDate: new Date(),
        transactionType: 'RECEIVABLE',
        weight: 0,
        fees: 0
      });
    }
  }

  onSubmit(): void {
    if (this.transactionForm.valid) {
      const formValue = this.transactionForm.value;
      const request = {
        ...formValue,
        transactionDate: new Date(formValue.transactionDate).toISOString()
      };

      this.supplierAccountService.createTransaction(request).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('supplierAccounts.createSuccess'), this.i18n.t('common.close'), { duration: 3000 });
          this.toggleAddForm();
          this.loadSummaries();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('supplierAccounts.createError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  viewDetails(supplierId: string, supplierName: string): void {
    this.router.navigate(['/supplier-accounts', supplierId], { state: { supplierName } });
  }

  formatCurrency(value: number): string {
    return (value ?? 0).toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
  }

  formatWeight(value: number): string {
    return (value ?? 0).toLocaleString('en-US', { minimumFractionDigits: 3, maximumFractionDigits: 3 });
  }
}
