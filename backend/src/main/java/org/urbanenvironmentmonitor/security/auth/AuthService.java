package org.urbanenvironmentmonitor.security.auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urbanenvironmentmonitor.security.auth.dtos.LoginRequest;
import org.urbanenvironmentmonitor.security.auth.dtos.LoginResponse;
import org.urbanenvironmentmonitor.security.auth.dtos.LogoutRequest;
import org.urbanenvironmentmonitor.security.auth.exceptions.InvalidCredentialsException;
import org.urbanenvironmentmonitor.user.UserService;
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
