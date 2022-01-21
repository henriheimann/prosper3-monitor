package org.urbanenvironmentmonitor.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableConfigurationProperties({CorsProperties.class})
public class CorsConfig implements WebFluxConfigurer
{
	private final CorsProperties corsProperties;

	public CorsConfig(CorsProperties deploymentProperties)
	{
		this.corsProperties = deploymentProperties;
	}

	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
		corsRegistry.addMapping("/**")
				.allowedOrigins(corsProperties.getAllowedOrigin())
				.allowedMethods("POST", "GET", "OPTIONS", "DELETE", "PUT")
				.maxAge(3600)
				.allowedHeaders("x-requested-with", "authorization", "content-type")
				.allowCredentials(true);
	}
}
