import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationSubject = new BehaviorSubject<string | null>(null);

  getNotification(): Observable<string | null> {
    return this.notificationSubject.asObservable();
  }

  showSuccess(message: string): void {
    this.notificationSubject.next(message);
    setTimeout(() => this.notificationSubject.next(null), 3000);
  }

  showError(message: string): void {
    this.notificationSubject.next(message);
    setTimeout(() => this.notificationSubject.next(null), 3000);
  }
}
