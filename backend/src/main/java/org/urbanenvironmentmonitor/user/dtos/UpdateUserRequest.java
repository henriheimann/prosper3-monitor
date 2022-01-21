package org.urbanenvironmentmonitor.user.dtos;

import lombok.Data;
import org.urbanenvironmentmonitor.user.UserRole;

import javax.validation.constraints.NotNull;

@Data
public class UpdateUserRequest
{
	@NotNull
	private UserRole role;
}
