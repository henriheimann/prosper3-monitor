package org.urbanenvironmentmonitor.user;

import org.junit.jupiter.api.Test;
import org.urbanenvironmentmonitor.shared.AbstractIntegrationTest;

class UserControllerIntegrationTest extends AbstractIntegrationTest
{
	@Test
	void getAllUsers_returnsAllUsers()
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
	void getAllUsers_returnsUnauthorized_forMissingToken()
	{
		get("/users", null)
				.expectStatus().isUnauthorized();
	}

	@Test
	void getAllUsers_returnsForbidden_forNonAdmin()
	{
		String userToken = getToken("user", "password");
		get("/users", userToken)
				.expectStatus().isForbidden();
	}

	@Test
	void createUser_returnsTheCreatedUser()
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
	void createUser_returnsUnauthorized_forMissingToken()
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
	void createUser_returnsForbidden_forNonAdmins()
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
	void createUser_returnsBadRequest_forInvalidJson()
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
	void createUser_returnsBadRequest_forInvalidlyFormattedJson()
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
	void getUser_returnsUser_forAdmin()
	{
		String adminToken = getToken("admin", "password");

		get("/users/user", adminToken)
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.username").isEqualTo("user")
				.jsonPath("$.role").isEqualTo("USER");
	}

	@Test
	void getUser_returnsUser_forUserHimself()
	{
		String userToken = getToken("user", "password");

		get("/users/user", userToken)
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.username").isEqualTo("user")
				.jsonPath("$.role").isEqualTo("USER");
	}

	@Test
	void getUser_returnsForbidden_forOtherUsers()
	{
		String userToken = getToken("user", "password");

		get("/users/admin", userToken)
				.expectStatus().isForbidden();
	}

	@Test
	void getUser_returnsNotFound_forInvalidUserAsAdmin()
	{
		String adminToken = getToken("admin", "password");

		get("/users/non_existent_user", adminToken)
				.expectStatus().isNotFound();
	}

	@Test
	void getUser_returnsForbidden_forInvalidUserAsUser()
	{
		String userToken = getToken("user", "password");

		get("/users/non_existent_user", userToken)
				.expectStatus().isForbidden();
	}

	@Test
	void deleteUser_returnsOkAndDeletesUser()
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
	void deleteUser_returnsUnauthorized_forMissingToken()
	{
		delete("/users/admin", null)
				.expectStatus().isUnauthorized();
	}

	@Test
	void deleteUser_returnsForbidden_forNonAdmins()
	{
		String userToken = getToken("user", "password");
		delete("/users/user", userToken)
				.expectStatus().isForbidden();
	}

	@Test
	void updateUser_returnsUpdatedUser()
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
	void updateUser_returnsUnauthorized_forMissingToken()
	{
		put("/users/user", null, """
				{
					"role": "ADMIN"
				}
				""")
				.expectStatus().isUnauthorized();
	}

	@Test
	void updateUser_returnsForbidden_forNonAdmins()
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
	void updateUser_returnsBadRequest_forInvalidJson()
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
	void updateUserPassword_returnsOkAndUpdatesUserPassword()
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
	void updateUserPassword_returnsUnauthorized_forWrongOldPassword()
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
	void updateUserPassword_returnsUnauthorized_forMissingToken()
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
	void updateUserPassword_returnsForbidden_forDifferentUser()
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
	void updateUserPassword_returnsBadRequest_forInvalidJson()
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
