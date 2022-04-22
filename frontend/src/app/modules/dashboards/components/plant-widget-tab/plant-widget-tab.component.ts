import { Component, Input } from '@angular/core';
import { map, share } from 'rxjs/operators';
import { DeviceService } from '../../../shared/services/device.service';
import { MeasurementTimespanModel } from '../../../sensor-maps/models/measurement-timespan.model';
import { combineLatest, Observable, of } from 'rxjs';
import { AveragedMeasurementsModel } from '../../../sensor-maps/models/averaged-measurements.model';
import { DeviceWithValuesModel } from '../../../sensor-maps/models/device-with-values.model';
import { MeasurementsService } from '../../../sensor-maps/services/measurements.service';

@Component({
  selector: 'p3m-plant-widget-tab',
  templateUrl: './plant-widget-tab.component.html',
  styleUrls: ['./plant-widget-tab.component.sass']
})
export class PlantWidgetTabComponent {
  devices$ = this.deviceService.getAll().pipe(
    map((devices) => {
      if (devices != null) {
        return devices.filter((device) => device.lastContact?.sensorType == 'PLANT_SENSOR' &&
          (new Date().getTime() - new Date(device.lastContact.timestamp).getTime() < 60 * 60 * 1000));
      } else {
        return devices;
      }
    })
  );
  averagedMeasurements$: Observable<AveragedMeasurementsModel | null> = of(null);
  devicesWithValues$: Observable<DeviceWithValuesModel[] | null> = of(null);
  averagedPlantWaterPercentage: number | null = null;

  constructor(private deviceService: DeviceService, private measurementsService: MeasurementsService) {}

  @Input()
  set timespan(timespan: MeasurementTimespanModel | null | undefined) {
    if (timespan) {
      this.updateObservablesForTimespan(timespan);
    }
  }

  private updateObservablesForTimespan(timespan: MeasurementTimespanModel): void {
    this.averagedPlantWaterPercentage = null;
    this.averagedMeasurements$ = this.measurementsService
      .getAveragedMeasurements(timespan, 'PLANT_SENSOR')
      .pipe(share());
    this.devicesWithValues$ = combineLatest([this.devices$, this.averagedMeasurements$]).pipe(
      map(([devices, measurements]) => {
        if (!devices || !measurements) {
          return null;
        } else {
          return devices.map(
            (device) =>
              ({
                device,
                values: measurements.deviceValues
              } as DeviceWithValuesModel)
          );
        }
      })
    );
    this.averagedMeasurements$.subscribe((averagedMeasurements) => {
      if (averagedMeasurements?.deviceValues?.mst != null) {
        this.averagedPlantWaterPercentage = averagedMeasurements.deviceValues.mst;
      }
    });
  }
}
