package de.p3monitor.users.dtos;

import lombok.Data;
import de.p3monitor.users.UserEntity;
import de.p3monitor.users.UserRole;

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
