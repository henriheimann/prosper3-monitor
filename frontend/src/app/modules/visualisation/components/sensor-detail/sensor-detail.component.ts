import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectSelectedDevice } from '../../store/visualisation.selectors';
import { selectDevice, startDraggingSelectedDevice } from '../../store/visualisation.actions';

@Component({
  selector: 'p3m-sensor-detail',
  templateUrl: './sensor-detail.component.html',
  styleUrls: ['./sensor-detail.component.sass']
})
export class SensorDetailComponent {
  selectedDevice$ = this.store.select(selectSelectedDevice);

  constructor(private store: Store) {}

  onCloseClicked(): void {
    this.store.dispatch(
      selectDevice({
        device: null
      })
    );
  }

  onMoveClicked(): void {
    this.store.dispatch(startDraggingSelectedDevice());
  }

  onEditClicked(): void {}
}
