package de.p3monitor.users.dtos;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateUserPasswordRequest
{
	@NotEmpty
	private String oldPassword;

	@NotEmpty
	private String newPassword;
}
