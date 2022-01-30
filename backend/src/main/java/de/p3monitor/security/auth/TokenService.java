package de.p3monitor.security.auth;

import de.p3monitor.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import de.p3monitor.user.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@AllArgsConstructor
public class TokenService
{
	private final TokenRepository tokenRepository;
	private final UserService userService;

	private final SecureRandom secureRandom = new SecureRandom();
	private final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

	@Transactional
	public Mono<Void> removeExpiredTokensFromDatabase()
	{
		return tokenRepository.findAll()
				.filter(tokenEntity -> tokenEntity.getExpiry().isBefore(LocalDateTime.now()))
				.collectList()
				.doOnNext(tokenEntities -> log.info("Removing {} expired tokens from database", tokenEntities.size()))
				.flatMapMany(Flux::fromIterable)
				.flatMap(tokenRepository::delete)
				.then();
	}

	@Transactional
	public Mono<String> createToken(UserEntity userEntity, LocalDateTime expiry)
	{
		byte[] randomBytes = new byte[18];
		secureRandom.nextBytes(randomBytes);
		String token = base64Encoder.encodeToString(randomBytes);

		return tokenRepository.save(new TokenEntity(token, userEntity.getUsername(), expiry, true))
				.map(TokenEntity::getToken);
	}

	@Transactional
	public Mono<UserEntity> validateTokenAndReturnUserInternal(String token)
	{
		return tokenRepository.findById(token)
				.filter(tokenEntity -> tokenEntity.getExpiry().isAfter(LocalDateTime.now()))
				.flatMap(tokenEntity -> userService.getUserInternal(tokenEntity.getOwner()))
				.switchIfEmpty(Mono.error(new BadCredentialsException("Invalid or expired token")));
	}

	@Transactional
	public Mono<Void> deleteToken(String token)
	{
		return tokenRepository.findById(token)
				.flatMap(tokenRepository::delete);
	}
}
