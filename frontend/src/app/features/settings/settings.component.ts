import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { GoldRateService, GoldRate, PageResponse } from '../../core/services/gold-rate.service';
import { ConfigService } from '../../core/services/config.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatSlideToggleModule,
    MatTableModule,
    MatPaginatorModule
  ],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  private fb = inject(FormBuilder);
  private goldRateService = inject(GoldRateService);
  private configService = inject(ConfigService);
  private snackBar = inject(MatSnackBar);

  goldRateForm!: FormGroup;
  saving = false;
  lastUpdated?: Date;

  goldAutoUpdateEnabled = false;
  hardwareEnabled = false;
  loadingConfig = true;

  // Gold rate history
  rateHistory: any[] = [];
  historyColumns = ['effectiveDate', 'rate24k', 'rate21k', 'rate18k'];
  loadingHistory = false;
  totalHistoryElements = 0;
  historyPageSize = 10;
  currentHistoryPage = 0;
  showHistory = false;

  ngOnInit(): void {
    this.initForm();
    this.loadCurrentRates();
    this.loadConfig();
  }

  toggleHistory(): void {
    this.showHistory = !this.showHistory;
    if (this.showHistory && this.rateHistory.length === 0) {
      this.loadRateHistory();
    }
  }

  loadRateHistory(): void {
    this.loadingHistory = true;
    this.goldRateService.getHistory(this.currentHistoryPage, this.historyPageSize).subscribe({
      next: (response: PageResponse<any>) => {
        this.rateHistory = response.content;
        this.totalHistoryElements = response.totalElements;
        this.loadingHistory = false;
      },
      error: () => {
        this.loadingHistory = false;
        this.snackBar.open('Error loading rate history', 'Close', { duration: 3000 });
      }
    });
  }

  onHistoryPageChange(event: PageEvent): void {
    this.currentHistoryPage = event.pageIndex;
    this.historyPageSize = event.pageSize;
    this.loadRateHistory();
  }

  loadConfig(): void {
    this.loadingConfig = true;

    this.configService.getGoldAutoUpdateStatus().subscribe({
      next: (res) => {
        this.goldAutoUpdateEnabled = !!res.enabled;
      },
      error: () => {
        this.snackBar.open('Failed to load Gold Auto-Update status', 'Close', { duration: 3000 });
      }
    });

    this.configService.getHardwareStatus().subscribe({
      next: (res) => {
        this.hardwareEnabled = !!res.enabled;
        this.loadingConfig = false;
      },
      error: () => {
        this.loadingConfig = false;
        this.snackBar.open('Failed to load Hardware status', 'Close', { duration: 3000 });
      }
    });
  }

  onToggleGoldAutoUpdate(enabled: boolean): void {
    const prev = this.goldAutoUpdateEnabled;
    this.goldAutoUpdateEnabled = enabled;
    this.configService.setGoldAutoUpdateStatus(enabled).subscribe({
      next: () => {
        this.snackBar.open('Gold Auto-Update setting updated', 'Close', { duration: 3000 });
      },
      error: () => {
        this.goldAutoUpdateEnabled = prev;
        this.snackBar.open('Failed to update Gold Auto-Update setting', 'Close', { duration: 3000 });
      }
    });
  }

  onToggleHardware(enabled: boolean): void {
    const prev = this.hardwareEnabled;
    this.hardwareEnabled = enabled;
    this.configService.setHardwareStatus(enabled).subscribe({
      next: () => {
        this.snackBar.open('Hardware setting updated', 'Close', { duration: 3000 });
      },
      error: () => {
        this.hardwareEnabled = prev;
        this.snackBar.open('Failed to update Hardware setting', 'Close', { duration: 3000 });
      }
    });
  }

  initForm(): void {
    this.goldRateForm = this.fb.group({
      karat24Buy: [0, [Validators.required, Validators.min(0)]],
      karat24Sell: [0, [Validators.required, Validators.min(0)]],
      karat21Buy: [0, [Validators.required, Validators.min(0)]],
      karat21Sell: [0, [Validators.required, Validators.min(0)]],
      karat18Buy: [0, [Validators.required, Validators.min(0)]],
      karat18Sell: [0, [Validators.required, Validators.min(0)]]
    });
  }

  loadCurrentRates(): void {
    this.goldRateService.getCurrentRates().subscribe({
      next: (rates: GoldRate[]) => {
        // Assuming rates is an array of GoldRate objects
        if (Array.isArray(rates)) {
          rates.forEach(rate => {
            if (rate.karat === 'KARAT_24') {
              this.goldRateForm.patchValue({
                karat24Buy: rate.buyRate,
                karat24Sell: rate.sellRate
              });
            } else if (rate.karat === 'KARAT_21') {
              this.goldRateForm.patchValue({
                karat21Buy: rate.buyRate,
                karat21Sell: rate.sellRate
              });
            } else if (rate.karat === 'KARAT_18') {
              this.goldRateForm.patchValue({
                karat18Buy: rate.buyRate,
                karat18Sell: rate.sellRate
              });
            }
          });
          this.lastUpdated = new Date();
        }
      },
      error: (error: any) => {
        console.error('Error loading gold rates:', error);
      }
    });
  }

  updateGoldRates(): void {
    if (this.goldRateForm.invalid) return;

    this.saving = true;
    const formValue = this.goldRateForm.value;

    // Backend expects a single daily rate payload (rate24k/rate21k/rate18k)
    // We treat the Sell rate fields as the authoritative rates.
    this.goldRateService.setDailyRate(formValue.karat24Sell, formValue.karat21Sell, formValue.karat18Sell).subscribe({
      next: () => {
        this.saving = false;
        this.lastUpdated = new Date();
        this.snackBar.open('Gold rates updated successfully!', 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      },
      error: (error: any) => {
        this.saving = false;
        console.error('Error updating gold rates:', error);
        this.snackBar.open('Failed to update gold rates', 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top'
        });
      }
    });
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }
}
