package de.p3monitor.influxdb.config;

import com.influxdb.LogLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "influx")
public class InfluxDbProperties
{
	private String url;
	private String username;
	private String password;
	private String token;
	private String org;
	private String bucket;
	private LogLevel logLevel;
	private Duration readTimeout;
	private Duration writeTimeout;
	private Duration connectTimeout;

	public InfluxDbProperties()
	{
		this.logLevel = LogLevel.NONE;
		this.readTimeout = Duration.ofMillis(10000L);
		this.writeTimeout = Duration.ofMillis(10000L);
		this.connectTimeout = Duration.ofMillis(10000L);
	}
}
