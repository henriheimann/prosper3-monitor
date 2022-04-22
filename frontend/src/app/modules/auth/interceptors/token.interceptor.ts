import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { selectToken } from '../store/auth.selectors';
import { Store } from '@ngrx/store';
import { catchError } from 'rxjs/operators';
import { requestUnauthorized } from '../store/auth.actions';
import { Router } from '@angular/router';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  private token$ = new BehaviorSubject<string | null>(null);

  constructor(private store: Store, private router: Router) {
    this.store.select(selectToken).subscribe(this.token$);
  }

  private static addToken(request: HttpRequest<any>, token: string): any {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (request.url.includes('/auth/login')) {
      return next.handle(request);
    }

    const token = this.token$.getValue();
    if (token) {
      request = TokenInterceptor.addToken(request, token);
    }

    return next.handle(request).pipe(
      catchError((error) => {
        if (error.status === 401) {
          this.store.dispatch(requestUnauthorized());
          this.router.navigate(['/']).then();
        }
        return throwError(error);
      })
    );
  }
}
