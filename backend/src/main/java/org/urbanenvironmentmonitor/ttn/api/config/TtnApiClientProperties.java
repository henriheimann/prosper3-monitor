package org.urbanenvironmentmonitor.ttn.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ttn.api")
public class TtnApiClientProperties
{
	private String url;
	private String token;
}
