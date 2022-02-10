import { Component, OnInit } from '@angular/core';
import { startDraggingSelectedDevice, stopDraggingSelectedDevice } from '../../store/visualisation.actions';
import { Store } from '@ngrx/store';

@Component({
  selector: 'p3m-stop-dragging-mode',
  templateUrl: './stop-dragging-mode.component.html',
  styleUrls: ['./stop-dragging-mode.component.sass']
})
export class StopDraggingModeComponent {
  constructor(private store: Store) {}

  onStopDraggingClicked(): void {
    this.store.dispatch(stopDraggingSelectedDevice());
  }
}
