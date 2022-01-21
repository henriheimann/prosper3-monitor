package org.urbanenvironmentmonitor.security.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse
{
	public String token;
}
