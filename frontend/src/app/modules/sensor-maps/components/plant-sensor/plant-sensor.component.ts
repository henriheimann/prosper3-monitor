import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectDevice } from '../../store/visualisation.actions';
import { selectSelectedDevice } from '../../store/visualisation.selectors';
import { map } from 'rxjs/operators';
import { DeviceWithValuesModel } from '../../models/device-with-values.model';

@Component({
  selector: 'p3m-plant-sensor',
  templateUrl: './plant-sensor.component.html',
  styleUrls: ['./plant-sensor.component.sass']
})
export class PlantSensorComponent {
  selectable = false;
  deviceWithValues: DeviceWithValuesModel | null = null;

  selected$ = this.store.select(selectSelectedDevice).pipe(
    map((selectedDevice) => {
      return selectedDevice && selectedDevice === this.deviceWithValues?.device;
    })
  );

  constructor(private store: Store) {}

  matchesIcon(type: string): boolean {
    const temperature = this.deviceWithValues?.device?.lastContact?.deviceValues?.tmp;
    const moisture = this.deviceWithValues?.device?.lastContact?.deviceValues?.mst;

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
    if (this.selectable && this.deviceWithValues != null) {
      this.store.dispatch(
        selectDevice({
          device: this.deviceWithValues.device
        })
      );
    }
  }
}
