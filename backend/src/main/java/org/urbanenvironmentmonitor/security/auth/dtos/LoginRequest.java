package org.urbanenvironmentmonitor.security.auth.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class LoginRequest
{
	@NotEmpty
	private String username;

	@NotEmpty
	private String password;
}
