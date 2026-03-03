import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface HomeExpense {
  id: string;
  transactionDate: string;
  description?: string;
  transactionType: 'RECEIVABLE' | 'PAYABLE';
  weight: number;
  money: number;
  createdBy?: string;
}

export interface HomeExpenseRequest {
  transactionDate: string;
  description?: string;
  transactionType: 'RECEIVABLE' | 'PAYABLE';
  weight?: number;
  money?: number;
}

export interface HomeExpenseSummary {
  totalMoneyReceivable: number;
  totalMoneyPayable: number;
  totalWeightReceivable: number;
  totalWeightPayable: number;
  netMoney: number;
  netWeight: number;
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
export class HomeExpenseService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/home-expenses';

  createTransaction(request: HomeExpenseRequest): Observable<HomeExpense> {
    return this.http.post<HomeExpense>(this.API_URL, request);
  }

  getAllTransactions(page: number = 0, size: number = 20): Observable<PageResponse<HomeExpense>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'lastModifiedDate,desc');
    return this.http.get<PageResponse<HomeExpense>>(this.API_URL, { params });
  }

  getSummary(): Observable<HomeExpenseSummary> {
    return this.http.get<HomeExpenseSummary>(`${this.API_URL}/summary`);
  }

  deleteTransaction(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
