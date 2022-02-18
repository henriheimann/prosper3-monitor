import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, switchMap, withLatestFrom } from 'rxjs/operators';
import * as VisualisationActions from './visualisation.actions';
import { selectMeasurementsFailure, selectMeasurementsSuccess } from './visualisation.actions';
import { selectVisualisationState } from './visualisation.selectors';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import { MeasurementsService } from '../services/measurements.service';

@Injectable()
export class VisualisationEffects {
  loadMeasurements$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(VisualisationActions.selectMeasurements),
      withLatestFrom(this.store.select(selectVisualisationState)),
      switchMap(([action, store]) => {
        if (action.measurementTimespan != store.selectedTimespan || store.measurements == null) {
          return this.measurementsService.getMeasurements(action.measurementTimespan).pipe(
            map((measurements) => {
              return selectMeasurementsSuccess({
                measurementTimespan: action.measurementTimespan,
                measurementType: action.measurementType,
                measurements
              });
            }),
            catchError((error) => of(selectMeasurementsFailure({ error })))
          );
        } else {
          return of(
            selectMeasurementsSuccess({
              measurementTimespan: action.measurementTimespan,
              measurementType: action.measurementType,
              measurements: store.measurements
            })
          );
        }
      })
    );
  });

  constructor(private actions$: Actions, private store: Store, private measurementsService: MeasurementsService) {}
}
