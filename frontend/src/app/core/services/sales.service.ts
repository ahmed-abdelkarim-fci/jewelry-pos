import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Sale {
  id: string;
  date: string;
  totalAmount: number;
  oldGoldTotalValue: number;
  netCashPaid: number;
  createdBy: string;
  customerName: string;
  customerPhone: string;
  items: SaleItem[];
}

export interface SaleItem {
  productName: string;
  weight: number;
  priceSnapshot: number;
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
export class SalesService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/sales';

  getAllSales(
    page: number = 0,
    size: number = 20,
    query?: string,
    fromDate?: string,
    toDate?: string
  ): Observable<PageResponse<Sale>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'transactionDate,desc');

    if (query) params = params.set('query', query);
    if (fromDate) params = params.set('fromDate', fromDate);
    if (toDate) params = params.set('toDate', toDate);

    return this.http.get<PageResponse<Sale>>(this.API_URL, { params });
  }

  getSaleById(id: string): Observable<Sale> {
    return this.http.get<Sale>(`${this.API_URL}/${id}`);
  }

  voidSale(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
