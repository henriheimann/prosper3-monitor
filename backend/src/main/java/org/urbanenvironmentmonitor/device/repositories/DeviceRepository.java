package org.urbanenvironmentmonitor.device.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.urbanenvironmentmonitor.device.entities.Device;

public interface DeviceRepository extends ReactiveCrudRepository<Device, Long>
{
}
