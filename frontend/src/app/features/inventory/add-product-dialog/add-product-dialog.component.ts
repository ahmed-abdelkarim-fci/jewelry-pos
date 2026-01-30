import { Component, inject, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { InventoryService, Product } from '../../../core/services/inventory.service';
import { I18nService } from '../../../core/services/i18n.service';
import { TPipe } from '../../../shared/pipes/t.pipe';

@Component({
  selector: 'app-add-product-dialog',
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
    MatSnackBarModule,
    TPipe
  ],
  templateUrl: './add-product-dialog.component.html',
  styleUrls: ['./add-product-dialog.component.scss']
})
export class AddProductDialogComponent {
  private fb = inject(FormBuilder);
  private inventoryService = inject(InventoryService);
  private dialogRef = inject(MatDialogRef<AddProductDialogComponent>);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);

  purities = ['K24', 'K21', 'K18'];
  types = [
    'RING',
    'NECKLACE',
    'PENDANT',
    'CHAIN',
    'BRACELET',
    'BANGLE',
    'EARRING',
    'ANKLET',
    'BROOCH',
    'CHARM',
    'SET',
    'CUFFLINKS',
    'OTHER'
  ];
  isEditMode = false;
  showCostPrice = false;

  productForm: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public data: Product | null) {
    this.isEditMode = !!data;
    this.productForm = this.fb.group({
      barcode: [{value: data?.barcode || '', disabled: this.isEditMode}, [Validators.pattern(/^[A-Za-z0-9-]+$/)]],
      modelName: [data?.modelName || '', [Validators.required, Validators.maxLength(100)]],
      purityEnum: [data?.purityEnum || '', Validators.required],
      type: [data?.type || '', Validators.required],
      grossWeight: [data?.grossWeight || '', [Validators.required, Validators.min(0.001)]],
      makingCharge: [data?.makingCharge || '', [Validators.required, Validators.min(0)]],
      description: [data?.description || ''],
      costPrice: [data?.costPrice || '', [Validators.required, Validators.min(1)]]
    });
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      const request = this.productForm.value;
      
      if (this.isEditMode && this.data) {
        this.inventoryService.updateProduct(this.data.id, request).subscribe({
          next: () => {
            this.snackBar.open(this.i18n.t('product.updateSuccess'), this.i18n.t('common.close'), { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: () => {
            this.snackBar.open(this.i18n.t('product.updateError'), this.i18n.t('common.close'), { duration: 3000 });
          }
        });
      } else {
        this.inventoryService.createProduct(request).subscribe({
          next: () => {
            this.snackBar.open(this.i18n.t('product.addSuccess'), this.i18n.t('common.close'), { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: () => {
            this.snackBar.open(this.i18n.t('product.addError'), this.i18n.t('common.close'), { duration: 3000 });
          }
        });
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getTypeLabel(type: string): string {
    return this.i18n.t(`jewelryType.${type}`);
  }
}
