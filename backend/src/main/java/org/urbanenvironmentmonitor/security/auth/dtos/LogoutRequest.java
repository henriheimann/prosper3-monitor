package org.urbanenvironmentmonitor.security.auth.dtos;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LogoutRequest
{
	@NotEmpty
	private final String token;
}
