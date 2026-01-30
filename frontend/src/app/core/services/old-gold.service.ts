import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface OldGoldPurchaseRequest {
  purity: string;
  weight: number;
  buyRate: number;
  customerNationalId: string;
  customerPhoneNumber?: string;
  description?: string;
}

export interface PurificationRequest {
  purity: string;
  weightToSell: number;
  cashReceived: number;
  factoryName: string;
}

export interface ScrapInventory {
  karat: string;
  availableWeight: number;
}

@Injectable({
  providedIn: 'root'
})
export class OldGoldService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/old-gold';

  buyCash(request: OldGoldPurchaseRequest): Observable<any> {
    return this.http.post(`${this.API_URL}/buy`, request);
  }

  purify(request: PurificationRequest): Observable<any> {
    return this.http.post(`${this.API_URL}/purify`, request);
  }

  getScrapInventory(): Observable<ScrapInventory[]> {
    return this.http.get<ScrapInventory[]>(`${this.API_URL}/scrap-inventory`);
  }
}
