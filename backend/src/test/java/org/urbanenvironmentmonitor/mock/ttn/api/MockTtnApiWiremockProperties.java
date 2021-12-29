package org.urbanenvironmentmonitor.mock.ttn.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "test.mock.ttn.api")
public class MockTtnApiWiremockProperties
{
	private int port;
}
