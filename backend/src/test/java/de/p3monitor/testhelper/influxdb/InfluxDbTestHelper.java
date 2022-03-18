package de.p3monitor.testhelper.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import de.p3monitor.influxdb.config.InfluxDbProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class InfluxDbTestHelper
{
	private final InfluxDbProperties influxDbProperties;
	private final InfluxDBClient influxDBClient;

	public InfluxDbTestHelper(InfluxDbProperties influxDbProperties, InfluxDBClientOptions influxDBClientOptions)
	{
		this.influxDbProperties = influxDbProperties;
		this.influxDBClient = InfluxDBClientFactory.create(influxDBClientOptions)
				.setLogLevel(influxDbProperties.getLogLevel());
	}

	public void cleanupDatabase()
	{
		influxDBClient.getDeleteApi().delete(OffsetDateTime.of(LocalDateTime.now().minusYears(1), ZoneOffset.UTC),
				OffsetDateTime.now(), "", influxDbProperties.getBucket(), influxDbProperties.getOrg());
	}
}
