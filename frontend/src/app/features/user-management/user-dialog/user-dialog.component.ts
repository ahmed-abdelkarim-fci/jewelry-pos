import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UserService, User } from '../../../core/services/user.service';

@Component({
  selector: 'app-user-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  templateUrl: './user-dialog.component.html',
  styleUrls: ['./user-dialog.component.scss']
})
export class UserDialogComponent {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private snackBar = inject(MatSnackBar);
  private dialogRef = inject(MatDialogRef<UserDialogComponent>);

  isEditMode = false;
  saving = false;

  roleOptions = ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'MANAGER', 'CASHIER', 'ROLE_USER'];

  form: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public data: User | null) {
    this.isEditMode = !!data;

    this.form = this.fb.group({
      firstName: [data?.firstName || '', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      lastName: [data?.lastName || '', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      username: [{ value: data?.username || '', disabled: this.isEditMode }, [Validators.required, Validators.minLength(3), Validators.maxLength(20), Validators.pattern(/^[a-zA-Z0-9_]*$/)]],
      password: ['', this.isEditMode ? [] : [Validators.required, Validators.minLength(6)]],
      enabled: [data?.enabled ?? true],
      roles: [data?.roles || [], [Validators.required]]
    });
  }

  save(): void {
    if (this.form.invalid) return;
    this.saving = true;

    if (this.isEditMode && this.data) {
      const payload = {
        firstName: this.form.get('firstName')?.value,
        lastName: this.form.get('lastName')?.value,
        enabled: this.form.get('enabled')?.value,
        roles: this.form.get('roles')?.value
      };

      this.userService.updateUser(this.data.id, payload).subscribe({
        next: () => {
          this.saving = false;
          this.snackBar.open('User updated', 'Close', { duration: 3000 });
          this.dialogRef.close(true);
        },
        error: () => {
          this.saving = false;
          this.snackBar.open('Error updating user', 'Close', { duration: 3000 });
        }
      });
      return;
    }

    const payload = {
      firstName: this.form.get('firstName')?.value,
      lastName: this.form.get('lastName')?.value,
      username: this.form.get('username')?.value,
      password: this.form.get('password')?.value,
      roles: this.form.get('roles')?.value
    };

    this.userService.createUser(payload).subscribe({
      next: () => {
        this.saving = false;
        this.snackBar.open('User created', 'Close', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('Error creating user', 'Close', { duration: 3000 });
      }
    });
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
