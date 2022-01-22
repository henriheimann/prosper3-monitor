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
	void getAllUsersReturnsForbiddenAsUser()
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
	void createUserReturnsForbiddenAsUser()
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

	@Test
	void getUserReturnsStartupUserAsAdmin()
	{
		String adminToken = getToken("admin", "password");

		get("/users/user", adminToken)
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.username").isEqualTo("user")
				.jsonPath("$.role").isEqualTo("USER");
	}

	@Test
	void getUserReturnsStartupUserAsUser()
	{
		String userToken = getToken("user", "password");

		get("/users/user", userToken)
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.username").isEqualTo("user")
				.jsonPath("$.role").isEqualTo("USER");
	}

	@Test
	void getUserReturnsForbiddenForOtherUserAsStartupUser()
	{
		String userToken = getToken("user", "password");

		get("/users/admin", userToken)
				.expectStatus().isForbidden();
	}

	@Test
	void getUserReturnsNotFoundForInvalidUserAsStartupAdmin()
	{
		String adminToken = getToken("admin", "password");

		get("/users/non_existent_user", adminToken)
				.expectStatus().isNotFound();
	}

	@Test
	void getUserReturnsForbiddenForInvalidUserAsStartupAdmin()
	{
		String userToken = getToken("user", "password");

		get("/users/non_existent_user", userToken)
				.expectStatus().isForbidden();
	}

	@Test
	void deleteUserReturnsOkAndDeletesUser()
	{
		String adminToken = getToken("admin", "password");

		post("/users", adminToken, """
				{
					"username": "new_user1",
					"password": "test_password",
					"role": "USER"
				}
				""")
				.expectStatus().isOk();

		delete("/users/new_user1", adminToken)
				.expectStatus().isOk();

		get("/users/new_user1", adminToken)
				.expectStatus().isNotFound();
	}

	@Test
	void deleteUserReturnsUnauthorizedForMissingToken()
	{
		delete("/users/admin", null)
				.expectStatus().isUnauthorized();
	}

	@Test
	void deleteUserReturnsForbiddenAsStartupUser()
	{
		String userToken = getToken("user", "password");
		delete("/users/user", userToken)
				.expectStatus().isForbidden();
	}

	@Test
	void updateUserReturnsUpdatedUser()
	{
		String adminToken = getToken("admin", "password");
		put("/users/user", adminToken, """
				{
					"role": "ADMIN"
				}
				""")
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.username").isEqualTo("user")
				.jsonPath("$.role").isEqualTo("ADMIN");
	}

	@Test
	void updateUserReturnsUnauthorizedForMissingToken()
	{
		put("/users/user", null, """
				{
					"role": "ADMIN"
				}
				""")
				.expectStatus().isUnauthorized();
	}

	@Test
	void updateUserReturnsForbiddenAsStartupUser()
	{
		String userToken = getToken("user", "password");
		put("/users/user", userToken, """
				{
					"role": "ADMIN"
				}
				""")
				.expectStatus().isForbidden();
	}

	@Test
	void updateUserReturnsBadRequestForInvalidJson()
	{
		String adminToken = getToken("admin", "password");
		put("/users/user", adminToken, """
				{
					"test": "none"
				}
				""")
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo(400)
				.jsonPath("$.errors[?(@.messageCode=='bad_request')]").exists();
	}

	@Test
	void updateUserPasswordReturnsOkAndUpdatesUserPassword()
	{
		String userToken = getToken("user", "password");
		put("/users/user/password", userToken, """
				{
					"oldPassword": "password",
					"newPassword": "new_password"
				}
				""")
				.expectStatus().isOk();

		post("/auth/login", null, """
				{
					"username": "user",
					"password": "password"
				}
				""")
				.expectStatus().isUnauthorized();

		post("/auth/login", null, """
				{
					"username": "user",
					"password": "new_password"
				}
				""")
				.expectStatus().isOk();
	}

	@Test
	void updateUserPasswordReturnsUnauthorizedForWrongOldPassword()
	{
		String userToken = getToken("user", "password");
		put("/users/user/password", userToken, """
				{
					"oldPassword": "wrong_old_password",
					"newPassword": "new_password"
				}
				""")
				.expectStatus().isUnauthorized();
	}

	@Test
	void updateUserPasswordReturnsUnauthorizedForMissingToken()
	{
		put("/users/user/password", null, """
				{
					"oldPassword": "password",
					"newPassword": "new_password"
				}
				""")
				.expectStatus().isUnauthorized();
	}

	@Test
	void updateUserPasswordReturnsForbiddenForDifferentUser()
	{
		String userToken = getToken("user", "password");
		put("/users/admin/password", userToken, """
				{
					"oldPassword": "password",
					"newPassword": "new_password"
				}
				""")
				.expectStatus().isForbidden();
	}

	@Test
	void updateUserPasswordReturnsBadRequestForInvalidJson()
	{
		String userToken = getToken("user", "password");
		put("/users/user/password", userToken, """
				{
					"oldPassword": "password",
					"newPassword": ""
				}
				""")
				.expectStatus().isBadRequest();
	}
}
