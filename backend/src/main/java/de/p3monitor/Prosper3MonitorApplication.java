package de.p3monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Prosper3MonitorApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(Prosper3MonitorApplication.class, args);
	}
}
