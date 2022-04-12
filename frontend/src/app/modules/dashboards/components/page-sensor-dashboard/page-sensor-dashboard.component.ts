import { Component } from '@angular/core';
import { map } from 'rxjs/operators';
import { Observable, of, zip } from 'rxjs';
import { DeviceWithValuesModel } from '../../../sensor-maps/models/device-with-values.model';
import { DeviceService } from '../../../shared/services/device.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MeasurementsService } from '../../../sensor-maps/services/measurements.service';
import { MeasurementTimespanModel } from '../../../sensor-maps/models/measurement-timespan.model';
import { WeatherService } from '../../../sensor-maps/services/weather.service';

@Component({
  selector: 'p3m-page-sensor-dashboard',
  templateUrl: './page-sensor-dashboard.component.html',
  styleUrls: ['./page-sensor-dashboard.component.sass']
})
export class PageSensorDashboardComponent {
  allDevices$ = this.deviceService.getAll();

  accessedDevice$ = this.allDevices$.pipe(
    map((devices) => {
      if (devices == null) {
        return null;
      } else {
        return devices.find((device) => device.qrCodeId === parseInt(this.route.snapshot.params['qrCodeId']));
      }
    })
  );

  plantSensorDevices$ = this.allDevices$.pipe(
    map((devices) => {
      if (devices != null) {
        return devices.filter((device) => device.lastContact?.sensorType == 'PLANT_SENSOR');
      } else {
        return devices;
      }
    })
  );

  numberOfPlantsRequiringIrrigation$ = this.plantSensorDevices$.pipe(
    map((devices) => {
      return devices?.filter((device) => {
        if (device?.lastContact?.deviceValues?.mst == undefined) {
          return false;
        } else {
          return device.lastContact.deviceValues.mst < 0.2;
        }
      }).length;
    })
  );

  numberOfPlantsRequiringIrrigation: number | undefined = undefined;

  climateSensorDevices$ = this.allDevices$.pipe(
    map((devices) => {
      if (devices != null) {
        return devices.filter((device) => device.lastContact?.sensorType == 'CLIMATE_SENSOR');
      } else {
        return devices;
      }
    })
  );

  plantSensorDeviceWithValues$: Observable<DeviceWithValuesModel[] | null> = this.plantSensorDevices$.pipe(
    map((devices) => {
      if (!devices) {
        return null;
      } else {
        return devices?.map(
          (device) =>
            ({
              device,
              values: device.lastContact?.deviceValues
            } as DeviceWithValuesModel)
        );
      }
    })
  );

  climateSensorDevicesWithValues$: Observable<DeviceWithValuesModel[] | null> = this.climateSensorDevices$.pipe(
    map((devices) => {
      if (!devices) {
        return null;
      } else {
        return devices?.map(
          (device) =>
            ({
              device,
              values: device.lastContact?.deviceValues
            } as DeviceWithValuesModel)
        );
      }
    })
  );

  averagedMeasuredTemperature: number | null = null;
  averagedWeatherTemperature: number | null = null;

  averagedMeasurements$ = this.measurementsService.getAveragedMeasurements(
    MeasurementTimespanModel.LAST_DAY,
    'CLIMATE_SENSOR'
  );

  averagedWeather$ = this.weatherService.getAveragedWeather('Bottrop', MeasurementTimespanModel.LAST_DAY);

  constructor(
    private deviceService: DeviceService,
    private measurementsService: MeasurementsService,
    private weatherService: WeatherService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.accessedDevice$.subscribe((device) => {
      if (device !== null) {
        if (device?.lastContact?.sensorType == undefined) {
          this.router.navigate(['/']).then();
        }
      }
    });
    this.numberOfPlantsRequiringIrrigation$.subscribe((number) => {
      this.numberOfPlantsRequiringIrrigation = number;
    });
    this.averagedMeasurements$.subscribe((averagedMeasurements) => {
      if (averagedMeasurements?.deviceValues?.tmp != null) {
        this.averagedMeasuredTemperature = averagedMeasurements.deviceValues.tmp;
      }
    });
    this.averagedWeather$.subscribe((averagedWeather) => {
      if (averagedWeather?.weatherValues?.tmp != null) {
        this.averagedWeatherTemperature = averagedWeather.weatherValues.tmp;
      }
    });
  }

  isPlantSensor(): Observable<boolean> {
    return this.accessedDevice$.pipe(
      map((device) => {
        return device?.lastContact?.sensorType == 'PLANT_SENSOR';
      })
    );
  }
}
