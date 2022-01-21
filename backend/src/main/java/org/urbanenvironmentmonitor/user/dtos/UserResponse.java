package org.urbanenvironmentmonitor.user.dtos;

import lombok.Data;
import org.urbanenvironmentmonitor.user.UserEntity;
import org.urbanenvironmentmonitor.user.UserRole;

@Data
public class UserResponse
{
	private String username;
	private UserRole role;

	public UserResponse(UserEntity userEntity)
	{
		username = userEntity.getUsername();
		role = userEntity.getRole();
	}
}
