package de.p3monitor.ttn.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ttn")
public class TtnProperties
{
	private String applicationId;
}
