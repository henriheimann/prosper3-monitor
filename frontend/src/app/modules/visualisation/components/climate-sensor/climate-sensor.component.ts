import { Component, OnInit } from '@angular/core';
import { selectDevice } from '../../store/visualisation.actions';
import { DeviceModel } from '../../../shared/models/device.model';
import {
  selectAllMeasurementsAtSelectedIndexForSelectedDevice,
  selectSelectedDevice,
  selectSelectedMeasurementType
} from '../../store/visualisation.selectors';
import { map } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { Observable, zip } from 'rxjs';
import {
  getMaximumValueForMeasurementType,
  getMinimumValueForMeasurementType,
  MeasurementTypeModel
} from '../../models/measurement-type.model';
import { SensorValuesModel } from '../../../shared/models/sensor-values.model';

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

  measurements$: Observable<{ timestamp: string; sensorValues: SensorValuesModel } | null> = this.store.select(
    selectAllMeasurementsAtSelectedIndexForSelectedDevice
  );
  measurementsType$: Observable<MeasurementTypeModel> = this.store.select(selectSelectedMeasurementType);

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

  matchesIcon(type: string): Observable<boolean> {
    return zip(this.measurements$, this.measurementsType$).pipe(
      map(([measurements, measurementsType]) => {
        if (measurements && measurementsType) {
          let value: number | undefined;
          switch (measurementsType) {
            default:
            case MeasurementTypeModel.TEMPERATURE:
              value = measurements?.sensorValues?.tmp;
              break;
            case MeasurementTypeModel.BRIGHTNESS_CURRENT:
              value = measurements?.sensorValues?.bgh;
              break;
            case MeasurementTypeModel.HUMIDITY:
              value = measurements?.sensorValues?.hum;
              break;
          }

          let min = getMinimumValueForMeasurementType(measurementsType);
          let max = getMaximumValueForMeasurementType(measurementsType);
          let diff = max - min;

          if (value) {
            switch (type) {
              case '0-percent':
                return value >= min && value < min + diff * 0.2;
              case '25-percent':
                return value >= min + diff * 0.2 && value <= min + diff * 0.4;
              case '50-percent':
                return value >= min + diff * 0.4 && value <= min + diff * 0.6;
              case '75-percent':
                return value >= min + diff * 0.6 && value <= min + diff * 0.8;
              case '100-percent':
                return value >= min + diff * 0.8;
            }
          }
        }

        return false;
      })
    );
  }
}
