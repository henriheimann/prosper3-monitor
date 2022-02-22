import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  getAggregateWindowSecondsForForMeasurementTimespan,
  getStartForMeasurementTimespan,
  getStopForMeasurementTimespan,
  MeasurementTimespanModel
} from '../models/measurement-timespan.model';
import { MeasurementsModel } from '../models/measurements.model';
import { AveragedMeasurementsModel } from '../models/averaged-measurements.model';

@Injectable({
  providedIn: 'root'
})
export class MeasurementsService {
  constructor(private httpClient: HttpClient) {}

  getMeasurements(measurementTimespan: MeasurementTimespanModel): Observable<MeasurementsModel[]> {
    return this.httpClient.post<MeasurementsModel[]>(`${environment.backendUrl}/measurements`, {
      deviceSensorType: 'CLIMATE_SENSOR',
      start: getStartForMeasurementTimespan(measurementTimespan),
      stop: getStopForMeasurementTimespan(measurementTimespan),
      aggregateWindowSeconds: getAggregateWindowSecondsForForMeasurementTimespan(measurementTimespan)
    });
  }

  getAveragedMeasurements(measurementTimespan: MeasurementTimespanModel): Observable<AveragedMeasurementsModel> {
    return this.httpClient.post<AveragedMeasurementsModel>(`${environment.backendUrl}/measurements/averaged`, {
      deviceSensorType: 'CLIMATE_SENSOR',
      start: getStartForMeasurementTimespan(measurementTimespan),
      stop: getStopForMeasurementTimespan(measurementTimespan)
    });
  }
}
