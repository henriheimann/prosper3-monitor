package org.urbanenvironmentmonitor.security.auth;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TokenRepository extends ReactiveCrudRepository<TokenEntity, String>
{
}
