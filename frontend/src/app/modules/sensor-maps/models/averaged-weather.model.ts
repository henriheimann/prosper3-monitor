import { WeatherValuesModel } from './weather-values.model';

export interface AveragedWeatherModel {
  start: string;
  stop: string;
  weatherValues: WeatherValuesModel;
}
