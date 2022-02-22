import { DeviceValuesModel } from './device-values.model';

export interface LastContactModel {
  timestamp: string;
  sensorType: string;
  deviceValues: DeviceValuesModel;
}
