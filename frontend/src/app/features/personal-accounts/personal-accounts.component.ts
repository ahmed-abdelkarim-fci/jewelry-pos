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
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { PersonalAccountService, PersonalAccountSummary } from '../../core/services/personal-account.service';
import { PersonalPersonService, PersonalPerson } from '../../core/services/personal-person.service';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

function requireWeightOrMoney(group: FormGroup) {
  const weight = Number(group.get('weight')?.value ?? 0);
  const money = Number(group.get('money')?.value ?? 0);
  return weight > 0 || money > 0 ? null : { weightOrMoneyRequired: true };
}

@Component({
  selector: 'app-personal-accounts',
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
    MatDialogModule,
    TPipe
  ],
  templateUrl: './personal-accounts.component.html',
  styleUrls: ['./personal-accounts.component.scss']
})
export class PersonalAccountsComponent implements OnInit {
  private fb = inject(FormBuilder);
  private personalAccountService = inject(PersonalAccountService);
  private personalPersonService = inject(PersonalPersonService);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);
  private router = inject(Router);
  private dialog = inject(MatDialog);

  summaries: PersonalAccountSummary[] = [];
  persons: PersonalPerson[] = [];
  displayedColumns = ['personName', 'netMoney', 'netWeight', 'moneyStatus', 'weightStatus', 'transactionCount', 'actions'];
  loading = false;
  showAddForm = false;

  transactionForm: FormGroup = this.fb.group(
    {
      personId: ['', Validators.required],
      transactionDate: [new Date(), Validators.required],
      statement: ['', Validators.required],
      transactionType: ['RECEIVABLE', Validators.required],
      weight: [0, [Validators.min(0)]],
      money: [0, [Validators.min(0)]]
    },
    { validators: requireWeightOrMoney }
  );

  ngOnInit(): void {
    this.loadPersons();
    this.loadSummaries();
  }

  loadPersons(): void {
    this.personalPersonService.getAllPersons().subscribe({
      next: (data) => {
        this.persons = data;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('personalPersons.noData'), this.i18n.t('common.close'), { duration: 3000 });
      }
    });
  }

  loadSummaries(): void {
    this.loading = true;
    this.personalAccountService.getPersonSummaries().subscribe({
      next: (data) => {
        this.summaries = data;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('personalAccounts.noData'), this.i18n.t('common.close'), { duration: 3000 });
        this.loading = false;
      }
    });
  }

  get totalNetMoney(): number {
    return (this.summaries ?? []).reduce((sum, s) => sum + (Number(s.netMoney) || 0), 0);
  }

  get totalNetWeight(): number {
    return (this.summaries ?? []).reduce((sum, s) => sum + (Number(s.netWeight) || 0), 0);
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.transactionForm.reset({
        personId: '',
        transactionDate: new Date(),
        statement: '',
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

      this.personalAccountService.createTransaction(request).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('personalAccounts.createSuccess'), this.i18n.t('common.close'), { duration: 3000 });
          this.toggleAddForm();
          this.loadSummaries();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('personalAccounts.createError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  viewDetails(personId: string, personName: string): void {
    this.router.navigate(['/personal-accounts', personId], { state: { personName } });
  }

  formatCurrency(value: number): string {
    return (value ?? 0).toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
  }

  formatWeight(value: number): string {
    return (value ?? 0).toLocaleString('en-US', { minimumFractionDigits: 3, maximumFractionDigits: 3 });
  }
}
