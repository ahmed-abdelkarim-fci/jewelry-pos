import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

export type LangCode = 'ar' | 'en';

@Injectable({
  providedIn: 'root'
})
export class I18nService {
  private http = inject(HttpClient);

  private lang: LangCode = 'ar';
  private dict: Record<string, string> = {};

  get currentLang(): LangCode {
    return this.lang;
  }

  async init(): Promise<void> {
    const saved = (localStorage.getItem('lang') as LangCode | null) ?? 'ar';
    await this.setLanguage(saved);
  }

  async setLanguage(lang: LangCode): Promise<void> {
    this.lang = lang;
    localStorage.setItem('lang', lang);

    this.dict = await firstValueFrom(this.http.get<Record<string, string>>(`/assets/i18n/${lang}.json`));

    const html = document.documentElement;
    html.lang = lang;
    html.dir = lang === 'ar' ? 'rtl' : 'ltr';
  }

  t(key: string, params?: Record<string, string | number>): string {
    const template = this.dict[key] ?? key;
    if (!params) return template;

    return Object.keys(params).reduce((acc, k) => {
      return acc.replaceAll(`{{${k}}}`, String(params[k]));
    }, template);
  }
}
