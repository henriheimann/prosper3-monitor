import { createReducer, on } from '@ngrx/store';
import * as AuthActions from './auth.actions';
import { UserModel } from '../../shared/models/user.model';

export const authFeatureKey = 'auth';

export interface AuthState {
  token: string | null;
  loggingIn: boolean;

  user: UserModel | null;
}

export const initialState: AuthState = {
  token: null,
  loggingIn: false,

  user: null
};

export const reducer = createReducer(
  initialState,

  on(AuthActions.loginUser, (state) => {
    return {
      ...state,
      loggingIn: true
    };
  }),

  on(AuthActions.loginUserGetTokenSuccess, (state, action) => {
    return {
      ...state,
      token: action.token,
      loggingIn: false
    };
  }),

  on(AuthActions.loginUserGetUserSuccess, (state, action) => ({
    ...state,
    user: action.user,
    loggingIn: false
  })),

  on(AuthActions.loginUserFailure, (state) => {
    return {
      ...state,
      user: null,
      token: null,
      loggingIn: false
    };
  }),

  on(AuthActions.loadUserFromLocalStorage, (state) => {
    return {
      ...state,
      loggingIn: true
    };
  }),

  on(AuthActions.loadUserFromLocalStorageSuccess, (state, action) => {
    return {
      ...state,
      user: action.user,
      token: action.token,
      loggingIn: false
    };
  }),

  on(AuthActions.loadUserFromLocalStorageFailure, (state) => {
    return {
      ...state,
      user: null,
      token: null,
      loggingIn: false
    };
  }),

  on(AuthActions.logoutUser, (state) => {
    return {
      ...state
    };
  }),

  on(AuthActions.logoutUserSuccess, (state) => {
    return {
      ...state,
      user: null,
      token: null
    };
  }),

  on(AuthActions.logoutUserSuccess, (state) => {
    return {
      ...state,
      user: null,
      token: null
    };
  }),

  on(AuthActions.requestUnauthorized, (state) => {
    return {
      ...state,
      user: null,
      token: null
    };
  })
);
