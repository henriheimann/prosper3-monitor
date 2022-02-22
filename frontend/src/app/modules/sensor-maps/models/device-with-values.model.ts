import { DeviceModel } from '../../shared/models/device.model';
import { DeviceValuesModel } from '../../shared/models/device-values.model';

export interface DeviceWithValuesModel {
  device: DeviceModel;
  values?: DeviceValuesModel;
}
