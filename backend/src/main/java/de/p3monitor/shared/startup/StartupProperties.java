package de.p3monitor.shared.startup;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import de.p3monitor.user.UserRole;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "startup")
public class StartupProperties
{
	private List<UserSetup> users;

	@Getter
	@Setter
	public static class UserSetup
	{
		private String username;
		private String password;
		private UserRole role;
	}
}
