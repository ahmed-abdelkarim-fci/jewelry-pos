import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Sale } from '../../../core/services/sales.service';
import { ReportService } from '../../../core/services/report.service';
import { I18nService } from '../../../core/services/i18n.service';
import { TPipe } from '../../../shared/pipes/t.pipe';

@Component({
  selector: 'app-sale-details-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatSnackBarModule,
    TPipe
  ],
  templateUrl: './sale-details-dialog.component.html',
  styleUrls: ['./sale-details-dialog.component.scss']
})
export class SaleDetailsDialogComponent {
  private reportService = inject(ReportService);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);
  downloading = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public sale: Sale,
    private dialogRef: MatDialogRef<SaleDetailsDialogComponent>
  ) {}

  formatCurrency(value: number): string {
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    return value.toLocaleString(locale, { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  formatDateTime(dateStr: string): string {
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    return new Date(dateStr).toLocaleString(locale, {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  downloadReceipt(): void {
    this.downloading = true;
    this.reportService.downloadReceipt(this.sale.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `receipt_${this.sale.id}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
        this.downloading = false;
        this.snackBar.open(this.i18n.t('saleDetails.receiptOk'), this.i18n.t('common.close'), { duration: 3000 });
      },
      error: () => {
        this.downloading = false;
        this.snackBar.open(this.i18n.t('saleDetails.receiptError'), this.i18n.t('common.close'), { duration: 3000 });
      }
    });
  }

  close(): void {
    this.dialogRef.close();
  }
}
