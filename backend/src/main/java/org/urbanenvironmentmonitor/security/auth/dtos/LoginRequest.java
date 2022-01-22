package org.urbanenvironmentmonitor.security.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class LoginRequest
{
	@NotEmpty
	private String username;

	@NotEmpty
	private String password;
}
