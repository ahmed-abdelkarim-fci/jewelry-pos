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
    MatInputModule
  ],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {
  private reportService = inject(ReportService);
  private snackBar = inject(MatSnackBar);

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
    const dateStr = this.zReportDate ? this.zReportDate.toISOString().split('T')[0] : undefined;
    this.reportService.getZReport(dateStr).subscribe({
      next: (report) => {
        this.zReport = report;
        this.loadingZReport = false;
      },
      error: () => {
        this.loadingZReport = false;
        this.snackBar.open('Error loading Z-Report', 'Close', { duration: 3000 });
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
        this.snackBar.open('Error loading transactions', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  refreshZReport(): void {
    this.loadZReport();
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleString('en-US', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
