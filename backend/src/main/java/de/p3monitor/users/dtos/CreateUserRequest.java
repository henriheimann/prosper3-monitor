package de.p3monitor.users.dtos;

import lombok.Data;
import de.p3monitor.users.UserRole;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateUserRequest
{
	@NotBlank(message = "username_cannot_be_blank")
	private String username;

	@NotEmpty(message = "password_cannot_be_empty")
	private String password;

	@NotNull(message = "user_role_cannot_be_null")
	private UserRole role;
}
