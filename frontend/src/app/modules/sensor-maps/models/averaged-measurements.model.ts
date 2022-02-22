import { DeviceValuesModel } from '../../shared/models/device-values.model';

export interface AveragedMeasurementsModel {
  start: string;
  stop: string;
  deviceValues: DeviceValuesModel;
}
