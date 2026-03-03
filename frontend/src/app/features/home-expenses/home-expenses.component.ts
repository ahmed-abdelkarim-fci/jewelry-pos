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
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { HomeExpenseService, HomeExpense, HomeExpenseSummary } from '../../core/services/home-expense.service';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

@Component({
  selector: 'app-home-expenses',
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
    MatPaginatorModule,
    TPipe
  ],
  templateUrl: './home-expenses.component.html',
  styleUrls: ['./home-expenses.component.scss']
})
export class HomeExpensesComponent implements OnInit {
  private fb = inject(FormBuilder);
  private homeExpenseService = inject(HomeExpenseService);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);

  expenses: HomeExpense[] = [];
  summary: HomeExpenseSummary | null = null;
  displayedColumns = ['transactionDate', 'description', 'transactionType', 'weight', 'money', 'actions'];
  totalElements = 0;
  pageSize = 20;
  currentPage = 0;
  loading = false;
  showAddForm = false;

  transactionForm: FormGroup = this.fb.group({
    transactionDate: [new Date(), Validators.required],
    description: [''],
    transactionType: ['RECEIVABLE', Validators.required],
    weight: [0, [Validators.min(0)]],
    money: [0, [Validators.min(0)]]
  });

  ngOnInit(): void {
    this.loadExpenses();
    this.loadSummary();
  }

  loadExpenses(page: number = 0): void {
    this.loading = true;
    this.homeExpenseService.getAllTransactions(page, this.pageSize).subscribe({
      next: (response) => {
        this.expenses = response.content;
        this.totalElements = response.totalElements;
        this.currentPage = response.number;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('homeExpenses.noData'), this.i18n.t('common.close'), { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadSummary(): void {
    this.homeExpenseService.getSummary().subscribe({
      next: (data) => {
        this.summary = data;
      },
      error: () => {
        console.error('Error loading summary');
      }
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.transactionForm.reset({
        transactionDate: new Date(),
        transactionType: 'RECEIVABLE',
        weight: 0,
        money: 0
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

      this.homeExpenseService.createTransaction(request).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('homeExpenses.createSuccess'), this.i18n.t('common.close'), { duration: 3000 });
          this.toggleAddForm();
          this.loadExpenses(this.currentPage);
          this.loadSummary();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('homeExpenses.createError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.loadExpenses(event.pageIndex);
  }

  deleteTransaction(id: string): void {
    if (confirm(this.i18n.t('common.delete'))) {
      this.homeExpenseService.deleteTransaction(id).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('homeExpenses.deleteSuccess'), this.i18n.t('common.close'), { duration: 3000 });
          this.loadExpenses(this.currentPage);
          this.loadSummary();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('homeExpenses.deleteError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  formatDateTime(value: string): string {
    if (!value) return '';
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    const date = new Date(value);
    return date.toLocaleString(locale);
  }

  formatCurrency(value: number): string {
    return (value ?? 0).toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
  }

  formatWeight(value: number): string {
    return (value ?? 0).toLocaleString('en-US', { minimumFractionDigits: 3, maximumFractionDigits: 3 });
  }
}
