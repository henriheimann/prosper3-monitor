package de.p3monitor.shared.logging;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingWebFilter implements WebFilter
{
	@NotNull
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain)
	{
		log.info("{} {}", exchange.getRequest().getMethod(), exchange.getRequest().getPath());
		return chain.filter(exchange);
	}
}
