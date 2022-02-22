import { Component, Input, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectSelectedDeviceValuesForIndex, selectSelectedDevice } from '../../store/visualisation.selectors';
import { selectDevice, startDraggingSelectedDevice } from '../../store/visualisation.actions';
import { Observable } from 'rxjs';
import { DeviceModel } from '../../../shared/models/device.model';
import { DeviceValuesModel } from '../../../shared/models/device-values.model';
import { selectLoggedInRole, selectLoggedInUsername } from '../../../auth/store/auth.selectors';
import { EditDeviceModalComponent } from '../../../admin/components/edit-device-modal/edit-device-modal.component';
import { BsModalService } from 'ngx-bootstrap/modal';

@Component({
  selector: 'p3m-sensor-detail',
  templateUrl: './sensor-detail.component.html',
  styleUrls: ['./sensor-detail.component.sass']
})
export class SensorDetailComponent implements OnInit {
  selectedDevice$: Observable<DeviceModel | null> = this.store.select(selectSelectedDevice);
  selectedDevice: DeviceModel | null = null;

  userRole$: Observable<string | undefined> = this.store.select(selectLoggedInRole);

  measurements$: Observable<{ timestamp: string; deviceValues: DeviceValuesModel } | null> = this.store.select(
    selectSelectedDeviceValuesForIndex
  );

  @Input() useDeviceLastContact = false;

  constructor(private store: Store, private modalService: BsModalService) {}

  ngOnInit(): void {
    this.selectedDevice$.subscribe((selectDevice) => (this.selectedDevice = selectDevice));
  }

  onCloseClicked(): void {
    this.store.dispatch(
      selectDevice({
        device: null
      })
    );
  }

  onMoveClicked(): void {
    this.store.dispatch(startDraggingSelectedDevice());
  }

  onEditClicked(): void {
    if (this.selectedDevice) {
      this.modalService.show(EditDeviceModalComponent, {
        class: 'modal-dialog-centered',
        initialState: {
          device: this.selectedDevice
        }
      });
    }
  }

  formatDate(timestamp: string): string {
    return new Date(timestamp).toLocaleString([], {
      year: 'numeric',
      month: 'numeric',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
