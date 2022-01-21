package org.urbanenvironmentmonitor.user.dtos;

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
