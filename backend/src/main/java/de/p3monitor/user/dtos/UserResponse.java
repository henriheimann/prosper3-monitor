package de.p3monitor.user.dtos;

import lombok.Data;
import de.p3monitor.user.UserEntity;
import de.p3monitor.user.UserRole;

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
