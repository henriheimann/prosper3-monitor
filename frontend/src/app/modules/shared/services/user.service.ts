import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserModel } from '../models/user.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  users$ = new BehaviorSubject<UserModel[] | null>(null);

  constructor(private httpClient: HttpClient) {}

  getByUsername(username: string): Observable<UserModel> {
    return this.httpClient.get<UserModel>(`${environment.backendUrl}/users/${username}`);
  }

  getAll(): BehaviorSubject<UserModel[] | null> {
    this.httpClient
      .get<UserModel[]>(`${environment.backendUrl}/users`)
      .subscribe((userModels) => this.users$.next(userModels));
    return this.users$;
  }

  changePassword(username: string, request: { oldPassword: string; newPassword: string }): Observable<void> {
    return this.httpClient.put<void>(`${environment.backendUrl}/users/${username}/password`, request);
  }

  deleteByUserName(username: string): Observable<void> {
    return this.httpClient.delete<void>(`${environment.backendUrl}/users/${username}`).pipe(tap(() => this.getAll()));
  }

  createUser(userModel: UserModel): Observable<UserModel> {
    return this.httpClient.post<UserModel>(`${environment.backendUrl}/users`, userModel).pipe(tap(() => this.getAll()));
  }

  editUser(username: string, userModel: Partial<UserModel>) {
    return this.httpClient
      .put<UserModel>(`${environment.backendUrl}/users/${username}`, userModel)
      .pipe(tap(() => this.getAll()));
  }
}
