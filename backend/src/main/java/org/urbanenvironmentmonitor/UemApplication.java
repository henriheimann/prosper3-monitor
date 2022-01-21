package org.urbanenvironmentmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UemApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(UemApplication.class, args);
	}
}
