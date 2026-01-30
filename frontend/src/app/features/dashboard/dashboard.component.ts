import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { DashboardService, DashboardData, DashboardStatsDTO, SalesTrendDTO, TopProductDTO, UserPerformanceDTO } from '../../core/services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  
  dashboardData?: DashboardData;
  loading = true;

  // Enhanced dashboard
  fromDate: Date | null = null;
  toDate: Date | null = null;
  rangeStats?: DashboardStatsDTO;
  topProducts: TopProductDTO[] = [];
  userPerformance: UserPerformanceDTO[] = [];
  salesTrends: SalesTrendDTO[] = [];
  loadingEnhanced = false;

  topProductsColumns = ['modelName', 'salesCount', 'totalWeight', 'totalRevenue'];
  userPerformanceColumns = ['fullName', 'salesCount', 'totalRevenue', 'averageSaleValue'];
  trendsColumns = ['date', 'salesCount', 'totalRevenue', 'netProfit'];

  ngOnInit(): void {
    this.loadDashboard();
    this.setDefaultRangeAndLoad();
  }

  loadDashboard(): void {
    this.dashboardService.getTodayDashboard().subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading dashboard:', error);
        this.loading = false;
      }
    });
  }

  private setDefaultRangeAndLoad(): void {
    const now = new Date();
    const from = new Date(now);
    from.setDate(now.getDate() - 30);
    this.fromDate = from;
    this.toDate = now;
    this.loadEnhanced();
  }

  loadEnhanced(): void {
    if (!this.fromDate || !this.toDate) return;
    this.loadingEnhanced = true;

    const fromDateStr = this.formatDate(this.fromDate);
    const toDateStr = this.formatDate(this.toDate);

    this.dashboardService.getStatsForDateRange(fromDateStr, toDateStr).subscribe({
      next: (stats) => {
        this.rangeStats = stats;
      },
      error: (error) => {
        console.error('Error loading range stats:', error);
      }
    });

    this.dashboardService.getTopProducts(fromDateStr, toDateStr, 10).subscribe({
      next: (data) => {
        this.topProducts = data;
      },
      error: (error) => {
        console.error('Error loading top products:', error);
      }
    });

    this.dashboardService.getUserPerformance(fromDateStr, toDateStr).subscribe({
      next: (data) => {
        this.userPerformance = data;
      },
      error: (error) => {
        console.error('Error loading user performance:', error);
      }
    });

    this.dashboardService.getSalesTrends(fromDateStr, toDateStr).subscribe({
      next: (data) => {
        this.salesTrends = data;
        this.loadingEnhanced = false;
      },
      error: (error) => {
        console.error('Error loading sales trends:', error);
        this.loadingEnhanced = false;
      }
    });
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  formatCurrency(value: number | undefined): string {
    if (!value && value !== 0) return '0';
    return value.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  formatDateLabel(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }

  getScrapPercentage(weight: number | undefined, maxWeight: number = 500): number {
    if (!weight) return 0;
    return (weight / maxWeight) * 100;
  }

  getScrapColor(percentage: number): string {
    if (percentage < 30) return 'var(--success)';
    if (percentage < 70) return 'var(--warning)';
    return 'var(--danger)';
  }
}
