package org.urbanenvironmentmonitor.shared;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.urbanenvironmentmonitor.device.DeviceRepository;
import org.urbanenvironmentmonitor.security.auth.TokenRepository;
import org.urbanenvironmentmonitor.shared.startup.StartupService;
import org.urbanenvironmentmonitor.user.UserRepository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@ActiveProfiles(profiles = "test")
@SpringBootTest
public abstract class AbstractIntegrationTest
{
	protected WebTestClient client;

	@Autowired
	protected TokenRepository tokenRepository;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected DeviceRepository deviceRepository;

	@Autowired
	protected StartupService startupService;

	private final Map<String, String> tokenCache = new HashMap<>();

	@BeforeEach
	void setUp(ApplicationContext context)
	{
		client = WebTestClient.bindToApplicationContext(context).build();

		tokenRepository.deleteAll()
				.then(Mono.zip(userRepository.deleteAll(), deviceRepository.deleteAll()))
				.then(startupService.setupStartupUsers())
				.block();

		tokenCache.clear();
	}

	protected WebTestClient.ResponseSpec get(String uri, String token)
	{
		var uriSpec = client.get().uri(uri)
				.accept(MediaType.APPLICATION_JSON);

		if (token != null) {
			uriSpec = uriSpec.header("Authorization", "Bearer " + token);
		}

		return uriSpec.exchange();
	}

	protected WebTestClient.ResponseSpec post(String uri, String token, String body)
	{
		var uriSpec = client.post().uri(uri)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(body);

		if (token != null) {
			uriSpec = uriSpec.header("Authorization", "Bearer " + token);
		}

		return uriSpec.exchange();
	}

	protected WebTestClient.ResponseSpec delete(String uri, String token)
	{
		var uriSpec = client.delete().uri(uri);

		if (token != null) {
			uriSpec = uriSpec.header("Authorization", "Bearer " + token);
		}

		return uriSpec.exchange();
	}

	protected WebTestClient.ResponseSpec put(String uri, String token, String body)
	{
		var uriSpec = client.put().uri(uri)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(body);

		if (token != null) {
			uriSpec = uriSpec.header("Authorization", "Bearer " + token);
		}

		return uriSpec.exchange();
	}

	protected String getToken(String username, String password)
	{
		String cachedToken = tokenCache.get(username);

		if (cachedToken != null) {
			return cachedToken;
		}

		String response = post("/auth/login", null, """
				{
					"username": "%s",
					"password": "%s"
				}
				""".formatted(username, password))
				.expectStatus()
				.isOk()
				.returnResult(String.class)
				.getResponseBody()
				.blockFirst();

		String token = JsonPath.read(response, "$.token");
		tokenCache.put(username, token);

		return token;
	}
}
