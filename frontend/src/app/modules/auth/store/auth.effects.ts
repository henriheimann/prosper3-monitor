import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';

import { catchError, map, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { of } from 'rxjs';

import * as AuthActions from './auth.actions';
import {
  loadUserFromLocalStorageFailure,
  loadUserFromLocalStorageSuccess,
  loginUserFailure,
  loginUserGetTokenSuccess,
  loginUserGetUserSuccess,
  logoutUserFailure,
  logoutUserSuccess
} from './auth.actions';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { selectAuthState } from './auth.selectors';
import { AuthService } from '../services/auth.service';
import { UserService } from '../../shared/services/user.service';

@Injectable()
export class AuthEffects {
  loginUser$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.loginUser),
      switchMap((action) => {
        return this.authService.login(action.username, action.password).pipe(
          map((response) => {
            return loginUserGetTokenSuccess({
              username: action.username,
              token: response.token
            });
          }),
          catchError((error) => of(loginUserFailure({ error })))
        );
      })
    );
  });

  onLoginUserGetTokenSuccessGetUser$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loginUserGetTokenSuccess),
      switchMap((action) =>
        this.userService.getByUsername(action.username).pipe(
          map((user) => loginUserGetUserSuccess({ user })),
          catchError((error) => of(loginUserFailure({ error })))
        )
      )
    )
  );

  onLoginUserGetUserSuccessSaveUserAndTokenToLocationStorage$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.loginUserGetUserSuccess),
        withLatestFrom(this.store.select(selectAuthState)),
        tap(([, state]) => {
          if (state.user != null && state.token != null) {
            this.authService.saveLocalStorage(state.user, state.token);
          }
        })
      ),
    { dispatch: false }
  );

  onLoginUserFailurePrintError$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.loginUserFailure),
        tap((action) => console.log('login failure: ' + JSON.stringify(action.error)))
      );
    },
    { dispatch: false }
  );

  onLoginUserFailureClearLocalStorage$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.loginUserFailure),
        tap(() => this.authService.clearLocalStorage())
      );
    },
    { dispatch: false }
  );

  loadUserFromLocalStorage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.loadUserFromLocalStorage),
      map(() => {
        const fromLocalStorage = this.authService.loadLocalStorage();
        if (fromLocalStorage != null) {
          return loadUserFromLocalStorageSuccess(fromLocalStorage);
        } else {
          return loadUserFromLocalStorageFailure();
        }
      })
    );
  });

  logoutUser$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.logoutUser),
      switchMap(() => {
        return this.authService.logout().pipe(
          map(() => {
            this.authService.clearLocalStorage();
            return logoutUserSuccess();
          }),
          catchError((error) => {
            this.authService.clearLocalStorage();
            return of(logoutUserFailure({ error }));
          })
        );
      })
    );
  });

  onLogoutUserSuccessRedirectToLandingPage$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.logoutUserSuccess),
        tap(() => {
          this.router.navigate(['']).then(() => {
            console.log('logout success');
          });
        })
      );
    },
    { dispatch: false }
  );

  logoutUserFailurePrintError$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.logoutUserFailure),
        tap((action) => console.log('login failure: ' + JSON.stringify(action.error)))
      );
    },
    { dispatch: false }
  );

  requestUnauthorizedClearLocalStorage$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.requestUnauthorized),
        tap(() => this.authService.clearLocalStorage())
      );
    },
    { dispatch: false }
  );

  constructor(
    private actions$: Actions,
    private store: Store,
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}
}
