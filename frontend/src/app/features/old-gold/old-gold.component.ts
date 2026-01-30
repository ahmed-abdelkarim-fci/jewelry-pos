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
import { OldGoldService, ScrapInventory } from '../../core/services/old-gold.service';

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
    MatSnackBarModule
  ],
  templateUrl: './old-gold.component.html',
  styleUrls: ['./old-gold.component.scss']
})
export class OldGoldComponent implements OnInit {
  private fb = inject(FormBuilder);
  private oldGoldService = inject(OldGoldService);
  private snackBar = inject(MatSnackBar);

  purities = ['KARAT_24', 'KARAT_21', 'KARAT_18'];
  scrapInventory: ScrapInventory[] = [];

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
  }

  loadScrapInventory(): void {
    this.oldGoldService.getScrapInventory().subscribe({
      next: (inventory) => {
        this.scrapInventory = inventory;
      },
      error: () => {
        this.snackBar.open('Error loading scrap inventory', 'Close', { duration: 3000 });
      }
    });
  }

  getAvailableWeight(karat: string): number {
    const item = this.scrapInventory.find(s => s.karat === karat);
    return item?.availableWeight || 0;
  }

  onBuyCash(): void {
    if (this.buyCashForm.valid) {
      this.oldGoldService.buyCash(this.buyCashForm.value).subscribe({
        next: () => {
          this.snackBar.open('Old gold purchase recorded successfully', 'Close', { duration: 3000 });
          this.buyCashForm.reset();
          this.loadScrapInventory();
        },
        error: () => {
          this.snackBar.open('Error recording purchase', 'Close', { duration: 3000 });
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
        this.snackBar.open(`Insufficient scrap. Available: ${available}g`, 'Close', { duration: 3000 });
        return;
      }

      this.oldGoldService.purify(this.purificationForm.value).subscribe({
        next: () => {
          this.snackBar.open('Purification recorded successfully', 'Close', { duration: 3000 });
          this.purificationForm.reset();
          this.loadScrapInventory();
        },
        error: () => {
          this.snackBar.open('Error recording purification', 'Close', { duration: 3000 });
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
    return value.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }
}
