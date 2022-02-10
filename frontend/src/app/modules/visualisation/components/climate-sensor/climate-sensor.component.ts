import { Component, OnInit } from '@angular/core';
import { selectDevice } from '../../store/visualisation.actions';
import { DeviceModel } from '../../../shared/models/device.model';
import { selectSelectedDevice } from '../../store/visualisation.selectors';
import { map } from 'rxjs/operators';
import { Store } from '@ngrx/store';

@Component({
  selector: 'p3m-climate-sensor',
  templateUrl: './climate-sensor.component.html',
  styleUrls: ['./climate-sensor.component.sass']
})
export class ClimateSensorComponent {
  device: DeviceModel | null = null;
  selected$ = this.store.select(selectSelectedDevice).pipe(
    map((selectedDevice) => {
      return selectedDevice && selectedDevice === this.device;
    })
  );

  constructor(private store: Store) {}

  onSelected(): void {
    if (this.device != null) {
      this.store.dispatch(
        selectDevice({
          device: this.device
        })
      );
    }
  }
}
