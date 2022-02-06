import { createAction, props } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import { UserModel } from '../../shared/models/user.model';

export const loginUser = createAction(
  '[Auth] Login User', props<{ username: string, password: string }>()
);

export const loginUserGetTokenSuccess = createAction(
  '[Auth] Login User Get Token Success', props<{ username: string, token: string }>()
);

export const loginUserGetUserSuccess = createAction(
  '[Auth] Login User Get User Success', props<{ user: UserModel }>()
);

export const loginUserFailure = createAction(
  '[Auth] Login User Failure', props<{ error: HttpErrorResponse }>()
);

export const loadUserFromLocalStorage = createAction(
  '[Auth] Load User From Local Storage'
);

export const loadUserFromLocalStorageSuccess = createAction(
  '[Auth] Load User From Local Storage Success', props<{ user: UserModel, token: string }>()
);

export const loadUserFromLocalStorageFailure = createAction(
  '[Auth] Load User From Local Storage Failure'
);

export const logoutUser = createAction(
  '[Auth] Logout User'
);

export const logoutUserSuccess = createAction(
  '[Auth] Logout User Success'
);

export const logoutUserFailure = createAction(
  '[Auth] Logout User Failure', props<{ error: HttpErrorResponse }>()
);

export const requestUnauthorized = createAction(
  '[Auth] Request Unauthorized'
);
