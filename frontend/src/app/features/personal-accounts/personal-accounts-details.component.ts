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

import { PersonalAccountService, PersonalAccount } from '../../core/services/personal-account.service';
import { PersonalPersonService } from '../../core/services/personal-person.service';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

function requireWeightOrMoney(group: FormGroup) {
  const weight = Number(group.get('weight')?.value ?? 0);
  const money = Number(group.get('money')?.value ?? 0);
  return weight > 0 || money > 0 ? null : { weightOrMoneyRequired: true };
}

@Component({
  selector: 'app-personal-accounts-details',
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
  templateUrl: './personal-accounts-details.component.html',
  styleUrls: ['./personal-accounts-details.component.scss']
})
export class PersonalAccountsDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private personalAccountService = inject(PersonalAccountService);
  private personalPersonService = inject(PersonalPersonService);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);

  personId = '';
  personName = '';

  transactions: PersonalAccount[] = [];
  displayedColumns = ['transactionDate', 'statement', 'transactionType', 'weight', 'money', 'actions'];

  loading = false;

  totalElements = 0;
  pageSize = 20;
  currentPage = 0;

  transactionForm: FormGroup = this.fb.group(
    {
      transactionDate: [new Date(), Validators.required],
      statement: ['', Validators.required],
      transactionType: ['RECEIVABLE', Validators.required],
      weight: [0, [Validators.min(0)]],
      money: [0, [Validators.min(0)]]
    },
    { validators: requireWeightOrMoney }
  );

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('personId');
    if (!id) {
      this.router.navigate(['/personal-accounts']);
      return;
    }

    this.personId = id;

    const navState = this.router.getCurrentNavigation()?.extras?.state as any;
    const historyState = history.state as any;
    this.personName = navState?.personName || historyState?.personName || '';

    if (!this.personName) {
      this.personalPersonService.getPersonById(this.personId).subscribe({
        next: (p) => (this.personName = p.name),
        error: () => (this.personName = this.personId)
      });
    }

    this.loadTransactions();
  }

  back(): void {
    this.router.navigate(['/personal-accounts']);
  }

  loadTransactions(page: number = 0): void {
    this.loading = true;
    this.personalAccountService.getTransactionsByPerson(this.personId, page, this.pageSize).subscribe({
      next: (resp) => {
        this.transactions = resp.content;
        this.totalElements = resp.totalElements;
        this.currentPage = resp.number;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snackBar.open(this.i18n.t('personalAccounts.noData'), this.i18n.t('common.close'), { duration: 3000 });
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
      personId: this.personId,
      transactionDate: new Date(v.transactionDate).toISOString(),
      statement: v.statement,
      transactionType: v.transactionType,
      weight: v.weight ?? 0,
      money: v.money ?? 0
    };

    this.personalAccountService.createTransaction(request).subscribe({
      next: () => {
        this.snackBar.open(this.i18n.t('personalAccounts.createSuccess'), this.i18n.t('common.close'), { duration: 3000 });
        this.transactionForm.reset({
          transactionDate: new Date(),
          transactionType: 'RECEIVABLE',
          weight: 0,
          money: 0,
          statement: ''
        });
        this.loadTransactions(this.currentPage);
      },
      error: () => {
        this.snackBar.open(this.i18n.t('personalAccounts.createError'), this.i18n.t('common.close'), { duration: 3000 });
      }
    });
  }

  deleteTransaction(id: string): void {
    const ok = confirm(this.i18n.t('common.delete'));
    if (!ok) return;

    this.personalAccountService.deleteTransaction(id).subscribe({
      next: () => {
        this.snackBar.open(this.i18n.t('personalAccounts.deleteSuccess'), this.i18n.t('common.close'), { duration: 3000 });
        this.loadTransactions(this.currentPage);
      },
      error: () => {
        this.snackBar.open(this.i18n.t('personalAccounts.deleteError'), this.i18n.t('common.close'), { duration: 3000 });
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
