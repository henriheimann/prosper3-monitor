package de.p3monitor.ttn.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties({TtnProperties.class})
public class TtnConfig
{

}
