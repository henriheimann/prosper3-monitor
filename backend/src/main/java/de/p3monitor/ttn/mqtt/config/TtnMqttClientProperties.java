package de.p3monitor.ttn.mqtt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ttn.mqtt")
public class TtnMqttClientProperties
{
	private String url;
	private String clientId;
	private String username;
	private String password;
}
