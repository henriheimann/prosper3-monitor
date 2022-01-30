package de.p3monitor.security.auth;

import com.jayway.jsonpath.JsonPath;
import de.p3monitor.shared.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

class AuthControllerIntegrationTest extends AbstractIntegrationTest
{
	@Test
	void login_returnsToken_forValidLogin()
	{
		post("/auth/login", null, """
				{
					"username": "admin",
					"password": "password"
				}
				""")
				.expectStatus()
				.isOk()
				.expectBody()
				.jsonPath("$.token").isNotEmpty();
	}

	@Test
	void login_returnsUnauthorized_forInvalidLogin()
	{
		post("/auth/login", null, """
				{
					"username": "admin",
					"password": "wrong_password"
				}
				""")
				.expectStatus()
				.isUnauthorized();
	}

	@Test
	void login_returnsBadRequest_forInvalidJson()
	{
		post("/auth/login", null, """
				{
					"username": "admin"
				}
				""")
				.expectStatus()
				.isBadRequest();
	}

	@Test
	void logout_returnsOkAndLogsOutUser_forValidToken()
	{
		String response = post("/auth/login", null, """
				{
					"username": "admin",
					"password": "password"
				}
				""")
				.expectStatus()
				.isOk()
				.returnResult(String.class)
				.getResponseBody()
				.blockFirst();

		String token = JsonPath.read(response, "$.token");

		post("/auth/logout", token, null)
				.expectStatus()
				.isOk();

		post("/auth/logout", token, null)
				.expectStatus()
				.isUnauthorized();
	}

	@Test
	void logout_returnsUnauthorized_forMissingToken()
	{
		post("/auth/logout", null, null)
				.expectStatus()
				.isUnauthorized();
	}

	@Test
	void logout_returnsUnauthorized_forInvalidToken()
	{
		post("/auth/logout", "invalid_token", null)
				.expectStatus()
				.isUnauthorized();
	}
}