package de.p3monitor.influxdb;

import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.query.FluxRecord;
import de.p3monitor.influxdb.dtos.DeviceSensorType;
import de.p3monitor.influxdb.dtos.DeviceValuesResponse;
import de.p3monitor.influxdb.dtos.WeatherValuesResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class InfluxDbService
{
	private final InfluxDBClientReactive influxDBClient;

	private DeviceValuesResponse buildDeviceValues(FluxRecord fluxRecord, String ttnDeviceId, boolean pivotedOnTtnId)
	{
		String suffix = "";

		if (pivotedOnTtnId) {
			suffix = "_v3/urban-environment-monitor@ttn/devices/" + ttnDeviceId + "/up";
		}

		var instant = (Instant) fluxRecord.getValueByKey("_time");
		if (instant != null) {
			var builder = DeviceValuesResponse.builder();
			builder.timestamp(ZonedDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId()));
			builder.ttnDeviceId(ttnDeviceId);

			boolean anyMeasurement = false;

			var sensorType = (Double) fluxRecord.getValueByKey("uplink_message_decoded_payload_sensor_type" + suffix);
			if (sensorType != null) {
				builder.sensorType(DeviceSensorType.fromId(sensorType.longValue()));
			}

			var batteryVoltage =
					(Double) fluxRecord.getValueByKey("uplink_message_decoded_payload_battery_voltage" + suffix);
			if (batteryVoltage != null) {
				builder.batteryVoltage(batteryVoltage);
				anyMeasurement = true;
			}

			var moistureCounter =
					(Double) fluxRecord.getValueByKey("uplink_message_decoded_payload_moisture_counter" + suffix);
			if (moistureCounter != null) {
				builder.moistureCounter(moistureCounter);
				anyMeasurement = true;
			}

			var temperature = (Double) fluxRecord.getValueByKey("uplink_message_decoded_payload_temperature" + suffix);
			if (temperature != null) {
				builder.temperature(temperature);
				anyMeasurement = true;
			}

			var humidity = (Double) fluxRecord.getValueByKey("uplink_message_decoded_payload_humidity" + suffix);
			if (humidity != null) {
				builder.humidity(humidity);
				anyMeasurement = true;
			}

			var brightnessCurrent = (Double) fluxRecord.getValueByKey(
					"uplink_message_decoded_payload_brightness_current" + suffix);
			if (brightnessCurrent != null) {
				builder.brightnessCurrent(brightnessCurrent);
				anyMeasurement = true;
			}

			var irTemperature =
					(Double) fluxRecord.getValueByKey("uplink_message_decoded_payload_ir_temperature" + suffix);
			if (irTemperature != null) {
				builder.irTemperature(irTemperature);
				anyMeasurement = true;
			}

			if (anyMeasurement) {
				return builder.build();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public Flux<Tuple2<ZonedDateTime, List<DeviceValuesResponse>>> getDevicesValues(List<String> ttnDeviceIds,
	                                                                                ZonedDateTime start,
	                                                                                ZonedDateTime stop,
	                                                                                long aggregateSeconds)
	{
		if (!stop.isAfter(start)) {
			throw new IllegalArgumentException("Stop time must be after start time.");
		}

		if (start.until(stop, ChronoUnit.SECONDS) / aggregateSeconds > 1000) {
			throw new IllegalArgumentException("Too many sensor values.");
		}

		String filterString = ttnDeviceIds.stream()
				.map(ttnDeviceId -> "\"v3/urban-environment-monitor@ttn/devices/" + ttnDeviceId + "/up\"")
				.collect(Collectors.joining(","));

		return Flux.from(influxDBClient.getQueryReactiveApi().query("""
						from(bucket: "p3m-bucket")
						    |> range(start: %s, stop: %s)
						    |> filter(fn: (r) => r._measurement == "mqtt_consumer")
						    |> drop(columns: ["host"])
							|> filter(fn: (r) => contains(value: r["topic"], set: [%s]))
						    |> aggregateWindow(every: %ds, fn: mean, createEmpty: true)
							|> pivot(rowKey:["_time"], columnKey: ["_field", "topic"], valueColumn: "_value")
						""".formatted(start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
						stop.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
						filterString,
						aggregateSeconds)))
				.flatMap(fluxRecord -> {
					var instant = (Instant) fluxRecord.getValueByKey("_time");
					if (instant != null) {
						return Mono.just(Tuples.of(
								ZonedDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId()),
								ttnDeviceIds.stream()
										.map(ttnDeviceId -> this.buildDeviceValues(fluxRecord, ttnDeviceId, true))
										.filter(Objects::nonNull)
										.collect(Collectors.toList())
						));
					} else {
						return Mono.empty();
					}
				});
	}

	public Mono<DeviceValuesResponse> getLatestDeviceValues(String ttnDeviceId)
	{
		return Mono.from(influxDBClient.getQueryReactiveApi().query("""
						from(bucket: "p3m-bucket")
							|> range(start: 0, stop: now())
						    |> filter(fn: (r) => r._measurement == "mqtt_consumer")
						    |> drop(columns: ["host"])
						    |> filter(fn: (r) => r["topic"] == "v3/urban-environment-monitor@ttn/devices/%s/up")
						    |> sort(columns:["_time"])
						    |> last()
						    |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
						""".formatted(ttnDeviceId)))
				.flatMap(fluxRecord -> {
					DeviceValuesResponse deviceValues = this.buildDeviceValues(fluxRecord, ttnDeviceId, false);
					if (deviceValues != null) {
						return Mono.just(deviceValues);
					} else {
						return Mono.empty();
					}
				});
	}

	public Mono<WeatherValuesResponse> getLatestCityWeather(String city)
	{
		return Mono.from(influxDBClient.getQueryReactiveApi().query("""
						from(bucket: "p3m-bucket")
							|> range(start: 0, stop: now())
						    |> filter(fn: (r) => r._measurement == "weather")
						    |> filter(fn: (r) => r["city"] == "%s")
						    |> sort(columns:["_time"])
						    |> last()
						    |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
						""".formatted(city)))
				.flatMap(fluxRecord -> {
					var instant = (Instant) fluxRecord.getValueByKey("_time");
					if (instant != null) {
						var builder = WeatherValuesResponse.builder();
						builder.timestamp(LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId()));
						builder.city((String) fluxRecord.getValueByKey("city"));

						var cloudiness = (Long) fluxRecord.getValueByKey("cloudiness");
						if (cloudiness != null) {
							builder.cloudiness(cloudiness);
						}

						var humidity = (Long) fluxRecord.getValueByKey("humidity");
						if (humidity != null) {
							builder.humidity(humidity);
						}

						var temperature = (Double) fluxRecord.getValueByKey("temperature");
						if (temperature != null) {
							builder.temperature(temperature);
						}

						var rain = (Double) fluxRecord.getValueByKey("rain");
						if (rain != null) {
							builder.rain(rain);
						}

						return Mono.just(builder.build());
					} else {
						return Mono.empty();
					}
				});
	}
}
