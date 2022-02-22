import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import * as VisualisationActions from './visualisation.actions';
import { selectDevice, selectMeasurementsFailure, selectMeasurementsSuccess } from './visualisation.actions';
import { selectVisualisationState } from './visualisation.selectors';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import { MeasurementsService } from '../services/measurements.service';
import { Router } from '@angular/router';

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

  /*selectDeviceUpdateRoute$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(VisualisationActions.selectDevice),
        withLatestFrom(this.store.select(selectVisualisationState)),
        tap(([action, store]) => {
          if (action.device) {
            switch (action.device?.lastContact?.sensorType) {
              default:
                break;
              case 'PLANT_SENSOR':
                this.router.navigate(['plants']).then();
                break;
              case 'CLIMATE_SENSOR':
                this.router.navigate(['climate']).then();
                break;
            }
          }
        })
      );
    },
    { dispatch: false }
  );*/

  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private measurementsService: MeasurementsService
  ) {}
}
