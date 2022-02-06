package de.p3monitor.influxdb;

import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.query.FluxRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
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

	public Mono<LocalDateTime> getLastContact(String ttnDeviceId)
	{
		return Mono.from(influxDBClient.getQueryReactiveApi().query("""
				from(bucket: "p3m-bucket")
				  |> range(start: -30d)
				  |> movingAverage(n: 10)
				  |> filter(fn: (r) => r["topic"] == "v3/urban-environment-monitor@ttn/devices/%s/up")
				  |> keep(columns: ["_time"])
				  |> last(column: "_time")
				""".formatted(ttnDeviceId)))
				.flatMap(fluxRecord -> {
					Instant time = (Instant)fluxRecord.getValueByKey("_time");
					if (time != null) {
						return Mono.just(LocalDateTime.ofInstant(time, TimeZone.getDefault().toZoneId()));
					} else {
						return Mono.empty();
					}
				});
	}
}
