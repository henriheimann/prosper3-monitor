import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  getStartForMeasurementTimespan,
  getStopForMeasurementTimespan,
  MeasurementTimespanModel
} from '../models/measurement-timespan.model';
import { WeatherValuesModel } from '../models/weather-values.model';
import { AveragedWeatherModel } from '../models/averaged-weather.model';

@Injectable({
  providedIn: 'root'
})
export class WeatherService {
  constructor(private httpClient: HttpClient) {}

  getCurrentWeather(city: string): Observable<WeatherValuesModel> {
    return this.httpClient.post<WeatherValuesModel>(`${environment.backendUrl}/weather`, {
      city
    });
  }

  getAveragedWeather(city: string, measurementTimespan: MeasurementTimespanModel): Observable<AveragedWeatherModel> {
    return this.httpClient.post<AveragedWeatherModel>(`${environment.backendUrl}/weather/averaged`, {
      start: getStartForMeasurementTimespan(measurementTimespan),
      stop: getStopForMeasurementTimespan(measurementTimespan),
      city
    });
  }
}
