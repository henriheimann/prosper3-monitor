package de.p3monitor.users.dtos;

import de.p3monitor.users.UserRole;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateUserRequest
{
	@NotNull
	private UserRole role;
}
