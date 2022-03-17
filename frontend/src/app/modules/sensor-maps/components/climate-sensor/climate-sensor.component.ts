import { Component, Input, OnInit } from '@angular/core';
import { selectDevice } from '../../store/visualisation.actions';
import { DeviceModel } from '../../../shared/models/device.model';
import {
  selectSelectedDeviceValuesForIndex,
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
import { DeviceValuesModel } from '../../../shared/models/device-values.model';
import { DeviceWithValuesModel } from '../../models/device-with-values.model';

@Component({
  selector: 'p3m-climate-sensor',
  templateUrl: './climate-sensor.component.html',
  styleUrls: ['./climate-sensor.component.sass']
})
export class ClimateSensorComponent {
  selectable = false;
  deviceWithValues: DeviceWithValuesModel | null = null;

  selected$ = this.store.select(selectSelectedDevice).pipe(
    map((selectedDevice) => {
      return selectedDevice && selectedDevice === this.deviceWithValues?.device;
    })
  );

  measurements$: Observable<{ timestamp: string; deviceValues: DeviceValuesModel } | null> = this.store.select(
    selectSelectedDeviceValuesForIndex
  );
  measurementsType$: Observable<MeasurementTypeModel> = this.store.select(selectSelectedMeasurementType);

  constructor(private store: Store) {}

  onSelected(): void {
    if (this.selectable && this.deviceWithValues != null) {
      this.store.dispatch(
        selectDevice({
          device: this.deviceWithValues.device
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
              value = measurements?.deviceValues?.tmp;
              break;
            case MeasurementTypeModel.BRIGHTNESS_CURRENT:
              if (measurements?.deviceValues?.bgh != undefined) {
                value = measurements.deviceValues.bgh * 100;
              }
              break;
            case MeasurementTypeModel.HUMIDITY:
              value = measurements?.deviceValues?.hum;
              break;
          }

          let min = getMinimumValueForMeasurementType(measurementsType);
          let max = getMaximumValueForMeasurementType(measurementsType);
          let diff = max - min;

          if (value != undefined) {
            switch (type) {
              case '0-percent':
                return value >= min && value < min + diff * 0.125;
              case '25-percent':
                return value >= min + diff * 0.125 && value <= min + diff * 0.375;
              case '50-percent':
                return value >= min + diff * 0.375 && value <= min + diff * 0.625;
              case '75-percent':
                return value >= min + diff * 0.625 && value <= min + diff * 0.875;
              case '100-percent':
                return value >= min + diff * 0.875;
              case 'no-data':
                return false;
            }
          }
        }
        return type == 'no-data';
      })
    );
  }
}
