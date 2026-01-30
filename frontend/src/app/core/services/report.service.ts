import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ZReport {
  reportDate: string;
  totalTransactions: number;
  totalRevenue: number;
  totalGoldWeightSold: number;
}

export interface Transaction {
  id: number;
  date: string;
  type: string;
  amount: number;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/reports';

  getRecentTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.API_URL}/transactions`);
  }

  getZReport(date?: string): Observable<ZReport> {
    const url = date ? `${this.API_URL}/z-report?date=${date}` : `${this.API_URL}/z-report`;
    return this.http.get<ZReport>(url);
  }

  downloadReceipt(saleId: string): Observable<Blob> {
    return this.http.get(`${this.API_URL}/receipt/${saleId}`, { responseType: 'blob' });
  }

  getLabelZpl(barcode: string): Observable<string> {
    return this.http.get(`${this.API_URL}/label/${barcode}`, { responseType: 'text' });
  }
}
