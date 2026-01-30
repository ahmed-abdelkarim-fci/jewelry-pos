import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ConfigStatus {
  enabled: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/config';

  getGoldAutoUpdateStatus(): Observable<ConfigStatus> {
    return this.http.get<ConfigStatus>(`${this.API_URL}/gold-update`);
  }

  setGoldAutoUpdateStatus(enabled: boolean): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/gold-update`, { enabled });
  }

  getHardwareStatus(): Observable<ConfigStatus> {
    return this.http.get<ConfigStatus>(`${this.API_URL}/hardware`);
  }

  setHardwareStatus(enabled: boolean): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/hardware`, { enabled });
  }
}
