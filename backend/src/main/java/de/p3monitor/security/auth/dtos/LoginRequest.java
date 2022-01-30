package de.p3monitor.security.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest
{
	@NotEmpty
	private String username;

	@NotEmpty
	private String password;
}
