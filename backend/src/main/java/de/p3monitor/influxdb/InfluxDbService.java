package de.p3monitor.influxdb;

import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.query.FluxRecord;
import de.p3monitor.influxdb.dtos.DeviceSensorType;
import de.p3monitor.influxdb.dtos.DeviceValuesResponse;
import de.p3monitor.influxdb.dtos.WeatherValuesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Slf4j
@Service
public class InfluxDbService
{
	private final InfluxDBClientReactive influxDBClient;

	public InfluxDbService(InfluxDBClientReactive influxDBClient)
	{
		this.influxDBClient = influxDBClient;
	}

	public Flux<String> test()
	{

		return Flux.from(influxDBClient.getQueryReactiveApi().query("""
						from(bucket:"p3m-bucket")
							|> range(start: -60m)
						"""))
				.map(FluxRecord::toString);
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
					var instant = (Instant)fluxRecord.getValueByKey("_time");
					if (instant != null) {
						var builder = WeatherValuesResponse.builder();
						builder.timestamp(LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId()));
						builder.city((String)fluxRecord.getValueByKey("city"));

						var cloudiness = (Long)fluxRecord.getValueByKey("cloudiness");
						if (cloudiness != null) {
							builder.cloudiness(cloudiness);
						}

						var humidity = (Long)fluxRecord.getValueByKey("humidity");
						if (humidity != null) {
							builder.humidity(humidity);
						}

						var temperature = (Double)fluxRecord.getValueByKey("temperature");
						if (temperature != null) {
							builder.temperature(temperature);
						}

						var rain = (Double)fluxRecord.getValueByKey("rain");
						if (rain != null) {
							builder.rain(rain);
						}

						return Mono.just(builder.build());
					} else {
						return Mono.empty();
					}
				});
	}

	public Mono<DeviceValuesResponse> getLastContact(String ttnDeviceId)
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

					var instant = (Instant)fluxRecord.getValueByKey("_time");
					if (instant != null) {
						var builder = DeviceValuesResponse.builder();
						builder.timestamp(LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId()));

						var sensorType = (Double)fluxRecord.getValueByKey("uplink_message_decoded_payload_sensor_type");
						if (sensorType != null) {
							builder.sensorType(DeviceSensorType.fromId(sensorType.longValue()));
						}

						var batteryVoltage = (Double)fluxRecord.getValueByKey("uplink_message_decoded_payload_battery_voltage");
						if (batteryVoltage != null) {
							builder.batteryVoltage(batteryVoltage);
						}

						var moistureCounter = (Double)fluxRecord.getValueByKey("uplink_message_decoded_payload_moisture_counter");
						if (moistureCounter != null) {
							builder.moistureCounter(moistureCounter);
						}

						var temperature = (Double)fluxRecord.getValueByKey("uplink_message_decoded_payload_temperature");
						if (temperature != null) {
							builder.temperature(temperature);
						}

						var humidity = (Double)fluxRecord.getValueByKey("uplink_message_decoded_payload_humidity");
						if (humidity != null) {
							builder.humidity(humidity);
						}

						var brightnessCurrent = (Double)fluxRecord.getValueByKey("uplink_message_decoded_payload_brightness_current");
						if (brightnessCurrent != null) {
							builder.brightnessCurrent(brightnessCurrent);
						}

						var irTemperature = (Double)fluxRecord.getValueByKey("uplink_message_decoded_payload_ir_temperature");
						if (irTemperature != null) {
							builder.irTemperature(irTemperature);
						}

						return Mono.just(builder.build());
					} else {
						return Mono.empty();
					}
				});
	}
}
