package de.p3monitor.security.auth.dtos;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LogoutRequest
{
	@NotEmpty
	private final String token;
}
