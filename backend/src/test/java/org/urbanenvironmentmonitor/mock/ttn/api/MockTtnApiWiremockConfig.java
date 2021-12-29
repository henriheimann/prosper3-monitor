package org.urbanenvironmentmonitor.mock.ttn.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({MockTtnApiWiremockProperties.class})
public class MockTtnApiWiremockConfig
{
	@Bean(initMethod = "start", destroyMethod = "stop")
	@Qualifier("mockTtnApiWireMockServer")
	public WireMockServer mockTtnApiWireMockServer(MockTtnApiWiremockProperties properties)
	{
		return new WireMockServer(properties.getPort());
	}
}
