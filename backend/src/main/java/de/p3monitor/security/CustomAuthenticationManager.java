package de.p3monitor.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import de.p3monitor.security.auth.TokenService;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements ReactiveAuthenticationManager
{
	private final TokenService tokenService;

	@Override
	public Mono<Authentication> authenticate(Authentication authentication)
	{
		return tokenService.validateTokenAndReturnUserInternal((String)authentication.getCredentials())
				.map(user -> new UsernamePasswordAuthenticationToken(
						user.getUsername(),
						authentication.getCredentials(),
						List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString().toUpperCase()))));
	}
}
