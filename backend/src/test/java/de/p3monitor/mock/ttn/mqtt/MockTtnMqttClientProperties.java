package de.p3monitor.mock.ttn.mqtt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "test.mock.ttn.mqtt")
public class MockTtnMqttClientProperties
{
	private String url;
	private String clientId;
}
