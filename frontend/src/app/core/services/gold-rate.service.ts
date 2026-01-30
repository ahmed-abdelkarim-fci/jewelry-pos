import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface GoldRate {
  karat: string;
  buyRate: number;
  sellRate: number;
  lastUpdated: string;
}

@Injectable({
  providedIn: 'root'
})
export class GoldRateService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/gold-rates';

  getCurrentRates(): Observable<GoldRate[]> {
    return this.http.get<GoldRate[]>(`${this.API_URL}/current`);
  }

  setDailyRate(rate24k: number, rate21k: number, rate18k: number): Observable<void> {
    return this.http.post<void>(`${this.API_URL}`, { rate24k, rate21k, rate18k });
  }

  getHistory(page: number = 0, size: number = 10): Observable<PageResponse<any>> {
    return this.http.get<PageResponse<any>>(`${this.API_URL}/history?page=${page}&size=${size}`);
  }
}
