package de.p3monitor.shared.startup;

import de.p3monitor.users.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties({StartupProperties.class})
public class StartupService
{
	private final UserService userService;
	private final StartupProperties properties;

	public Mono<Void> setupStartupUsers()
	{
		return Flux.fromIterable(properties.getUsers())
				.flatMap(userSetup -> userService.setupUserInternal(userSetup.getUsername(), userSetup.getPassword(),
						userSetup.getRole()))
				.doOnNext(userEntity -> log.info("Setup user " + userEntity.getUsername() + " with role " +
						userEntity.getRole()))
				.then();
	}
}
