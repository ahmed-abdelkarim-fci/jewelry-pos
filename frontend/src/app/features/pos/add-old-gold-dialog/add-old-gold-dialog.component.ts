import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { I18nService } from '../../../core/services/i18n.service';
import { TPipe } from '../../../shared/pipes/t.pipe';

@Component({
  selector: 'app-add-old-gold-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    TPipe
  ],
  templateUrl: './add-old-gold-dialog.component.html',
  styleUrls: ['./add-old-gold-dialog.component.scss']
})
export class AddOldGoldDialogComponent {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<AddOldGoldDialogComponent>);
  private i18n = inject(I18nService);

  purities = ['KARAT_24', 'KARAT_21', 'KARAT_18'];

  oldGoldForm: FormGroup = this.fb.group({
    purity: ['', Validators.required],
    weight: ['', [Validators.required, Validators.min(0.1)]],
    buyRate: ['', [Validators.required, Validators.min(1)]],
    customerNationalId: ['', [Validators.required, Validators.pattern(/^\d{14}$/)]],
    customerPhoneNumber: ['', Validators.pattern(/^\d{10,15}$/)],
    description: ['']
  });

  getTotalValue(): number {
    const weight = this.oldGoldForm.get('weight')?.value || 0;
    const buyRate = this.oldGoldForm.get('buyRate')?.value || 0;
    return weight * buyRate;
  }

  formatCurrency(value: number): string {
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    return value.toLocaleString(locale, { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  onSubmit(): void {
    if (this.oldGoldForm.valid) {
      const formValue = this.oldGoldForm.value;
      this.dialogRef.close({
        ...formValue,
        totalValue: this.getTotalValue()
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
