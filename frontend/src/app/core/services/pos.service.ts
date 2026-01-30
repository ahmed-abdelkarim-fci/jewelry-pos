import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Product {
  id: string;
  barcode: string;
  modelName: string;
  purityEnum: string;
  type: string;
  grossWeight: number;
  makingCharge: number;
  status: string;
  costPrice: number;
  estimatedPrice: number;
}

export interface OldGoldItem {
  purity: string;
  weight: number;
  buyRate: number;
  customerNationalId: string;
  customerPhoneNumber?: string;
  description?: string;
}

export interface SaleRequest {
  barcodes: string[];
  currentGoldRate: number;
  customerName: string;
  customerPhone?: string;
  tradeInItems?: OldGoldItem[];
}

export interface SaleResponse {
  id: string;
  date: string;
  totalAmount: number;
  oldGoldTotalValue: number;
  netCashPaid: number;
  createdBy: string;
  customerName: string;
  customerPhone: string;
  items: SaleItemDTO[];
}

export interface SaleItemDTO {
  productName: string;
  weight: number;
  priceSnapshot: number;
}

@Injectable({
  providedIn: 'root'
})
export class PosService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/sales';

  getProductByBarcode(barcode: string): Observable<Product> {
    return this.http.post<Product>(`/api/pos/scan/${barcode}`, {});
  }

  createSale(saleRequest: SaleRequest): Observable<void> {
    return this.http.post<void>(this.API_URL, saleRequest);
  }

  getSales(page: number = 0, size: number = 20): Observable<any> {
    return this.http.get(`${this.API_URL}?page=${page}&size=${size}`);
  }

  getSaleById(id: string): Observable<SaleResponse> {
    return this.http.get<SaleResponse>(`${this.API_URL}/${id}`);
  }

  voidSale(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
