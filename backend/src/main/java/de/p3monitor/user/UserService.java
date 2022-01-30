package de.p3monitor.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import de.p3monitor.security.auth.exceptions.InvalidCredentialsException;
import de.p3monitor.shared.exceptions.ResourceNotFoundException;
import de.p3monitor.user.dtos.CreateUserRequest;
import de.p3monitor.user.dtos.UpdateUserPasswordRequest;
import de.p3monitor.user.dtos.UpdateUserRequest;
import de.p3monitor.user.dtos.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class UserService
{
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public Flux<UserResponse> getAllUsers()
	{
		return userRepository.findAll()
				.map(UserResponse::new);
	}

	public Mono<UserResponse> createUser(CreateUserRequest request)
	{
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		return Mono.just(new UserEntity(request.getUsername(), encodedPassword, request.getRole()))
				.flatMap(userRepository::save)
				.map(UserResponse::new);
	}

	public Mono<UserResponse> getUser(String username)
	{
		return Mono.just(username)
				.flatMap(userRepository::findById)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")))
				.map(UserResponse::new);
	}

	public Mono<Void> deleteUser(String username)
	{
		return Mono.just(username)
				.flatMap(userRepository::findById)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")))
				.flatMap(userRepository::delete);
	}

	public Mono<UserResponse> updateUser(String username, UpdateUserRequest request)
	{
		return Mono.just(username)
				.flatMap(userRepository::findById)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")))
				.doOnNext(userEntity -> {
					if (request.getRole() != null) {
						userEntity.setRole(request.getRole());
					}
				})
				.flatMap(userRepository::save)
				.map(UserResponse::new);
	}

	public Mono<Void> updateUserPassword(String username, UpdateUserPasswordRequest request)
	{
		String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

		return Mono.just(username)
				.flatMap(userRepository::findById)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")))
				.filter(userEntity -> passwordEncoder.matches(request.getOldPassword(), userEntity.getPassword()))
				.switchIfEmpty(Mono.error(new InvalidCredentialsException()))
				.doOnNext(userEntity -> userEntity.setPassword(encodedNewPassword))
				.flatMap(userRepository::save)
				.then();
	}

	public Mono<UserEntity> getUserInternal(String username)
	{
		return userRepository.findById(username);
	}

	public Mono<UserEntity> setupUserInternal(String username, String password, UserRole role)
	{
		String encodedPassword = passwordEncoder.encode(password);

		return userRepository.findById(username)
				.doOnNext(userEntity -> {
					userEntity.setRole(role);
					userEntity.setPassword(encodedPassword);
				})
				.switchIfEmpty(Mono.just(new UserEntity(username, encodedPassword, role)))
				.flatMap(userRepository::save);
	}
}
