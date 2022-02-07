import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { UserModel } from '../../shared/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private static LOCAL_STORAGE_AUTH_USER = 'auth.user';
  private static LOCAL_STORAGE_AUTH_TOKEN = 'auth.token';

  constructor(private httpClient: HttpClient) {}

  login(username: string, password: string): Observable<{ token: string }> {
    return this.httpClient
      .post<{ token: string }>(`${environment.backendUrl}/auth/login`, {
        username,
        password
      })
      .pipe(
        map((response) => ({
          token: response.token
        }))
      );
  }

  logout(): Observable<void> {
    return this.httpClient.post<void>(`${environment.backendUrl}/auth/logout`, null);
  }

  clearLocalStorage(): void {
    localStorage.removeItem(AuthService.LOCAL_STORAGE_AUTH_USER);
    localStorage.removeItem(AuthService.LOCAL_STORAGE_AUTH_TOKEN);
  }

  saveLocalStorage(user: UserModel, token: string): void {
    localStorage.setItem(AuthService.LOCAL_STORAGE_AUTH_USER, JSON.stringify(user));
    localStorage.setItem(AuthService.LOCAL_STORAGE_AUTH_TOKEN, token);
  }

  loadLocalStorage(): { user: UserModel; token: string } | null {
    if (localStorage.getItem(AuthService.LOCAL_STORAGE_AUTH_USER) != null) {
      return {
        user: JSON.parse(localStorage.getItem(AuthService.LOCAL_STORAGE_AUTH_USER) || '{}') as unknown as UserModel,
        token: localStorage.getItem(AuthService.LOCAL_STORAGE_AUTH_TOKEN) || ''
      };
    } else {
      return null;
    }
  }
}
