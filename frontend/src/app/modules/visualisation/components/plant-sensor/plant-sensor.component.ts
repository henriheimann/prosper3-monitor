import { Component } from '@angular/core';
import { DeviceModel } from '../../../shared/models/device.model';
import { Store } from '@ngrx/store';
import { selectDevice } from '../../store/visualisation.actions';
import {
  selectAllMeasurementsAtSelectedIndexForSelectedDevice,
  selectSelectedDevice,
  selectSelectedMeasurementsAtSelectedIndex
} from '../../store/visualisation.selectors';
import { map } from 'rxjs/operators';

@Component({
  selector: 'p3m-plant-sensor',
  templateUrl: './plant-sensor.component.html',
  styleUrls: ['./plant-sensor.component.sass']
})
export class PlantSensorComponent {
  device: DeviceModel | null = null;

  selected$ = this.store.select(selectSelectedDevice).pipe(
    map((selectedDevice) => {
      return selectedDevice && selectedDevice === this.device;
    })
  );

  measurements$ = this.store.select(selectAllMeasurementsAtSelectedIndexForSelectedDevice);

  constructor(private store: Store) {}

  matchesIcon(type: string): boolean {
    const temperature = this.device?.lastContact?.sensorValues?.tmp;
    const moisture = this.device?.lastContact?.sensorValues?.mst;

    if (temperature === undefined || moisture === undefined) {
      return false;
    } else {
      switch (type) {
        case '0-percent':
          return temperature >= 0 && moisture < 0.2;
        case '25-percent':
          return temperature >= 0 && moisture < 0.4 && moisture >= 0.2;
        case '50-percent':
          return temperature >= 0 && moisture < 0.6 && moisture >= 0.4;
        case '75-percent':
          return temperature >= 0 && moisture < 0.8 && moisture >= 0.6;
        case '100-percent':
          return temperature >= 0 && moisture >= 0.8;
        case 'frost':
          return temperature < 0;
        default:
          return false;
      }
    }
  }

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
