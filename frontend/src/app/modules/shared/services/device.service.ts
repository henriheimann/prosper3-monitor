import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { DeviceModel } from '../models/device.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {
  devices$ = new BehaviorSubject<DeviceModel[] | null>(null);

  constructor(private httpClient: HttpClient) {}

  getById(id: number): Observable<DeviceModel> {
    return this.httpClient.get<DeviceModel>(`${environment.backendUrl}/devices/${id}`);
  }

  getAll(): BehaviorSubject<DeviceModel[] | null> {
    this.httpClient
      .get<DeviceModel[]>(`${environment.backendUrl}/devices`)
      .subscribe((deviceModels) => this.devices$.next(deviceModels));
    return this.devices$;
  }

  deleteById(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${environment.backendUrl}/devices/${id}`).pipe(tap(() => this.getAll()));
  }

  editDevice(id: number, deviceModel: Partial<DeviceModel>) {
    return this.httpClient
      .put<DeviceModel>(`${environment.backendUrl}/devices/${id}`, deviceModel)
      .pipe(tap(() => this.getAll()));
  }

  createDevice(deviceModel: Partial<DeviceModel>): Observable<DeviceModel> {
    return this.httpClient
      .post<DeviceModel>(`${environment.backendUrl}/devices`, deviceModel)
      .pipe(tap(() => this.getAll()));
  }

  performDevicesTtnSync(): Observable<void> {
    return this.httpClient
      .post<void>(`${environment.backendUrl}/ttn-sync/devices`, null)
      .pipe(tap(() => this.getAll()));
  }
}
