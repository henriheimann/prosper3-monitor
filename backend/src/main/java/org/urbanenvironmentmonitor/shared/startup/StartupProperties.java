package org.urbanenvironmentmonitor.shared.startup;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "startup")
public class StartupProperties
{
	private String adminUsername;
	private String adminPassword;
}
