package de.p3monitor.device;

import de.p3monitor.device.dtos.MeasurementsRequest;
import de.p3monitor.device.dtos.MeasurementsResponse;
import de.p3monitor.device.dtos.SensorValuesResponse;
import de.p3monitor.influxdb.InfluxDbService;
import de.p3monitor.influxdb.dtos.DeviceValuesResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MeasurementsService
{
	private final InfluxDbService influxDbService;
	private final DeviceRepository deviceRepository;

	public static SensorValuesResponse deviceToSensorValues(DeviceValuesResponse deviceValues)
	{
		return new SensorValuesResponse(
				deviceValues.getBatteryVoltage(),
				transformMoistureCounterToPercentage(deviceValues.getMoistureCounter()),
				deviceValues.getTemperature(),
				deviceValues.getHumidity(),
				deviceValues.getIrTemperature(),
				transformBrightnessCurrentToPercentage(deviceValues.getBrightnessCurrent())
		);
	}

	private static double clampPercentage(double percentage)
	{
		return Math.min(100, Math.max(0, percentage));
	}

	private static Double transformMoistureCounterToPercentage(Double moistureCounter)
	{
		if (moistureCounter != null) {
			return 1 - clampPercentage((moistureCounter - 5200) / 300);
		} else {
			return null;
		}
	}

	private static Double transformBrightnessCurrentToPercentage(Double brightnessCurrent)
	{
		if (brightnessCurrent != null) {
			return clampPercentage(Math.log(brightnessCurrent / 1000 + 1));
		} else {
			return null;
		}
	}

	public Flux<MeasurementsResponse> measurements(MeasurementsRequest measurementsRequest)
	{
		Mono<List<DeviceEntity>> deviceEntitiesMono = deviceRepository.findAll()
				.collectList()
				.cache();

		Flux<Tuple2<ZonedDateTime, List<DeviceValuesResponse>>> timestampAndDeviceValuesFlux = deviceEntitiesMono
				.map(deviceEntities -> deviceEntities.stream()
						.map(DeviceEntity::getTtnId)
						.collect(Collectors.toList()))
				.flatMapMany(ttnDeviceIds -> influxDbService.getDevicesValues(ttnDeviceIds,
						measurementsRequest.getStart(),
						measurementsRequest.getStop(),
						measurementsRequest.getAggregateWindowSeconds())
				);

		Flux<Map<String, Long>> deviceTtnIdToIdMapRepeatedFlux = deviceEntitiesMono
				.map(deviceEntities -> deviceEntities.stream()
						.collect(Collectors.toMap(DeviceEntity::getTtnId, DeviceEntity::getId)))
				.repeat();

		return Flux.zip(timestampAndDeviceValuesFlux, deviceTtnIdToIdMapRepeatedFlux)
				.map(timestampAndDeviceValueResponses -> {
					ZonedDateTime timestamp = timestampAndDeviceValueResponses.getT1().getT1();
					List<DeviceValuesResponse> deviceValues = timestampAndDeviceValueResponses.getT1().getT2();
					Map<String, Long> deviceTtnIdToIdMap = timestampAndDeviceValueResponses.getT2();

					Map<Long, SensorValuesResponse> sensorValues = new HashMap<>();
					for (DeviceValuesResponse deviceValue : deviceValues) {
						if (measurementsRequest.getDeviceSensorType() == null ||
								deviceValue.getSensorType() == measurementsRequest.getDeviceSensorType()) {
							sensorValues.put(deviceTtnIdToIdMap.get(deviceValue.getTtnDeviceId()),
									deviceToSensorValues(deviceValue)
							);
						}
					}

					MeasurementsResponse measurementsResponse = new MeasurementsResponse();
					measurementsResponse.setTimestamp(timestamp);
					measurementsResponse.setSensorValues(sensorValues);
					return measurementsResponse;
				});
	}
}
