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

@Injectable({
  providedIn: 'root'
})
export class MeasurementsService {
  constructor(private httpClient: HttpClient) {}

  getMeasurements(measurementTimespan: MeasurementTimespanModel): Observable<MeasurementsModel[]> {
    return this.httpClient.post<MeasurementsModel[]>(`${environment.backendUrl}/measurements`, {
      deviceSensorType: null,
      start: getStartForMeasurementTimespan(measurementTimespan),
      stop: getStopForMeasurementTimespan(measurementTimespan),
      aggregateWindowSeconds: getAggregateWindowSecondsForForMeasurementTimespan(measurementTimespan)
    });
  }
}
