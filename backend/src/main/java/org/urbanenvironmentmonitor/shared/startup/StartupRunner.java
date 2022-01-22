package org.urbanenvironmentmonitor.shared.startup;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@AllArgsConstructor
public class StartupRunner implements CommandLineRunner
{
	private final StartupService startupService;

	@Override
	public void run(String... args)
	{
		startupService.setupStartupUsers()
				.subscribe();
	}
}
