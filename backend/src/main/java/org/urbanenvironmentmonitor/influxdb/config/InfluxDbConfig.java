package org.urbanenvironmentmonitor.influxdb.config;

import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.InfluxDBClientReactiveFactory;
import okhttp3.Protocol;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Configuration
@EnableConfigurationProperties({InfluxDbProperties.class})
public class InfluxDbConfig
{
	@Bean
	public InfluxDBClientReactive influxDBClientReactive(InfluxDbProperties properties)
	{
		okhttp3.OkHttpClient.Builder okHttpBuilder = (new okhttp3.OkHttpClient.Builder())
				.protocols(Collections.singletonList(Protocol.HTTP_1_1))
				.readTimeout(properties.getReadTimeout())
				.writeTimeout(properties.getWriteTimeout())
				.connectTimeout(properties.getConnectTimeout());

		InfluxDBClientOptions.Builder influxBuilder = InfluxDBClientOptions.builder()
				.url(properties.getUrl())
				.bucket(properties.getBucket())
				.org(properties.getOrg())
				.okHttpClient(okHttpBuilder);

		if (StringUtils.hasLength(properties.getToken())) {
			influxBuilder.authenticateToken(properties.getToken().toCharArray());
		} else if (StringUtils.hasLength(properties.getUsername()) && StringUtils.hasLength(properties.getPassword())) {
			influxBuilder.authenticate(properties.getUsername(), properties.getPassword().toCharArray());
		}
		
		return InfluxDBClientReactiveFactory.create(influxBuilder.build())
				.setLogLevel(properties.getLogLevel());
	}
}
