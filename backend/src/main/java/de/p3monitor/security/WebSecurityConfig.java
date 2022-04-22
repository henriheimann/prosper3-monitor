package de.p3monitor.security;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@AllArgsConstructor
@EnableConfigurationProperties({CorsProperties.class})
public class WebSecurityConfig
{
	private final CustomAuthenticationManager authenticationManager;
	private final CustomAuthenticationConverter authenticationConverter;
	private final CorsProperties corsProperties;

	@Bean
	public AuthenticationWebFilter authenticationWebFilter()
	{
		AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
		authenticationWebFilter.setServerAuthenticationConverter(authenticationConverter);
		return authenticationWebFilter;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedOrigins(List.of(corsProperties.getAllowedOrigin()));
		corsConfig.addAllowedMethod("POST");
		corsConfig.addAllowedMethod("GET");
		corsConfig.addAllowedMethod("OPTIONS");
		corsConfig.addAllowedMethod("DELETE");
		corsConfig.addAllowedMethod("PUT");
		corsConfig.setMaxAge(3600L);
		corsConfig.setAllowedHeaders(List.of("x-requested-with", "authorization", "content-type"));
		corsConfig.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http)
	{
		return http
				.csrf().disable()
				.cors().configurationSource(corsConfigurationSource())
				.and().headers().contentSecurityPolicy("frame-ancestors 'self' " + corsProperties.getAllowedOrigin()).and().and()
				.addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
				.authorizeExchange()
				.pathMatchers("/auth/login").permitAll()
				.pathMatchers(HttpMethod.GET, "/devices").permitAll()
				.pathMatchers(HttpMethod.POST, "/measurements/**").permitAll()
				.pathMatchers(HttpMethod.POST, "/weather/**").permitAll()
				.pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**").permitAll()
				.pathMatchers(HttpMethod.OPTIONS).permitAll()
				.anyExchange().authenticated()
				.and()
				.httpBasic().disable()
				.formLogin().disable()
				.build();
	}
}
