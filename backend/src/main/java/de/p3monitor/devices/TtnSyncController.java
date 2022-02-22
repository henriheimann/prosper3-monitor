package de.p3monitor.devices;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/ttn-sync")
public class TtnSyncController
{
	private final TtnSyncService ttnSyncService;

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/devices")
	public Mono<Void> performDevicesTtnSync()
	{
		return ttnSyncService.syncDevices();
	}
}
