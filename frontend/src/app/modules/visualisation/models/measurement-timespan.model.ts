export enum MeasurementTimespanModel {
  LAST_DAY = 'Letzter Tag',
  LAST_WEEK = 'Letzte Woche',
  LAST_MONTH = 'Letzter Monat',
  LAST_YEAR = 'Letztes Jahr'
}

export function getStartForMeasurementTimespan(measurementTimespan: MeasurementTimespanModel): Date {
  let currentDate = new Date();

  switch (measurementTimespan) {
    default:
    case MeasurementTimespanModel.LAST_DAY:
      currentDate = new Date(currentDate.setDate(currentDate.getDate() - 1));
      break;
    case MeasurementTimespanModel.LAST_WEEK:
      currentDate = new Date(currentDate.setDate(currentDate.getDate() - 7));
      break;
    case MeasurementTimespanModel.LAST_MONTH:
      currentDate = new Date(currentDate.setMonth(currentDate.getMonth() - 1));
      break;
    case MeasurementTimespanModel.LAST_YEAR:
      currentDate = new Date(currentDate.setFullYear(currentDate.getFullYear() - 1));
      break;
  }

  return currentDate;
}

export function getStopForMeasurementTimespan(measurementTimespan: MeasurementTimespanModel): Date {
  return new Date();
}

export function getAggregateWindowSecondsForForMeasurementTimespan(
  measurementTimespan: MeasurementTimespanModel
): number {
  switch (measurementTimespan) {
    default:
    case MeasurementTimespanModel.LAST_DAY:
      return 60 * 10; // Every 10 minutes
    case MeasurementTimespanModel.LAST_WEEK:
      return 60 * 60; // Every 1 hour
    case MeasurementTimespanModel.LAST_MONTH:
      return 60 * 60 * 4; // Every 4 hours
    case MeasurementTimespanModel.LAST_YEAR:
      return 60 * 60 * 24; // Every 1 day
  }
}
