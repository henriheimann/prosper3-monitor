import { Component, OnInit } from '@angular/core';
import { selectLoggedInRole } from '../../../auth/store/auth.selectors';
import { map } from 'rxjs/operators';
import { Store } from '@ngrx/store';

@Component({
  selector: 'p3m-page-main-dashboard',
  templateUrl: './page-main-dashboard.component.html',
  styleUrls: ['./page-main-dashboard.component.sass']
})
export class PageMainDashboardComponent {

  isUserAdmin$ = this.store.select(selectLoggedInRole).pipe(
    map((role) => {
      return role === 'ADMIN';
    })
  );

  constructor(private store: Store) {}
}
