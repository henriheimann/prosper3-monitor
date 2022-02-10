package de.p3monitor.device;

import de.p3monitor.device.dtos.CreateDeviceRequest;
import de.p3monitor.device.dtos.DeviceResponse;
import de.p3monitor.influxdb.dtos.DeviceSensorType;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import de.p3monitor.device.dtos.UpdateDeviceRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/devices")
@AllArgsConstructor
public class DeviceController
{
	private final DeviceService deviceService;
	private final LiveMeasurementService measurementService;

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("")
	public Mono<DeviceResponse> createDevice(@Valid @RequestBody CreateDeviceRequest createDeviceRequest)
	{
		return deviceService.createDevice(createDeviceRequest);
	}

	@GetMapping("")
	public Flux<DeviceResponse> getDevices(@RequestParam(required = false) DeviceSensorType sensorType)
	{
		return deviceService.getDevices(sensorType);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	public Mono<DeviceResponse> getDevice(@PathVariable long id)
	{
		return deviceService.getDevice(id);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public Mono<DeviceResponse> updateDevice(@PathVariable long id, @RequestBody UpdateDeviceRequest updateDeviceRequest)
	{
		return deviceService.updateDevice(id, updateDeviceRequest);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public Mono<Void> deleteDevice(@PathVariable long id)
	{
		return deviceService.deleteDevice(id);
	}

	@GetMapping(value = "/{id}/live-measurements", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> getDeviceLiveMeasurements(@PathVariable String id)
	{
		return measurementService.getMeasurements();
	}
}
