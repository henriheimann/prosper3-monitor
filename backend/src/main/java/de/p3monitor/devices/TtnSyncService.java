package de.p3monitor.devices;

import de.p3monitor.ttn.TtnService;
import de.p3monitor.ttn.api.dtos.EndDevice;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class TtnSyncService
{
	private final TtnService ttnService;
	private final DeviceRepository deviceRepository;

	public enum SyncType
	{
		ADD_DEVICE_WITH_TTN_ID,
		ERASE_DEVICES_TTN_ID
	}

	@AllArgsConstructor
	private static class SyncOperation
	{
		private final SyncType type;
		private final EndDevice ttnDevice;
		private final DeviceEntity deviceEntity;
	}
	
	@Transactional
	public Mono<Void> syncDevices()
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
									.map(endDevice -> new SyncOperation(SyncType.ADD_DEVICE_WITH_TTN_ID, endDevice, null)),
							deviceEntities.stream()
									.filter(deviceEntity -> !ttnIds.contains(deviceEntity.getTtnId()))
									.map(deviceEntity -> new SyncOperation(SyncType.ERASE_DEVICES_TTN_ID, null, deviceEntity))
					).collect(Collectors.toList());
				})
				.flatMapMany(Flux::fromIterable)
				.flatMap(syncOperation -> {
					if (syncOperation.type == SyncType.ADD_DEVICE_WITH_TTN_ID) {
						return ttnService.getEndDevice(syncOperation.ttnDevice.getIds().getDeviceId())
								.map(endDevice -> new SyncOperation(SyncType.ADD_DEVICE_WITH_TTN_ID, endDevice, null));
					} else {
						return Mono.just(syncOperation);
					}
				})
				.flatMap(syncOperation -> {
					DeviceEntity deviceEntity;
					if (syncOperation.type == SyncType.ADD_DEVICE_WITH_TTN_ID) {
						deviceEntity = new DeviceEntity();
						deviceEntity.setName(syncOperation.ttnDevice.getIds().getDeviceId());
						deviceEntity.setTtnId(syncOperation.ttnDevice.getIds().getDeviceId());
						deviceEntity.setTtnDeviceAddress(syncOperation.ttnDevice.getSession().getDeviceAddress());
						deviceEntity.setTtnApplicationSessionKey(syncOperation.ttnDevice.getSession().getKeys().getAppSessionKey().getKey());
						deviceEntity.setTtnNetworkSessionKey(syncOperation.ttnDevice.getSession().getKeys().getNetworkSessionKey().getKey());
						deviceEntity.setLatitude(51.532375);
						deviceEntity.setLongitude(6.933003);
					} else {
						deviceEntity = syncOperation.deviceEntity;
						deviceEntity.setTtnId(null);
						deviceEntity.setTtnDeviceAddress(null);
						deviceEntity.setTtnApplicationSessionKey(null);
						deviceEntity.setTtnNetworkSessionKey(null);
					}
					return deviceRepository.save(deviceEntity);
				})
				.collectList()
				.then();
	}
}
