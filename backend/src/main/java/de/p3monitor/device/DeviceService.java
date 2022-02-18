package de.p3monitor.device;

import de.p3monitor.device.dtos.CreateDeviceRequest;
import de.p3monitor.device.dtos.DeviceResponse;
import de.p3monitor.device.dtos.LastContactResponse;
import de.p3monitor.device.dtos.UpdateDeviceRequest;
import de.p3monitor.influxdb.dtos.DeviceSensorType;
import de.p3monitor.influxdb.InfluxDbService;
import de.p3monitor.device.dtos.SensorValuesResponse;
import de.p3monitor.ttn.TtnService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class DeviceService
{
	private final DeviceRepository deviceRepository;
	private final TtnService ttnService;
	private final InfluxDbService influxDbService;

	private DeviceResponse createDeviceResponse(Tuple2<DeviceEntity, Optional<LastContactResponse>> tuple, boolean includeKeys)
	{
		DeviceResponse deviceResponse = new DeviceResponse();
		deviceResponse.setId(tuple.getT1().getId());
		deviceResponse.setName(tuple.getT1().getName());
		deviceResponse.setTtnId(tuple.getT1().getTtnId());
		if (includeKeys) {
			deviceResponse.setTtnDeviceAddress(tuple.getT1().getTtnDeviceAddress());
			deviceResponse.setTtnApplicationSessionKey(tuple.getT1().getTtnApplicationSessionKey());
			deviceResponse.setTtnNetworkSessionKey(tuple.getT1().getTtnNetworkSessionKey());
		}
		if (tuple.getT2().isPresent()) {
			deviceResponse.setLastContact(tuple.getT2().get());
		}
		deviceResponse.setLatitude(tuple.getT1().getLatitude());
		deviceResponse.setLongitude(tuple.getT1().getLongitude());
		return deviceResponse;
	}

	private Mono<Tuple2<DeviceEntity, Optional<LastContactResponse>>> zipWithLastContact(DeviceEntity deviceEntity)
	{
		Mono<Optional<LastContactResponse>> lastContactOptional = Mono.empty();
		if (deviceEntity.getTtnId() != null) {
			lastContactOptional = influxDbService.getLatestDeviceValues(deviceEntity.getTtnId())
					.map(deviceValues -> {
						LastContactResponse lastContact = new LastContactResponse();
						lastContact.setTimestamp(deviceValues.getTimestamp());
						lastContact.setSensorType(deviceValues.getSensorType());
						lastContact.setSensorValues(MeasurementsService.deviceToSensorValues(deviceValues));
						return Optional.of(lastContact);
					})
					.defaultIfEmpty(Optional.empty());
		}
		return Mono.zip(Mono.just(deviceEntity), lastContactOptional);
	}

	@Transactional
	public Mono<DeviceResponse> createDevice(CreateDeviceRequest createDeviceRequest)
	{
		DeviceEntity entity = new DeviceEntity();
		entity.setName(createDeviceRequest.getName());
		entity.setLatitude(createDeviceRequest.getLatitude());
		entity.setLongitude(createDeviceRequest.getLongitude());

		return deviceRepository.save(entity)
				.zipWith(ttnService.createEndDevice()
						.map(Optional::of)
						.doOnError(e -> log.warn("Unable to create TTN end device", e))
						.onErrorReturn(Optional.empty()))
				.flatMap(tuple -> {
					var deviceEntity = tuple.getT1();
					var endDeviceOptional = tuple.getT2();
					endDeviceOptional.ifPresent(endDevice -> {
						deviceEntity.setTtnId(endDevice.getIds().getDeviceId());
						deviceEntity.setTtnDeviceAddress(endDevice.getSession().getDeviceAddress());
						deviceEntity.setTtnApplicationSessionKey(endDevice.getSession().getKeys().getAppSessionKey().getKey());
						deviceEntity.setTtnNetworkSessionKey(endDevice.getSession().getKeys().getNetworkSessionKey().getKey());
					});
					return deviceRepository.save(deviceEntity);
				})
				.flatMap(this::zipWithLastContact)
				.map(tuple -> createDeviceResponse(tuple, false));
	}

	@Transactional
	public Mono<DeviceResponse> getDevice(long id)
	{
		return deviceRepository.findById(id)
				.flatMap(this::zipWithLastContact)
				.map(tuple -> createDeviceResponse(tuple, true));
	}

	@Transactional
	public Flux<DeviceResponse> getDevices(DeviceSensorType sensorType)
	{
		return deviceRepository.findAll()
				.flatMap(this::zipWithLastContact)
				.filter(tuple -> sensorType == null || (tuple.getT2().isPresent() &&
						tuple.getT2().get().getSensorType() == sensorType))
				.map(tuple -> createDeviceResponse(tuple, false));
	}

	@Transactional
	public Mono<DeviceResponse> updateDevice(long id, UpdateDeviceRequest updateDeviceRequest)
	{
		return deviceRepository.findById(id)
				.doOnNext(device -> device.setName(updateDeviceRequest.getName()))
				.flatMap(deviceRepository::save)
				.flatMap(this::zipWithLastContact)
				.map(tuple -> createDeviceResponse(tuple, false));
	}

	@Transactional
	public Mono<Void> deleteDevice(long id)
	{
		return deviceRepository.findById(id)
				.flatMap(deviceEntity -> ttnService.deleteEndDevice(deviceEntity.getTtnId()))
				.then(deviceRepository.findById(id))
				.flatMap(deviceRepository::delete);
	}
}
