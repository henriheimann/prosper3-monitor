package de.p3monitor.influxdb;

import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.query.FluxRecord;
import de.p3monitor.influxdb.dtos.DeviceSensorType;
import de.p3monitor.influxdb.dtos.InfluxDeviceValues;
import de.p3monitor.influxdb.dtos.InfluxWeatherValues;
import de.p3monitor.ttn.mqtt.config.TtnMqttClientProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
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
	private final TtnMqttClientProperties ttnProperties;

	private InfluxDeviceValues buildDeviceValues(FluxRecord fluxRecord, ZonedDateTime timestamp, String ttnDeviceId, boolean pivotedOnTtnId)
	{
		String suffix = "";

		if (pivotedOnTtnId) {
			suffix = "_v3/" + ttnProperties.getUsername() + "/devices/" + ttnDeviceId + "/up";
		}

		var builder = InfluxDeviceValues.builder();
		builder.timestamp(timestamp);
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
	}

	private Mono<? extends InfluxDeviceValues> buildDeviceValuesMonoFromRecord(String ttnDeviceId,
	                                                                           FluxRecord fluxRecord, Instant instant)
	{
		if (instant != null) {
			var timestamp = ZonedDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId());
			InfluxDeviceValues deviceValues = this.buildDeviceValues(fluxRecord, timestamp, ttnDeviceId, false);
			if (deviceValues != null) {
				return Mono.just(deviceValues);
			}
		}
		return Mono.empty();
	}

	private InfluxWeatherValues buildWeatherValues(FluxRecord fluxRecord, ZonedDateTime timestamp)
	{
		var builder = InfluxWeatherValues.builder();
		builder.timestamp(timestamp);
		builder.city((String) fluxRecord.getValueByKey("city"));

		var cloudiness = (Double) fluxRecord.getValueByKey("cloudiness");
		if (cloudiness != null) {
			builder.cloudiness(cloudiness);
		}

		var humidity = (Double) fluxRecord.getValueByKey("humidity");
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

		return builder.build();
	}

	private Mono<? extends InfluxWeatherValues> buildWeatherValuesMonoFromRecord(FluxRecord fluxRecord, Instant instant)
	{
		if (instant != null) {
			var timestamp = ZonedDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId());
			InfluxWeatherValues weatherValues = this.buildWeatherValues(fluxRecord, timestamp);
			if (weatherValues != null) {
				return Mono.just(weatherValues);
			}
		}
		return Mono.empty();
	}

	public Flux<Tuple2<ZonedDateTime, List<InfluxDeviceValues>>> getDevicesValues(List<String> ttnDeviceIds,
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
				.map(ttnDeviceId -> "\"v3/" + ttnProperties.getUsername() + "/devices/" + ttnDeviceId + "/up\"")
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
						var timestamp = ZonedDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId());
						return Mono.just(Tuples.of(
								timestamp,
								ttnDeviceIds.stream()
										.map(ttnDeviceId -> this.buildDeviceValues(fluxRecord, timestamp, ttnDeviceId, true))
										.filter(Objects::nonNull)
										.collect(Collectors.toList())
						));
					} else {
						return Mono.empty();
					}
				});
	}

	public Mono<InfluxDeviceValues> getAveragedDevicesValues(String ttnDeviceId, ZonedDateTime start, ZonedDateTime stop)
	{
		if (!stop.isAfter(start)) {
			throw new IllegalArgumentException("Stop time must be after start time.");
		}

		return Mono.from(influxDBClient.getQueryReactiveApi().query("""
						from(bucket: "p3m-bucket")
							|> range(start: %s, stop: %s)
							|> filter(fn: (r) => r._measurement == "mqtt_consumer")
							|> drop(columns: ["host"])
						    |> filter(fn: (r) => r["topic"] == "v3/%s/devices/%s/up")
							|> mean()
							|> group(columns: ["_measurement", "_start", "_stop"])
							|> pivot(rowKey:["topic"], columnKey: ["_field"], valueColumn: "_value")
						""".formatted(start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
						stop.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), ttnProperties.getUsername(), ttnDeviceId)))
				.flatMap(fluxRecord -> {
					var instant = (Instant) fluxRecord.getValueByKey("_stop");
					return buildDeviceValuesMonoFromRecord(ttnDeviceId, fluxRecord, instant);
				});
	}

	public Mono<InfluxDeviceValues> getLatestDeviceValues(String ttnDeviceId)
	{
		return Mono.from(influxDBClient.getQueryReactiveApi().query("""
						from(bucket: "p3m-bucket")
							|> range(start: 0, stop: now())
						    |> filter(fn: (r) => r._measurement == "mqtt_consumer")
						    |> drop(columns: ["host"])
						    |> filter(fn: (r) => r["topic"] == "v3/%s/devices/%s/up")
						    |> sort(columns:["_time"])
						    |> last()
						    |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
						""".formatted(ttnProperties.getUsername(), ttnDeviceId)))
				.flatMap(fluxRecord -> {
					var instant = (Instant) fluxRecord.getValueByKey("_time");
					return buildDeviceValuesMonoFromRecord(ttnDeviceId, fluxRecord, instant);
				});
	}

	public Mono<InfluxWeatherValues> getAveragedCityWeather(String city, ZonedDateTime start, ZonedDateTime stop)
	{
		if (!stop.isAfter(start)) {
			throw new IllegalArgumentException("Stop time must be after start time.");
		}

		return Mono.from(influxDBClient.getQueryReactiveApi().query("""
						from(bucket: "p3m-bucket")
							|> range(start: %s, stop: %s)
							|> filter(fn: (r) => r._measurement == "weather")
							|> filter(fn: (r) => r["city"] == "%s" and (r._field == "temperature" or r._field == "humidity" or r._field == "cloudiness" or r._field == "rain"))
						    |> mean()
							|> group(columns: ["_measurement", "_start", "_stop"])
							|> pivot(rowKey:["city"], columnKey: ["_field"], valueColumn: "_value")
						""".formatted(start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
								stop.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), city)))
				.flatMap(fluxRecord -> {
					var instant = (Instant) fluxRecord.getValueByKey("_stop");
					return buildWeatherValuesMonoFromRecord(fluxRecord, instant);
				});
	}

	public Mono<InfluxWeatherValues> getLatestCityWeather(String city)
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
					return buildWeatherValuesMonoFromRecord(fluxRecord, instant);
				});
	}
}
