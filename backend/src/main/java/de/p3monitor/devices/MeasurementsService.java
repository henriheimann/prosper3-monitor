package de.p3monitor.devices;

import de.p3monitor.devices.requests.AveragedMeasurementsRequest;
import de.p3monitor.devices.requests.MeasurementsRequest;
import de.p3monitor.devices.responses.AveragedMeasurementsResponse;
import de.p3monitor.devices.responses.MeasurementsResponse;
import de.p3monitor.devices.responses.DeviceValuesResponse;
import de.p3monitor.influxdb.InfluxDbService;
import de.p3monitor.influxdb.dtos.InfluxDeviceValues;
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

	public static DeviceValuesResponse influxDeviceValuesToResponse(InfluxDeviceValues deviceValues)
	{
		return new DeviceValuesResponse(
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
			return 1 - clampPercentage((moistureCounter - 5200) / 100);
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

		Flux<Tuple2<ZonedDateTime, List<InfluxDeviceValues>>> timestampAndDeviceValuesFlux = deviceEntitiesMono
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
					List<InfluxDeviceValues> influxDeviceValues = timestampAndDeviceValueResponses.getT1().getT2();
					Map<String, Long> deviceTtnIdToIdMap = timestampAndDeviceValueResponses.getT2();

					Map<Long, DeviceValuesResponse> deviceValues = new HashMap<>();
					for (InfluxDeviceValues influxDeviceValue : influxDeviceValues) {
						if (measurementsRequest.getDeviceSensorType() == null ||
								influxDeviceValue.getSensorType() == measurementsRequest.getDeviceSensorType()) {
							deviceValues.put(deviceTtnIdToIdMap.get(influxDeviceValue.getTtnDeviceId()),
									influxDeviceValuesToResponse(influxDeviceValue)
							);
						}
					}

					MeasurementsResponse measurementsResponse = new MeasurementsResponse();
					measurementsResponse.setTimestamp(timestamp);
					measurementsResponse.setDeviceValues(deviceValues);
					return measurementsResponse;
				})
				.filter(measurementsResponse -> !measurementsResponse.getDeviceValues().isEmpty());
	}

	public Mono<AveragedMeasurementsResponse> averagedMeasurements(AveragedMeasurementsRequest averagedMeasurementsRequest)
	{
		return deviceRepository.findAll()
				.flatMap(deviceEntity -> influxDbService.getAveragedDevicesValues(deviceEntity.getTtnId(), averagedMeasurementsRequest.getStart(), averagedMeasurementsRequest.getStop()))
				.collectList()
				.map(deviceValues -> {
					double totalBattery = 0.0;
					int batteryCount = 0;
					double totalMoisture = 0.0;
					int moistureCount = 0;
					double totalTemperature = 0.0;
					int temperatureCount = 0;
					double totalHumidity = 0.0;
					int humidityCount = 0;
					double totalIrTemperature = 0.0;
					int irTemperatureCount = 0;
					double totalBrightness = 0.0;
					int brightnessCount = 0;

					for (InfluxDeviceValues deviceValue : deviceValues) {
						if (averagedMeasurementsRequest.getDeviceSensorType() == null ||
								deviceValue.getSensorType() == averagedMeasurementsRequest.getDeviceSensorType()) {
							if (deviceValue.getBatteryVoltage() != null) {
								totalBattery += deviceValue.getBatteryVoltage();
								batteryCount++;
							}
							if (deviceValue.getMoistureCounter() != null) {
								totalMoisture += transformMoistureCounterToPercentage(deviceValue.getMoistureCounter());
								moistureCount++;
							}
							if (deviceValue.getTemperature() != null) {
								totalTemperature += deviceValue.getTemperature();
								temperatureCount++;
							}
							if (deviceValue.getHumidity() != null) {
								totalHumidity += deviceValue.getHumidity();
								humidityCount++;
							}
							if (deviceValue.getIrTemperature() != null) {
								totalIrTemperature += deviceValue.getIrTemperature();
								irTemperatureCount++;
							}
							if (deviceValue.getBrightnessCurrent() != null) {
								totalBrightness += transformBrightnessCurrentToPercentage(deviceValue.getBrightnessCurrent());
								brightnessCount++;
							}
						}
					}

					DeviceValuesResponse deviceValuesResponse = new DeviceValuesResponse(
							batteryCount == 0 ? null : totalBattery / batteryCount,
							moistureCount == 0 ? null : totalMoisture / moistureCount,
							temperatureCount == 0 ? null : totalTemperature / temperatureCount,
							humidityCount == 0 ? null : totalHumidity / humidityCount,
							irTemperatureCount == 0 ? null : totalIrTemperature / irTemperatureCount,
							brightnessCount == 0 ? null : totalBrightness / brightnessCount
					);

					AveragedMeasurementsResponse averagedMeasurementsResponse = new AveragedMeasurementsResponse();
					averagedMeasurementsResponse.setStart(averagedMeasurementsResponse.getStart());
					averagedMeasurementsResponse.setStop(averagedMeasurementsRequest.getStop());
					averagedMeasurementsResponse.setDeviceValues(deviceValuesResponse);
					return averagedMeasurementsResponse;
				});
	}
}
