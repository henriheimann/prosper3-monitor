import { Component } from '@angular/core';
import { DeviceModel } from '../../../shared/models/device.model';
import { Store } from '@ngrx/store';
import { selectDevice } from '../../store/visualisation.actions';
import { selectSelectedDevice } from '../../store/visualisation.selectors';
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

  constructor(private store: Store) {}

  matchesIcon(type: string): boolean {
    const temperature = this.device?.lastContact?.temperature;
    const moistureCounter = this.device?.lastContact?.moistureCounter;

    if (temperature === undefined || moistureCounter === undefined) {
      return false;
    } else {
      switch (type) {
        case '0-percent':
          return temperature >= 0 && moistureCounter < 4000;
        case '25-percent':
          return temperature >= 0 && moistureCounter < 5000 && moistureCounter >= 4000;
        case '50-percent':
          return temperature >= 0 && moistureCounter < 6000 && moistureCounter >= 5000;
        case '75-percent':
          return temperature >= 0 && moistureCounter < 7000 && moistureCounter >= 6000;
        case '100-percent':
          return temperature >= 0 && moistureCounter >= 7000;
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
