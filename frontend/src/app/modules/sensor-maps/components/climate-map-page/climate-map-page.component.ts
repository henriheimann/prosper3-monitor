import { Component, OnInit } from '@angular/core';
import { DeviceService } from '../../../shared/services/device.service';
import { map } from 'rxjs/operators';
import {
  selectDraggingDevice,
  selectIsIdentifyingDevice,
  selectAllDeviceValuesForIndex,
  selectSelectedMeasurementType
} from '../../store/visualisation.selectors';
import { Store } from '@ngrx/store';
import { selectDevice } from '../../store/visualisation.actions';
import { combineLatest, Observable, zip } from 'rxjs';
import { DeviceWithValuesModel } from '../../models/device-with-values.model';

@Component({
  selector: 'p3m-climate-map-page',
  templateUrl: './climate-map-page.component.html',
  styleUrls: ['./climate-map-page.component.sass']
})
export class ClimateMapPageComponent implements OnInit {
  devices$ = this.deviceService.getAll().pipe(
    map((devices) => {
      if (devices != null) {
        return devices.filter((device) => device.lastContact?.sensorType == 'CLIMATE_SENSOR');
      } else {
        return devices;
      }
    })
  );

  devicesWithValues$: Observable<DeviceWithValuesModel[] | null> = combineLatest([
    this.devices$,
    this.store.select(selectAllDeviceValuesForIndex)
  ]).pipe(
    map(([devices, measurements]) => {
      if (!devices) {
        return null;
      } else {
        return devices.map(
          (device) =>
            ({
              device,
              values: measurements?.deviceValues[device.id]
            } as DeviceWithValuesModel)
        );
      }
    })
  );

  isDraggingDevice$ = this.store.select(selectDraggingDevice).pipe(map((draggingDevice) => draggingDevice != null));
  isIdentifyingDevice$ = this.store.select(selectIsIdentifyingDevice);
  selectedMeasurementType$ = this.store.select(selectSelectedMeasurementType);

  constructor(private deviceService: DeviceService, private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      selectDevice({
        device: null
      })
    );
  }
}
