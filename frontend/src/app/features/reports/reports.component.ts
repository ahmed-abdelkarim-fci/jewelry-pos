import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { ReportService, Transaction, ZReport } from '../../core/services/report.service';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatInputModule,
    TPipe
  ],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {
  private reportService = inject(ReportService);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);

  transactions: Transaction[] = [];
  displayedColumns = ['id', 'date', 'type', 'amount', 'description'];
  loading = true;

  zReport?: ZReport;
  zReportDate: Date | null = null;
  loadingZReport = false;

  ngOnInit(): void {
    this.loadTransactions();
    this.loadZReport();
  }

  loadZReport(): void {
    this.loadingZReport = true;
    const dateStr = this.zReportDate ? this.formatLocalDate(this.zReportDate) : undefined;
    this.reportService.getZReport(dateStr).subscribe({
      next: (report) => {
        this.zReport = report;
        this.loadingZReport = false;
      },
      error: () => {
        this.loadingZReport = false;
        this.snackBar.open(this.i18n.t('reports.z.error'), this.i18n.t('common.close'), { duration: 3000 });
      }
    });
  }

  loadTransactions(): void {
    this.reportService.getRecentTransactions().subscribe({
      next: (transactions) => {
        this.transactions = transactions;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('reports.tx.error'), this.i18n.t('common.close'), { duration: 3000 });
        this.loading = false;
      }
    });
  }

  refreshZReport(): void {
    this.loadZReport();
  }

  formatCurrency(value: number): string {
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    return value.toLocaleString(locale, { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  formatDate(date: string): string {
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    return new Date(date).toLocaleString(locale, {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  private formatLocalDate(date: Date): string {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  }
}
