package de.p3monitor.influxdb;

import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.query.FluxRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
}
