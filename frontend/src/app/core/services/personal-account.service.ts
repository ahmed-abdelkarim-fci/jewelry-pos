import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PersonalAccount {
  id: string;
  personId: string;
  transactionDate: string;
  statement: string;
  transactionType: 'RECEIVABLE' | 'PAYABLE';
  weight: number;
  money: number;
  createdBy?: string;
}

export interface PersonalAccountRequest {
  personId: string;
  transactionDate: string;
  statement: string;
  transactionType: 'RECEIVABLE' | 'PAYABLE';
  weight?: number;
  money?: number;
}

export interface PersonalAccountSummary {
  personId: string;
  personName: string;
  netMoney: number;
  netWeight: number;
  moneyStatus: string;
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
export class PersonalAccountService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/personal-accounts';

  createTransaction(request: PersonalAccountRequest): Observable<PersonalAccount> {
    return this.http.post<PersonalAccount>(this.API_URL, request);
  }

  getAllTransactions(page: number = 0, size: number = 20): Observable<PageResponse<PersonalAccount>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'lastModifiedDate,desc');
    return this.http.get<PageResponse<PersonalAccount>>(this.API_URL, { params });
  }

  getTransactionsByPerson(personId: string, page: number = 0, size: number = 20): Observable<PageResponse<PersonalAccount>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'lastModifiedDate,desc');
    return this.http.get<PageResponse<PersonalAccount>>(`${this.API_URL}/person/${encodeURIComponent(personId)}`, { params });
  }

  getPersonSummaries(): Observable<PersonalAccountSummary[]> {
    return this.http.get<PersonalAccountSummary[]>(`${this.API_URL}/summaries`);
  }

  deleteTransaction(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
