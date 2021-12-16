package org.urbanenvironmentmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@EnableReactiveFeignClients
@SpringBootApplication
public class UemApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(UemApplication.class, args);
	}
}
