package de.p3monitor.device;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/devices-ttn-sync")
public class DevicesTtnSyncController
{
	private final DeviceService deviceService;

	@PostMapping("")
	public Mono<Void> performDevicesTtnSync()
	{
		return deviceService.syncDevicesWithTtn()
				.then();
	}
}
