import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Supplier {
  id: string;
  name: string;
  phoneNumber?: string;
  address?: string;
  notes?: string;
}

export interface SupplierRequest {
  name: string;
  phoneNumber?: string;
  address?: string;
  notes?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SupplierService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/suppliers';

  getAllSuppliers(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(this.API_URL);
  }

  getSupplierById(id: string): Observable<Supplier> {
    return this.http.get<Supplier>(`${this.API_URL}/${id}`);
  }

  createSupplier(request: SupplierRequest): Observable<Supplier> {
    return this.http.post<Supplier>(this.API_URL, request);
  }

  updateSupplier(id: string, request: SupplierRequest): Observable<Supplier> {
    return this.http.put<Supplier>(`${this.API_URL}/${id}`, request);
  }

  deleteSupplier(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
