package de.p3monitor.security.auth;

import de.p3monitor.users.UserEntity;
import de.p3monitor.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@SuppressWarnings("ReactiveStreamsUnusedPublisher")
class TokenServiceTest
{
	private TokenRepository tokenRepositoryMock;
	private UserService userServiceMock;

	private TokenService tokenService;

	@BeforeEach
	public void setUp()
	{
		tokenRepositoryMock = mock(TokenRepository.class);
		userServiceMock = mock(UserService.class);

		tokenService = new TokenService(tokenRepositoryMock, userServiceMock);
	}

	@Test
	void removeExpiredTokensFromDatabase_removesExpiredTokensFromDatabase()
	{
		TokenEntity expiredToken1 = new TokenEntity("token_1", "user1", LocalDateTime.now().minusMinutes(2), false);
		TokenEntity expiredToken2 = new TokenEntity("token_2", "user1", LocalDateTime.now().minusMinutes(43), false);
		TokenEntity activeToken = new TokenEntity("token_3", "user2", LocalDateTime.now().plusMinutes(40), false);

		when(tokenRepositoryMock.findAll()).thenReturn(Flux.just(
				expiredToken1, expiredToken2, activeToken
		));

		when(tokenRepositoryMock.delete(any())).thenReturn(Mono.empty());

		Mono<Void> result = tokenService.removeExpiredTokensFromDatabase();

		StepVerifier.create(result)
				.expectComplete()
				.verify();

		verify(tokenRepositoryMock).delete(expiredToken1);
		verify(tokenRepositoryMock).delete(expiredToken2);
	}

	@Test
	void createToken_createsTokenForGivenUserAndExpiry()
	{
		when(tokenRepositoryMock.save(any())).thenAnswer(i -> Mono.just((TokenEntity)i.getArgument(0)));

		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("user");

		LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

		Mono<String> token = tokenService.createToken(userEntity, expiry);

		StepVerifier.create(token)
				.expectNextMatches(t -> t.length() == 24)
				.expectComplete()
				.verify();

		verify(tokenRepositoryMock).save(argThat((tokenEntity -> tokenEntity.getOwner().equals("user")
				&& tokenEntity.getExpiry().equals(expiry))));
	}

	@Test
	void validateTokenAndReturnUserInternal_returnsUser_forValidToken()
	{
		when(tokenRepositoryMock.findById("token_1")).thenReturn(Mono.just(
				new TokenEntity("token_1", "user1", LocalDateTime.now().plusMinutes(20), false)
		));

		UserEntity userEntity = new UserEntity();
		userEntity.setUsername("user");

		when(userServiceMock.getUserInternal("user1")).thenReturn(Mono.just(userEntity));

		Mono<UserEntity> token = tokenService.validateTokenAndReturnUserInternal("token_1");

		StepVerifier.create(token)
				.expectNext(userEntity)
				.expectComplete()
				.verify();
	}

	@Test
	void validateTokenAndReturnUserInternal_returnsError_forExpiredToken()
	{
		when(tokenRepositoryMock.findById("token_1")).thenReturn(Mono.just(
				new TokenEntity("token_1", "user1", LocalDateTime.now().minusMinutes(20), false)
		));

		Mono<UserEntity> token = tokenService.validateTokenAndReturnUserInternal("token_1");

		StepVerifier.create(token)
				.expectError(BadCredentialsException.class)
				.verify();
	}

	@Test
	void validateTokenAndReturnUserInternal_returnsError_forInvalidToken()
	{
		when(tokenRepositoryMock.findById("token_1")).thenReturn(Mono.empty());

		Mono<UserEntity> token = tokenService.validateTokenAndReturnUserInternal("token_1");

		StepVerifier.create(token)
				.expectError(BadCredentialsException.class)
				.verify();
	}
}
