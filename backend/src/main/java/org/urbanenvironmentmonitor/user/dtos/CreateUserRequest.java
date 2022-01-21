package org.urbanenvironmentmonitor.user.dtos;

import lombok.Data;
import org.urbanenvironmentmonitor.user.UserRole;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateUserRequest
{
	@NotBlank
	private String username;

	@NotEmpty
	private String password;

	@NotNull
	private UserRole role;
}
