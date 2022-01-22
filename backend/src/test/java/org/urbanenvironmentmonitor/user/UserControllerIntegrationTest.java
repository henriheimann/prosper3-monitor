package org.urbanenvironmentmonitor.user;

import org.junit.jupiter.api.Test;
import org.urbanenvironmentmonitor.shared.AbstractIntegrationTest;

class UserControllerIntegrationTest extends AbstractIntegrationTest
{
	@Test
	void getAllUsersReturnsStartupAndCreatedUsers()
	{
		String adminToken = getToken("admin", "password");

		post("/users", adminToken, """
				{
					"username": "new_user1",
					"password": "test_password",
					"role": "USER"
				}
				""")
				.expectStatus()
				.isOk();

		post("/users", adminToken, """
				{
					"username": "new_user2",
					"password": "test_password",
					"role": "USER"
				}
				""")
				.expectStatus()
				.isOk();

		get("/users", adminToken)
				.expectStatus()
				.isOk()
				.expectBody()
				.jsonPath("$.[?(@.username=='admin' && @.role=='ADMIN')]").exists()
				.jsonPath("$.[?(@.username=='new_user1' && @.role=='USER')]").exists()
				.jsonPath("$.[?(@.username=='new_user2' && @.role=='USER')]").exists();
	}

	@Test
	void getAllUsersReturnsUnauthorizedForMissingToken()
	{
		get("/users", null)
				.expectStatus().isUnauthorized();
	}

	@Test
	void getAllUsersReturnsForbiddenForUserRole()
	{
		String userToken = getToken("user", "password");
		get("/users", userToken)
				.expectStatus().isForbidden();
	}

	@Test
	void createUserReturnsTheCreatedUser()
	{
		String adminToken = getToken("admin", "password");
		post("/users", adminToken, """
				{
					"username": "new_admin",
					"password": "test_password",
					"role": "ADMIN"
				}
				""")
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.username").isEqualTo("new_admin")
				.jsonPath("$.role").isEqualTo("ADMIN");
	}


	@Test
	void createUserReturnsUnauthorizedForMissingToken()
	{
		post("/users", null, """
				{
					"username": "new_admin",
					"password": "test_password",
					"role": "ADMIN"
				}
				""")
				.expectStatus().isUnauthorized();
	}

	@Test
	void createUserReturnsForbiddenForUserRole()
	{
		String userToken = getToken("user", "password");
		post("/users", userToken, """
				{
					"username": "new_admin",
					"password": "test_password",
					"role": "ADMIN"
				}
				""")
				.expectStatus().isForbidden();
	}

	@Test
	void createUserReturnsBadRequestForInvalidJson()
	{
		String adminToken = getToken("admin", "password");
		post("/users", adminToken, """
				{
					"username": "",
					"password": ""
				}
				""")
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo(400)
				.jsonPath("$.errors[?(@.messageCode=='username_cannot_be_blank')]").exists()
				.jsonPath("$.errors[?(@.messageCode=='password_cannot_be_empty')]").exists()
				.jsonPath("$.errors[?(@.messageCode=='user_role_cannot_be_null')]").exists();
	}

	@Test
	void createUserReturnsBadRequestForInvalidlyFormattedJson()
	{
		String adminToken = getToken("admin", "password");
		post("/users", adminToken, """
				{
					"u}
				""")
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo(400)
				.jsonPath("$.errors[?(@.messageCode=='bad_request')]").exists();
	}


}
