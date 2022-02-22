import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { startIdentifyingDevice } from '../../store/visualisation.actions';

@Component({
  selector: 'p3m-button-identify-device',
  templateUrl: './button-identify-device.component.html',
  styleUrls: ['./button-identify-device.component.sass']
})
export class ButtonIdentifyDeviceComponent {
  constructor(private store: Store) {}

  onIdentifyDeviceClicked(): void {
    this.store.dispatch(startIdentifyingDevice());
  }
}
