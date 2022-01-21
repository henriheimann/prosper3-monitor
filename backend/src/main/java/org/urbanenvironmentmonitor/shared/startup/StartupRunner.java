package org.urbanenvironmentmonitor.shared.startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.urbanenvironmentmonitor.user.UserRole;
import org.urbanenvironmentmonitor.user.UserService;

@Slf4j
@Component
@EnableConfigurationProperties({StartupProperties.class})
public class StartupRunner implements CommandLineRunner
{
	private final UserService userService;
	private final StartupProperties properties;

	public StartupRunner(UserService userService, StartupProperties properties)
	{
		this.userService = userService;
		this.properties = properties;
	}

	@Override
	public void run(String... args)
	{
		userService.setupUserInternal(properties.getAdminUsername(), properties.getAdminPassword(), UserRole.ADMIN)
				.doOnNext(userEntity -> log.info("Setup admin user " + properties.getAdminUsername()))
				.subscribe();
	}
}
