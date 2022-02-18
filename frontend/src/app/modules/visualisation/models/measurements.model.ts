import { SensorValuesModel } from '../../shared/models/sensor-values.model';

export interface MeasurementsModel {
  timestamp: string;
  sensorValues: {
    [device_id: number]: SensorValuesModel;
  };
}
