package org.urbanenvironmentmonitor.security.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.urbanenvironmentmonitor.security.auth.dtos.LoginRequest;
import org.urbanenvironmentmonitor.security.auth.dtos.LoginResponse;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController
{
	private final AuthService authService;

	@PostMapping("/login")
	public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest)
	{
		return authService.login(loginRequest);
	}

	@PostMapping("/logout")
	public Mono<Void> logout(Authentication authentication)
	{
		return authService.logout((String)authentication.getCredentials());
	}
}
