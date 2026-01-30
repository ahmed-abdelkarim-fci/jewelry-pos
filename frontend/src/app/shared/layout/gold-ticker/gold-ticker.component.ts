import { Component, OnInit, inject, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GoldRateService, GoldRate } from '../../../core/services/gold-rate.service';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-gold-ticker',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gold-ticker.component.html',
  styleUrls: ['./gold-ticker.component.scss']
})
export class GoldTickerComponent implements OnInit, OnDestroy {
  private goldRateService = inject(GoldRateService);
  goldRates: GoldRate[] = [];
  private subscription?: Subscription;

  ngOnInit(): void {
    this.loadGoldRates();
    this.subscription = interval(60000)
      .pipe(switchMap(() => this.goldRateService.getCurrentRates()))
      .subscribe(rates => this.goldRates = rates);
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  loadGoldRates(): void {
    this.goldRateService.getCurrentRates().subscribe(
      rates => this.goldRates = rates
    );
  }

  formatPrice(price: number): string {
    return price.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  getKaratLabel(karat: string): string {
    const labels: { [key: string]: string } = {
      'KARAT_24': 'عيار 24',
      'KARAT_21': 'عيار 21',
      'KARAT_18': 'عيار 18'
    };
    return labels[karat] || karat;
  }
}
