export enum MeasurementTypeModel {
  TEMPERATURE = 'Temperatur',
  HUMIDITY = 'Luftfeuchtigkeit',
  BRIGHTNESS_CURRENT = 'Helligkeit'
}

export function getMinimumValueForMeasurementType(type: MeasurementTypeModel): number {
  switch (type) {
    default:
    case MeasurementTypeModel.TEMPERATURE:
      return -20;
    case MeasurementTypeModel.HUMIDITY:
      return 0;
    case MeasurementTypeModel.BRIGHTNESS_CURRENT:
      return 0;
  }
}

export function getMaximumValueForMeasurementType(type: MeasurementTypeModel): number {
  switch (type) {
    default:
    case MeasurementTypeModel.TEMPERATURE:
      return 80;
    case MeasurementTypeModel.HUMIDITY:
      return 100;
    case MeasurementTypeModel.BRIGHTNESS_CURRENT:
      return 10000;
  }
}

export function getUnitForMeasurementType(type: MeasurementTypeModel): string {
  switch (type) {
    default:
    case MeasurementTypeModel.TEMPERATURE:
      return '°C';
    case MeasurementTypeModel.HUMIDITY:
      return '%RH';
    case MeasurementTypeModel.BRIGHTNESS_CURRENT:
      return '';
  }
}
