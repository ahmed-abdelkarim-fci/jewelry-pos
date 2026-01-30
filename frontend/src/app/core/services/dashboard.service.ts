import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardData {
  salesRevenue: number;
  cost: number;
  netProfit: number;
  salesCount: number;
  itemsInStock: number;
  oldGoldBoughtWeight: number;
  oldGoldExpense: number;
  purificationIncome: number;
  scrapInventory?: {
    KARAT_21?: number;
    KARAT_18?: number;
    [key: string]: number | undefined;
  };
  lastUpdated: string;
}

export interface DashboardStatsDTO {
  salesRevenue: number;
  cost: number;
  netProfit: number;
  salesCount: number;
  itemsInStock: number;
  oldGoldBoughtWeight: number;
  oldGoldExpense: number;
  purificationIncome: number;
  scrapInventory?: { [key: string]: number | undefined };
  lastUpdated: string;
  fromDate: string;
  toDate: string;
}

export interface TopProductDTO {
  productId: string;
  barcode: string;
  modelName: string;
  salesCount: number;
  totalRevenue: number;
  totalWeight: number;
}

export interface UserPerformanceDTO {
  userId: string;
  username: string;
  fullName: string;
  salesCount: number;
  totalRevenue: number;
  averageSaleValue: number;
}

export interface SalesTrendDTO {
  date: string;
  salesCount: number;
  totalRevenue: number;
  netProfit: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/dashboard';

  getTodayDashboard(): Observable<DashboardData> {
    return this.http.get<DashboardData>(`${this.API_URL}/today`);
  }

  getStatsForDateRange(fromDate: string, toDate: string): Observable<DashboardStatsDTO> {
    const params = new HttpParams()
      .set('fromDate', fromDate)
      .set('toDate', toDate);
    return this.http.get<DashboardStatsDTO>(`${this.API_URL}/stats/range`, { params });
  }

  getTopProducts(fromDate?: string, toDate?: string, limit: number = 10): Observable<TopProductDTO[]> {
    let params = new HttpParams().set('limit', limit.toString());
    if (fromDate) params = params.set('fromDate', fromDate);
    if (toDate) params = params.set('toDate', toDate);
    return this.http.get<TopProductDTO[]>(`${this.API_URL}/top-products`, { params });
  }

  getUserPerformance(fromDate?: string, toDate?: string): Observable<UserPerformanceDTO[]> {
    let params = new HttpParams();
    if (fromDate) params = params.set('fromDate', fromDate);
    if (toDate) params = params.set('toDate', toDate);
    return this.http.get<UserPerformanceDTO[]>(`${this.API_URL}/user-performance`, { params });
  }

  getSalesTrends(fromDate?: string, toDate?: string): Observable<SalesTrendDTO[]> {
    let params = new HttpParams();
    if (fromDate) params = params.set('fromDate', fromDate);
    if (toDate) params = params.set('toDate', toDate);
    return this.http.get<SalesTrendDTO[]>(`${this.API_URL}/trends`, { params });
  }
}
