import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { combineLatest, Observable } from 'rxjs';
import { selectIsLoggedIn, selectLoggedInRole } from '../store/auth.selectors';
import { Store } from '@ngrx/store';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  private loggedIn$ = this.store.select(selectIsLoggedIn);
  private role$ = this.store.select(selectLoggedInRole);

  constructor(private store: Store, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return combineLatest([this.loggedIn$, this.role$]).pipe(
      map(([loggedIn, role]) => {
        const success =
          (typeof route.data['requiredRoles'] === 'string' && route.data['requiredRoles'] === role) ||
          (route.data['requiredRoles'] instanceof Array && route.data['requiredRoles'].indexOf(role) !== -1);

        if (!success && loggedIn) {
          this.router.navigate(['/']).then();
        }

        return success;
      })
    );
  }
}
