import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PersonalPerson {
  id: string;
  name: string;
  phoneNumber?: string;
  address?: string;
  notes?: string;
}

export interface PersonalPersonRequest {
  name: string;
  phoneNumber?: string;
  address?: string;
  notes?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PersonalPersonService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/personal-persons';

  getAllPersons(): Observable<PersonalPerson[]> {
    return this.http.get<PersonalPerson[]>(this.API_URL);
  }

  getPersonById(id: string): Observable<PersonalPerson> {
    return this.http.get<PersonalPerson>(`${this.API_URL}/${id}`);
  }

  createPerson(request: PersonalPersonRequest): Observable<PersonalPerson> {
    return this.http.post<PersonalPerson>(this.API_URL, request);
  }

  updatePerson(id: string, request: PersonalPersonRequest): Observable<PersonalPerson> {
    return this.http.put<PersonalPerson>(`${this.API_URL}/${id}`, request);
  }

  deletePerson(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
