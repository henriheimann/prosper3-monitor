package org.urbanenvironmentmonitor.device.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.urbanenvironmentmonitor.device.entities.DeviceEntity;

public interface DeviceRepository extends ReactiveCrudRepository<DeviceEntity, Long>
{
}
