import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromAuth from './auth.reducer';
import { AuthState } from './auth.reducer';

export const selectAuthState = createFeatureSelector<fromAuth.AuthState>(fromAuth.authFeatureKey);

export const selectToken = createSelector(selectAuthState, (state: AuthState) => state.token);

export const selectLoggingIn = createSelector(selectAuthState, (state: AuthState) => state.loggingIn);

export const selectIsLoggedIn = createSelector(selectAuthState, (state: AuthState) => state.user !== null);

export const selectLoggedInUsername = createSelector(selectAuthState, (state: AuthState) => state.user?.username);

export const selectLoggedInRole = createSelector(selectAuthState, (state: AuthState) => state.user?.role);
