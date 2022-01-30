package de.p3monitor.user.dtos;

import de.p3monitor.user.UserRole;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateUserRequest
{
	@NotNull
	private UserRole role;
}
