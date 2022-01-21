package org.urbanenvironmentmonitor.user;

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

	@GetMapping("")
	public Flux<UserResponse> getAllUsers()
	{
		return userService.getAllUsers();
	}

	@PostMapping("")
	public Mono<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest)
	{
		return userService.createUser(createUserRequest);
	}

	@GetMapping("/{username}")
	public Mono<UserResponse> getUser(@PathVariable String username)
	{
		return userService.getUser(username);
	}

	@DeleteMapping("/{username}")
	public Mono<Void> deleteUser(@PathVariable String username)
	{
		return userService.deleteUser(username);
	}

	@PutMapping("/{username}")
	public Mono<UserResponse> updateUser(@PathVariable String username,
	                                     @Valid @RequestBody UpdateUserRequest updateUserRequest)
	{
		return userService.updateUser(username, updateUserRequest);
	}

	@PutMapping("/{username}/password")
	public Mono<Void> updateUserPassword(@PathVariable String username,
	                                     @Valid @RequestBody UpdateUserPasswordRequest updateUserPasswordRequest)
	{
		return userService.updateUserPassword(username, updateUserPasswordRequest);
	}
}
