package de.p3monitor.device;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/ttn-sync")
public class TtnSyncController
{
	private final TtnSyncService ttnSyncService;

	@PostMapping("/devices")
	public Mono<Void> performDevicesTtnSync()
	{
		return ttnSyncService.syncDevices();
	}
}
