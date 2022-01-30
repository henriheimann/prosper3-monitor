package de.p3monitor.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomAuthenticationConverter implements ServerAuthenticationConverter
{
	@Override
	public Mono<Authentication> convert(ServerWebExchange serverWebExchange)
	{
		return Mono.justOrEmpty(serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
				.filter(authHeader -> authHeader.startsWith("Bearer "))
				.map(authHeader -> {
					String authToken = authHeader.substring(7);
					return new UsernamePasswordAuthenticationToken(null, authToken);
				});
	}
}
