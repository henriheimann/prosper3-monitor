import { Component, Input, OnInit } from '@angular/core';
import { MeasurementTypeModel } from '../../../sensor-maps/models/measurement-type.model';
import { map, share } from 'rxjs/operators';
import { MeasurementTimespanModel } from '../../../sensor-maps/models/measurement-timespan.model';
import { combineLatest, Observable, of } from 'rxjs';
import { DeviceWithValuesModel } from '../../../sensor-maps/models/device-with-values.model';
import { DeviceService } from '../../../shared/services/device.service';
import { MeasurementsService } from '../../../sensor-maps/services/measurements.service';
import { WeatherService } from '../../../sensor-maps/services/weather.service';
import { AveragedMeasurementsModel } from '../../../sensor-maps/models/averaged-measurements.model';
import { AveragedWeatherModel } from '../../../sensor-maps/models/averaged-weather.model';

@Component({
  selector: 'p3m-climate-widget-tab',
  templateUrl: './climate-widget-tab.component.html',
  styleUrls: ['./climate-widget-tab.component.sass']
})
export class ClimateWidgetTabComponent {
  measurementType = MeasurementTypeModel;

  devices$ = this.deviceService.getAll().pipe(
    map((devices) => {
      if (devices != null) {
        return devices.filter((device) => device.lastContact?.sensorType == 'CLIMATE_SENSOR');
      } else {
        return devices;
      }
    })
  );

  @Input()
  set timespan(timespan: MeasurementTimespanModel | null | undefined) {
    if (timespan) {
      this.updateObservablesForTimespan(timespan);
    }
  }

  averagedMeasurements$: Observable<AveragedMeasurementsModel | null> = of(null);
  averagedWeather$: Observable<AveragedWeatherModel | null> = of(null);
  devicesWithValues$: Observable<DeviceWithValuesModel[] | null> = of(null);

  averagedMeasuredTemperature: number | null = null;
  averagedWeatherTemperature: number | null = null;

  averagedMeasuredHumidity: number | null = null;
  averagedWeatherHumidity: number | null = null;

  constructor(
    private deviceService: DeviceService,
    private measurementsService: MeasurementsService,
    private weatherService: WeatherService
  ) {}

  private updateObservablesForTimespan(timespan: MeasurementTimespanModel): void {
    this.averagedMeasuredTemperature = null;
    this.averagedWeatherTemperature = null;
    this.averagedMeasuredHumidity = null;
    this.averagedWeatherHumidity = null;
    this.averagedMeasurements$ = this.measurementsService.getAveragedMeasurements(timespan).pipe(share());
    this.averagedWeather$ = this.weatherService.getAveragedWeather('Bottrop', timespan).pipe(share());
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
      if (averagedMeasurements?.deviceValues?.tmp != null) {
        this.averagedMeasuredTemperature = averagedMeasurements.deviceValues.tmp;
      }
      if (averagedMeasurements?.deviceValues?.hum != null) {
        this.averagedMeasuredHumidity = averagedMeasurements.deviceValues.hum;
      }
    });
    this.averagedWeather$.subscribe((averagedWeather) => {
      if (averagedWeather?.weatherValues?.tmp != null) {
        this.averagedWeatherTemperature = averagedWeather.weatherValues.tmp;
      }
      if (averagedWeather?.weatherValues?.hum != null) {
        this.averagedWeatherHumidity = averagedWeather.weatherValues.hum;
      }
    });
  }
}
