package org.urbanenvironmentmonitor.security.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.urbanenvironmentmonitor.user.UserEntity;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table("tokens")
public class TokenEntity implements Persistable<String>
{
	@Id
	private String token;

	private String owner;

	private LocalDateTime expiry;

	@Transient
	private boolean isNew;

	@Override
	public String getId()
	{
		return token;
	}

	@Override
	public boolean isNew()
	{
		return Objects.isNull(token) || isNew;
	}
}
