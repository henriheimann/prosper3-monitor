import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { loadUserFromLocalStorage } from './modules/auth/store/auth.actions';

@Component({
  selector: 'p3m-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(loadUserFromLocalStorage());
  }
}
