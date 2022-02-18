import { SensorValuesModel } from './sensor-values.model';

export interface LastContactModel {
  timestamp: string;
  sensorType: string;
  sensorValues: SensorValuesModel;
}
