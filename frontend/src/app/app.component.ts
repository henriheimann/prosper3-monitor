import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { loadUserFromLocalStorage } from './modules/auth/store/auth.actions';
import { ActivatedRouteSnapshot, NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'p3m-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent implements OnInit {
  hideNavigation = false;

  routeData$ = this.router.events.pipe(
    filter((event) => event instanceof NavigationEnd),
    map(() => {
      const getParams = (route: ActivatedRouteSnapshot): any => ({
        ...route.data,
        ...route.children.reduce((acc, child) => ({ ...getParams(child), ...acc }), {})
      });
      return getParams(this.router.routerState.snapshot.root);
    })
  );

  constructor(private store: Store, private router: Router) {}

  ngOnInit(): void {
    this.store.dispatch(loadUserFromLocalStorage());

    this.routeData$.subscribe((data) => {
      this.hideNavigation = data?.hideNavigation || false;
    });
  }
}
