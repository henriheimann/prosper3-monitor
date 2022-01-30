package de.p3monitor.shared.scheduler;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import de.p3monitor.security.auth.TokenService;

import java.util.concurrent.TimeUnit;

@Component
@Profile("!test")
@EnableScheduling
@AllArgsConstructor
public class SchedulerConfig
{
	private final TokenService tokenService;

	@Scheduled(initialDelay = 0, fixedDelay = 1, timeUnit = TimeUnit.HOURS)
	public void removeExpiredTokensFromDatabase()
	{
		tokenService.removeExpiredTokensFromDatabase()
				.subscribe();
	}
}
