import { Component, OnInit, inject, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GoldRateService, GoldRate } from '../../../core/services/gold-rate.service';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { I18nService } from '../../../core/services/i18n.service';
import { TPipe } from '../../pipes/t.pipe';

@Component({
  selector: 'app-gold-ticker',
  standalone: true,
  imports: [CommonModule, TPipe],
  templateUrl: './gold-ticker.component.html',
  styleUrls: ['./gold-ticker.component.scss']
})
export class GoldTickerComponent implements OnInit, OnDestroy {
  private goldRateService = inject(GoldRateService);
  private i18n = inject(I18nService);
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
    const locale = this.i18n.currentLang === 'ar' ? 'ar-EG' : 'en-US';
    return price.toLocaleString(locale, { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }

  getKaratLabel(karat: string): string {
    const keys: { [key: string]: string } = {
      'KARAT_24': 'gold.karat24',
      'KARAT_21': 'gold.karat21',
      'KARAT_18': 'gold.karat18'
    };
    const key = keys[karat];
    return key ? this.i18n.t(key) : karat;
  }
}
