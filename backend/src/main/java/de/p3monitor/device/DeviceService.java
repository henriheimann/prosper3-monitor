package de.p3monitor.device;

import de.p3monitor.device.dtos.CreateDeviceRequest;
import de.p3monitor.device.dtos.DeviceResponse;
import de.p3monitor.device.dtos.UpdateDeviceRequest;
import de.p3monitor.influxdb.InfluxDbService;
import de.p3monitor.ttn.TtnService;
import de.p3monitor.ttn.api.dtos.EndDevice;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
@Service
public class DeviceService
{
	private final DeviceRepository deviceRepository;
	private final TtnService ttnService;
	private final InfluxDbService influxDbService;

	private Mono<Tuple2<DeviceEntity, Optional<LocalDateTime>>> zipWithLastContact(DeviceEntity deviceEntity)
	{
		Mono<Optional<LocalDateTime>> lastContact = Mono.empty();
		if (deviceEntity.getTtnId() != null) {
			lastContact = influxDbService.getLastContact(deviceEntity.getTtnId())
					.map(Optional::of)
					.defaultIfEmpty(Optional.empty());
		}
		return Mono.zip(Mono.just(deviceEntity), lastContact);
	}

	@Transactional
	public Mono<DeviceResponse> createDevice(CreateDeviceRequest createDeviceRequest)
	{
		return deviceRepository.save(new DeviceEntity(createDeviceRequest.getName()))
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
				.map(tuple -> new DeviceResponse(tuple.getT1(), tuple.getT2().orElse(null)));
	}

	@Transactional
	public Mono<DeviceResponse> getDevice(long id)
	{
		return deviceRepository.findById(id)
				.flatMap(this::zipWithLastContact)
				.map(tuple -> new DeviceResponse(tuple.getT1(), tuple.getT2().orElse(null)));
	}

	@Transactional
	public Flux<DeviceResponse> getDevices()
	{
		return deviceRepository.findAll()
				.flatMap(this::zipWithLastContact)
				.map(tuple -> new DeviceResponse(tuple.getT1(), tuple.getT2().orElse(null)));
	}

	@Transactional
	public Mono<DeviceResponse> updateDevice(long id, UpdateDeviceRequest updateDeviceRequest)
	{
		return deviceRepository.findById(id)
				.doOnNext(device -> {
					device.setName(updateDeviceRequest.getName());
				})
				.flatMap(deviceRepository::save)
				.flatMap(this::zipWithLastContact)
				.map(tuple -> new DeviceResponse(tuple.getT1(), tuple.getT2().orElse(null)));
	}

	@Transactional
	public Mono<Void> deleteDevice(long id)
	{
		return deviceRepository.findById(id)
				.flatMap(deviceEntity -> ttnService.deleteEndDevice(deviceEntity.getTtnId()))
				.then(deviceRepository.findById(id))
				.flatMap(deviceRepository::delete);
	}

	@AllArgsConstructor
	private static class SyncOperation
	{
		public enum Type
		{
			ADD_DEVICE_WITH_TTN_ID,
			ERASE_DEVICES_TTN_ID
		}

		private final Type type;
		private final EndDevice ttnDevice;
		private final DeviceEntity deviceEntity;
	}

	@Transactional
	public Flux<DeviceResponse> syncDevicesWithTtn()
	{
		return ttnService.getEndDevices()
				.zipWith(deviceRepository.findAll().collectList())
				.map(tuple -> {
					var endDevices = tuple.getT1();
					var deviceEntities = tuple.getT2();

					var ttnIds =  endDevices.stream()
							.map(endDevice -> endDevice.getIds().getDeviceId())
							.collect(Collectors.toSet());

					var deviceEntitiesByTtnId = deviceEntities.stream()
							.collect(Collectors.toMap(DeviceEntity::getTtnId, deviceEntity -> deviceEntity));

					return Stream.concat(
							endDevices.stream()
									.filter(endDevice -> !deviceEntitiesByTtnId.containsKey(endDevice.getIds().getDeviceId()))
									.map(endDevice -> new SyncOperation(SyncOperation.Type.ADD_DEVICE_WITH_TTN_ID, endDevice, null)),
							deviceEntities.stream()
									.filter(deviceEntity -> !ttnIds.contains(deviceEntity.getTtnId()))
									.map(deviceEntity -> new SyncOperation(SyncOperation.Type.ERASE_DEVICES_TTN_ID, null, deviceEntity))
					).collect(Collectors.toList());
				})
				.flatMapMany(Flux::fromIterable)
				.flatMap(syncOperation -> {
					if (syncOperation.type == SyncOperation.Type.ADD_DEVICE_WITH_TTN_ID) {
						return ttnService.getEndDevice(syncOperation.ttnDevice.getIds().getDeviceId())
								.map(endDevice -> new SyncOperation(SyncOperation.Type.ADD_DEVICE_WITH_TTN_ID, endDevice, null));
					} else {
						return Mono.just(syncOperation);
					}
				})
				.flatMap(syncOperation -> {
					DeviceEntity deviceEntity;
					if (syncOperation.type == SyncOperation.Type.ADD_DEVICE_WITH_TTN_ID) {
						deviceEntity = new DeviceEntity();
						deviceEntity.setName(syncOperation.ttnDevice.getIds().getDeviceId());
						deviceEntity.setTtnId(syncOperation.ttnDevice.getIds().getDeviceId());
						deviceEntity.setTtnDeviceAddress(syncOperation.ttnDevice.getSession().getDeviceAddress());
						deviceEntity.setTtnApplicationSessionKey(syncOperation.ttnDevice.getSession().getKeys().getAppSessionKey().getKey());
						deviceEntity.setTtnNetworkSessionKey(syncOperation.ttnDevice.getSession().getKeys().getNetworkSessionKey().getKey());
					} else {
						deviceEntity = syncOperation.deviceEntity;
						deviceEntity.setTtnId(null);
						deviceEntity.setTtnDeviceAddress(null);
						deviceEntity.setTtnApplicationSessionKey(null);
						deviceEntity.setTtnNetworkSessionKey(null);
					}
					return deviceRepository.save(deviceEntity);
				})
				.flatMap(this::zipWithLastContact)
				.map(tuple -> new DeviceResponse(tuple.getT1(), tuple.getT2().orElse(null)));
	}
}
