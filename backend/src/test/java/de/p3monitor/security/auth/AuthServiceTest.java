package de.p3monitor.security.auth;

import de.p3monitor.security.auth.dtos.LoginResponse;
import de.p3monitor.users.UserEntity;
import de.p3monitor.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import de.p3monitor.security.auth.dtos.LoginRequest;
import de.p3monitor.security.auth.exceptions.InvalidCredentialsException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest
{
	private TokenService tokenServiceMock;
	private UserService userServiceMock;
	private AuthProperties authPropertiesMock;
	private PasswordEncoder passwordEncoderMock;

	private AuthService authService;

	@BeforeEach
	public void setUp()
	{
		tokenServiceMock = mock(TokenService.class);
		userServiceMock = mock(UserService.class);
		authPropertiesMock = mock(AuthProperties.class);
		passwordEncoderMock = mock(PasswordEncoder.class);

		authService = new AuthService(tokenServiceMock, userServiceMock, authPropertiesMock, passwordEncoderMock);
	}

	@Test
	void login_createsTokenAndReturnsResponse_forCorrectPassword()
	{
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("user");
		userEntity.setPassword("password");

		when(authPropertiesMock.getExpirySeconds()).thenReturn(60L);
		when(userServiceMock.getUserInternal("user")).thenReturn(Mono.just(userEntity));
		when(passwordEncoderMock.matches("password", "password")).thenReturn(true);
		when(tokenServiceMock.createToken(eq(userEntity), any())).thenReturn(Mono.just("generated_token"));

		Mono<LoginResponse> response = authService.login(new LoginRequest("user", "password"));

		StepVerifier.create(response)
				.expectNext(new LoginResponse("generated_token"))
				.expectComplete()
				.verify();
	}

	@Test
	void login_returnsError_forIncorrectPassword()
	{
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("user");
		userEntity.setPassword("password");

		when(authPropertiesMock.getExpirySeconds()).thenReturn(60L);
		when(userServiceMock.getUserInternal("user")).thenReturn(Mono.just(userEntity));
		when(passwordEncoderMock.matches("wrong_password", "password")).thenReturn(false);
		when(tokenServiceMock.createToken(eq(userEntity), any())).thenReturn(Mono.just("generated_token"));

		Mono<LoginResponse> response = authService.login(new LoginRequest("user", "wrong_password"));

		StepVerifier.create(response)
				.expectError(InvalidCredentialsException.class)
				.verify();
	}
}
