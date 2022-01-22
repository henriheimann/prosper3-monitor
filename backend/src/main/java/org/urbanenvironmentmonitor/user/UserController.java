package org.urbanenvironmentmonitor.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.urbanenvironmentmonitor.user.dtos.CreateUserRequest;
import org.urbanenvironmentmonitor.user.dtos.UpdateUserPasswordRequest;
import org.urbanenvironmentmonitor.user.dtos.UpdateUserRequest;
import org.urbanenvironmentmonitor.user.dtos.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController
{
	private final UserService userService;

	public UserController(UserService userService)
	{
		this.userService = userService;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("")
	public Flux<UserResponse> getAllUsers()
	{
		return userService.getAllUsers();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("")
	public Mono<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest)
	{
		return userService.createUser(createUserRequest);
	}

	@PreAuthorize("hasRole('ADMIN') || #username == authentication.name")
	@GetMapping("/{username}")
	public Mono<UserResponse> getUser(@PathVariable String username)
	{
		return userService.getUser(username);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{username}")
	public Mono<Void> deleteUser(@PathVariable String username)
	{
		return userService.deleteUser(username);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{username}")
	public Mono<UserResponse> updateUser(@PathVariable String username,
	                                     @Valid @RequestBody UpdateUserRequest updateUserRequest)
	{
		return userService.updateUser(username, updateUserRequest);
	}

	@PreAuthorize("#username == authentication.name")
	@PutMapping("/{username}/password")
	public Mono<Void> updateUserPassword(@PathVariable String username,
	                                     @Valid @RequestBody UpdateUserPasswordRequest updateUserPasswordRequest)
	{
		return userService.updateUserPassword(username, updateUserPasswordRequest);
	}
}
