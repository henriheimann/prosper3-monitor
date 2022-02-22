import { MeasurementTypeModel } from '../../sensor-maps/models/measurement-type.model';

export interface DeviceValuesModel {
  bat?: number;
  mst?: number;
  tmp?: number;
  hum?: number;
  irt?: number;
  bgh?: number;
}

export function extractValueOfType(deviceValues: DeviceValuesModel, measurementType: MeasurementTypeModel) {
  switch (measurementType) {
    case MeasurementTypeModel.TEMPERATURE:
      return deviceValues.tmp;
    case MeasurementTypeModel.HUMIDITY:
      return deviceValues.hum;
    case MeasurementTypeModel.BRIGHTNESS_CURRENT:
      return deviceValues.bgh;
  }
}
