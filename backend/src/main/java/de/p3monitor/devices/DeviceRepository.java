package de.p3monitor.devices;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DeviceRepository extends ReactiveCrudRepository<DeviceEntity, Long>
{
}
