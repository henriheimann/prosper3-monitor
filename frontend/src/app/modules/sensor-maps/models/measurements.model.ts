import { DeviceValuesModel } from '../../shared/models/device-values.model';

export interface MeasurementsModel {
  timestamp: string;
  deviceValues: {
    [device_id: number]: DeviceValuesModel;
  };
}
