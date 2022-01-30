package de.p3monitor.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Table("users")
public class UserEntity implements Persistable<String>
{
	@Id
	private String username;

	private String password;

	private UserRole role;

	@Transient
	private boolean isNew;

	public UserEntity(String username, String password, UserRole role)
	{
		this.username = username;
		this.password = password;
		this.role = role;
		isNew = true;
	}

	@Override
	public String getId()
	{
		return username;
	}

	@Override
	public boolean isNew()
	{
		return Objects.isNull(username) || isNew;
	}
}
