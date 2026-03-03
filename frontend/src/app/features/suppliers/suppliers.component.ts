import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { SupplierService, Supplier } from '../../core/services/supplier.service';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

@Component({
  selector: 'app-suppliers',
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
    MatSnackBarModule,
    MatDialogModule,
    TPipe
  ],
  templateUrl: './suppliers.component.html',
  styleUrls: ['./suppliers.component.scss']
})
export class SuppliersComponent implements OnInit {
  private fb = inject(FormBuilder);
  private supplierService = inject(SupplierService);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);
  private router = inject(Router);

  suppliers: Supplier[] = [];
  displayedColumns = ['name', 'phoneNumber', 'address', 'account', 'actions'];
  loading = false;
  showAddForm = false;
  editingId: string | null = null;

  supplierForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    phoneNumber: ['', Validators.required],
    address: ['', Validators.required],
    notes: ['']
  });

  ngOnInit(): void {
    this.loadSuppliers();
  }

  loadSuppliers(): void {
    this.loading = true;
    this.supplierService.getAllSuppliers().subscribe({
      next: (data) => {
        this.suppliers = data;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('suppliers.noData'), this.i18n.t('common.close'), { duration: 3000 });
        this.loading = false;
      }
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    this.editingId = null;
    if (!this.showAddForm) {
      this.supplierForm.reset();
    }
  }

  onSubmit(): void {
    if (this.supplierForm.valid) {
      const request = this.supplierForm.value;

      if (this.editingId) {
        this.supplierService.updateSupplier(this.editingId, request).subscribe({
          next: () => {
            this.snackBar.open(this.i18n.t('suppliers.updateSuccess'), this.i18n.t('common.close'), { duration: 3000 });
            this.toggleAddForm();
            this.loadSuppliers();
          },
          error: () => {
            this.snackBar.open(this.i18n.t('suppliers.updateError'), this.i18n.t('common.close'), { duration: 3000 });
          }
        });
      } else {
        this.supplierService.createSupplier(request).subscribe({
          next: () => {
            this.snackBar.open(this.i18n.t('suppliers.createSuccess'), this.i18n.t('common.close'), { duration: 3000 });
            this.toggleAddForm();
            this.loadSuppliers();
          },
          error: () => {
            this.snackBar.open(this.i18n.t('suppliers.createError'), this.i18n.t('common.close'), { duration: 3000 });
          }
        });
      }
    }
  }

  editSupplier(supplier: Supplier): void {
    this.editingId = supplier.id;
    this.showAddForm = true;
    this.supplierForm.patchValue(supplier);
  }

  deleteSupplier(supplier: Supplier): void {
    if (confirm(this.i18n.t('suppliers.confirmDelete', { name: supplier.name }))) {
      this.supplierService.deleteSupplier(supplier.id).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('suppliers.deleteSuccess'), this.i18n.t('common.close'), { duration: 3000 });
          this.loadSuppliers();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('suppliers.deleteError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  openAccountDetails(supplier: Supplier): void {
    this.router.navigate(['/supplier-accounts', supplier.id], { state: { supplierName: supplier.name } });
  }
}
