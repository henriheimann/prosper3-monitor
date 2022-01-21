package org.urbanenvironmentmonitor.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@AllArgsConstructor
public class WebSecurityConfig
{
	private final CustomAuthenticationManager authenticationManager;
	private final CustomAuthenticationConverter authenticationConverter;

	@Bean
	public AuthenticationWebFilter authenticationWebFilter()
	{
		AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
		authenticationWebFilter.setServerAuthenticationConverter(authenticationConverter);
		return authenticationWebFilter;
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http)
	{
		return http
				.csrf().disable()
				.cors().and()
				.addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
				.authorizeExchange()
				.pathMatchers("/auth/login").permitAll()
				.anyExchange().authenticated()
				.and().build();
	}
}
