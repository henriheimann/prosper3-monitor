import { Component, OnInit } from '@angular/core';
import { DeviceModel } from '../../../shared/models/device.model';
import { selectSelectedDevice } from '../../store/visualisation.selectors';
import { map } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { selectDevice } from '../../store/visualisation.actions';
import { DeviceWithValuesModel } from '../../models/device-with-values.model';

@Component({
  selector: 'p3m-unknown-sensor',
  templateUrl: './unknown-sensor.component.html',
  styleUrls: ['./unknown-sensor.component.sass']
})
export class UnknownSensorComponent {
  selectable = false;
  deviceWithValues: DeviceWithValuesModel | null = null;

  selected$ = this.store.select(selectSelectedDevice).pipe(
    map((selectedDevice) => {
      return selectedDevice && selectedDevice === this.deviceWithValues?.device;
    })
  );

  constructor(private store: Store) {}

  onSelected(): void {
    if (this.deviceWithValues != null) {
      this.store.dispatch(
        selectDevice({
          device: this.deviceWithValues.device
        })
      );
    }
  }
}
