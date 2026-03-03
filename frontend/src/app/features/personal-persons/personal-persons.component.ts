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
import { MatDialogModule } from '@angular/material/dialog';
import { PersonalPersonService, PersonalPerson } from '../../core/services/personal-person.service';
import { I18nService } from '../../core/services/i18n.service';
import { TPipe } from '../../shared/pipes/t.pipe';

@Component({
  selector: 'app-personal-persons',
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
  templateUrl: './personal-persons.component.html',
  styleUrls: ['./personal-persons.component.scss']
})
export class PersonalPersonsComponent implements OnInit {
  private fb = inject(FormBuilder);
  private personalPersonService = inject(PersonalPersonService);
  private snackBar = inject(MatSnackBar);
  private i18n = inject(I18nService);

  persons: PersonalPerson[] = [];
  displayedColumns = ['name', 'phoneNumber', 'address', 'actions'];
  loading = false;
  showAddForm = false;
  editingId: string | null = null;

  personForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    phoneNumber: ['', Validators.required],
    address: ['', Validators.required],
    notes: ['']
  });

  ngOnInit(): void {
    this.loadPersons();
  }

  loadPersons(): void {
    this.loading = true;
    this.personalPersonService.getAllPersons().subscribe({
      next: (data) => {
        this.persons = data;
        this.loading = false;
      },
      error: () => {
        this.snackBar.open(this.i18n.t('personalPersons.noData'), this.i18n.t('common.close'), { duration: 3000 });
        this.loading = false;
      }
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    this.editingId = null;
    if (!this.showAddForm) {
      this.personForm.reset();
    }
  }

  onSubmit(): void {
    if (!this.personForm.valid) return;

    const request = this.personForm.value;

    if (this.editingId) {
      this.personalPersonService.updatePerson(this.editingId, request).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('personalPersons.updateSuccess'), this.i18n.t('common.close'), { duration: 3000 });
          this.toggleAddForm();
          this.loadPersons();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('personalPersons.updateError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    } else {
      this.personalPersonService.createPerson(request).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('personalPersons.createSuccess'), this.i18n.t('common.close'), { duration: 3000 });
          this.toggleAddForm();
          this.loadPersons();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('personalPersons.createError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }

  editPerson(person: PersonalPerson): void {
    this.editingId = person.id;
    this.showAddForm = true;
    this.personForm.patchValue(person);
  }

  deletePerson(person: PersonalPerson): void {
    if (confirm(this.i18n.t('personalPersons.confirmDelete', { name: person.name }))) {
      this.personalPersonService.deletePerson(person.id).subscribe({
        next: () => {
          this.snackBar.open(this.i18n.t('personalPersons.deleteSuccess'), this.i18n.t('common.close'), { duration: 3000 });
          this.loadPersons();
        },
        error: () => {
          this.snackBar.open(this.i18n.t('personalPersons.deleteError'), this.i18n.t('common.close'), { duration: 3000 });
        }
      });
    }
  }
}
