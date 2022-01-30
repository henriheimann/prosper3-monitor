package de.p3monitor.security.auth;

import de.p3monitor.security.auth.dtos.LoginRequest;
import de.p3monitor.security.auth.dtos.LoginResponse;
import de.p3monitor.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import de.p3monitor.security.auth.exceptions.InvalidCredentialsException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@EnableConfigurationProperties({AuthProperties.class})
@AllArgsConstructor
public class AuthService
{
	private final TokenService tokenService;
	private final UserService userService;
	private final AuthProperties authProperties;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Mono<LoginResponse> login(LoginRequest loginRequest)
	{
		LocalDateTime expiry = LocalDateTime.now().plusSeconds(authProperties.getExpirySeconds());

		return userService.getUserInternal(loginRequest.getUsername())
				.filter(userEntity -> passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword()))
				.flatMap(userEntity -> tokenService.createToken(userEntity, expiry))
				.map(LoginResponse::new)
				.switchIfEmpty(Mono.error(new InvalidCredentialsException()));
	}

	@Transactional
	public Mono<Void> logout(String token)
	{
		return tokenService.deleteToken(token);
	}
}
