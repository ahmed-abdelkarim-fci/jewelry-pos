import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SupplierAccount {
  id: string;
  supplierId: string;
  transactionDate: string;
  statement?: string;
  transactionType: 'RECEIVABLE' | 'PAYABLE';
  weight: number;
  fees: number;
  numberOfPieces?: number;
  purificationId?: string;
  createdBy?: string;
}

export interface SupplierAccountRequest {
  supplierId: string;
  transactionDate: string;
  statement?: string;
  transactionType: 'RECEIVABLE' | 'PAYABLE';
  weight?: number;
  fees?: number;
  numberOfPieces?: number;
  purificationId?: string;
}

export interface SupplierAccountSummary {
  supplierId: string;
  supplierName: string;
  netFees: number;
  netWeight: number;
  feesStatus: string;
  weightStatus: string;
  transactionCount: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class SupplierAccountService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/supplier-accounts';

  createTransaction(request: SupplierAccountRequest): Observable<SupplierAccount> {
    return this.http.post<SupplierAccount>(this.API_URL, request);
  }

  getAllTransactions(page: number = 0, size: number = 20): Observable<PageResponse<SupplierAccount>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'lastModifiedDate,desc');
    return this.http.get<PageResponse<SupplierAccount>>(this.API_URL, { params });
  }

  getTransactionsBySupplier(supplierId: string, page: number = 0, size: number = 20): Observable<PageResponse<SupplierAccount>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'lastModifiedDate,desc');
    return this.http.get<PageResponse<SupplierAccount>>(`${this.API_URL}/supplier/${supplierId}`, { params });
  }

  getSupplierSummaries(): Observable<SupplierAccountSummary[]> {
    return this.http.get<SupplierAccountSummary[]>(`${this.API_URL}/summaries`);
  }

  deleteTransaction(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
