package de.p3monitor.device;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DeviceRepository extends ReactiveCrudRepository<DeviceEntity, Long>
{
}
