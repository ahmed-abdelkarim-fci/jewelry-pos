import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type NotificationType = 'REMINDER' | 'INFO' | 'WARNING';

export interface AppNotification {
  id: string;
  type: NotificationType;
  messageKey: string;
  actionLabelKey?: string;
  actionRoute?: string;
  createdAt: string; // ISO
  read: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly storageKey = 'app_notifications_v1';

  private readonly notificationsSubject = new BehaviorSubject<AppNotification[]>(this.load());
  readonly notifications$ = this.notificationsSubject.asObservable();

  getSnapshot(): AppNotification[] {
    return this.notificationsSubject.value;
  }

  getUnreadCount(): number {
    return this.notificationsSubject.value.filter(n => !n.read).length;
  }

  add(notification: AppNotification): void {
    const existing = this.notificationsSubject.value;
    if (existing.some(n => n.id === notification.id)) return;

    const next = [notification, ...existing];
    this.save(next);
    this.notificationsSubject.next(next);
  }

  markRead(id: string): void {
    const next = this.notificationsSubject.value.map(n =>
      n.id === id ? { ...n, read: true } : n
    );
    this.save(next);
    this.notificationsSubject.next(next);
  }

  markAllRead(): void {
    const next = this.notificationsSubject.value.map(n => ({ ...n, read: true }));
    this.save(next);
    this.notificationsSubject.next(next);
  }

  private load(): AppNotification[] {
    try {
      const raw = localStorage.getItem(this.storageKey);
      if (!raw) return [];
      const parsed = JSON.parse(raw);
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  }

  private save(items: AppNotification[]): void {
    localStorage.setItem(this.storageKey, JSON.stringify(items));
  }
}
