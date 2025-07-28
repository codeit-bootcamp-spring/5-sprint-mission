package BasicUnitTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DuplicateLoginIdException;
import com.sprint.mission.discodeit.exception.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

@DisplayName("BasicUserService 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicUserServiceTest {

	private UserRepository userRepository;
	private BasicUserService userService;
	private User testUser;

	@BeforeEach
	void setUp() {
		cleanDirectory(new File("data/"));

		userRepository = new FileUserRepository();
		userService = new BasicUserService(userRepository);
		testUser = new User("test1", "password1", "홍길동");
	}

	private void cleanDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						cleanDirectory(file);
					} else {
						file.delete();
					}
				}
			}
			directory.delete();
		}
	}

	@Test
	@Order(1)
	@DisplayName("1. Basic CreateUser")
	void testCreateUser() {
		// given

		// when
		User result = userService.createUser("test1", "password1", "홍길동");

		// then
		assertThat(result).isNotNull();
		assertThat(result.getLoginId()).isEqualTo("test1");
		assertThat(result.getPassword()).isEqualTo("password1");
		assertThat(result.getDefaultNickname()).isEqualTo("홍길동");

		User saved = userRepository.findById(result.getId());
		assertThat(saved).isNotNull();
		assertThat(saved.getLoginId()).isEqualTo("test1");
	}

	@Test
	@Order(2)
	@DisplayName("2. Basic CreateUser 중복 예외")
	void testCreateUser_DuplicateLoginId() {
		// given
		userService.createUser("test1", "password1", "홍길동");

		// when

		// then
		assertThatThrownBy(() -> userService.createUser("test1", "password2", "김길동"))
			.isInstanceOf(DuplicateLoginIdException.class);
	}

	@Test
	@Order(3)
	@DisplayName("3. Basic Login")
	void testLogin() {
		// given
		userService.createUser("test1", "password1", "홍길동");

		// when
		User result = userService.login("test1", "password1");

		// then
		assertThat(result).isNotNull();
		assertThat(result.getLoginId()).isEqualTo("test1");
		assertThat(result.getDefaultNickname()).isEqualTo("홍길동");
	}

	@Test
	@Order(4)
	@DisplayName("4. Basic UserLogin 사용자 예외")
	void testLogin_UserNotFound() {
		// given

		// when

		// then
		assertThatThrownBy(() -> userService.login("nonexistent", "password1"))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(5)
	@DisplayName("5. Basic UserLogin 비밀번호 예외")
	void testLogin_InvalidPassword() {
		// given
		userService.createUser("test1", "password1", "홍길동");

		// when

		// then
		assertThatThrownBy(() -> userService.login("test1", "wrongpassword"))
			.isInstanceOf(InvalidPasswordException.class);
	}

	@Test
	@Order(6)
	@DisplayName("6. Basic UserGetById")
	void testGetUserById() {
		// given
		User created = userService.createUser("test1", "password1", "홍길동");

		// when
		User result = userService.getUserById(created.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(created.getId());
		assertThat(result.getLoginId()).isEqualTo("test1");
		assertThat(result.getDefaultNickname()).isEqualTo("홍길동");
	}

	@Test
	@Order(7)
	@DisplayName("7. Basic UserGetById 사용자 예외")
	void testGetUserById_UserNotFound() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> userService.getUserById(nonExistentId))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(8)
	@DisplayName("8. Basic UserGetByLoginId")
	void testGetUserByLoginId() {
		// given
		userService.createUser("test1", "password1", "홍길동");

		// when
		User result = userService.getUserByLoginId("test1");

		// then
		assertThat(result).isNotNull();
		assertThat(result.getLoginId()).isEqualTo("test1");
		assertThat(result.getDefaultNickname()).isEqualTo("홍길동");
	}

	@Test
	@Order(9)
	@DisplayName("9. Basic UserGetByLoginId 사용자 예외")
	void testGetUserByLoginId_UserNotFound() {
		// given

		// when

		// then
		assertThatThrownBy(() -> userService.getUserByLoginId("nonexistent"))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(10)
	@DisplayName("10. Basic UserGetAll")
	void testGetAllUsers() {
		// given
		userService.createUser("test1", "password1", "홍길동");
		userService.createUser("test2", "password2", "김길동");
		userService.createUser("test3", "password3", "박길동");

		// when
		List<User> result = userService.getAllUsers();

		// then
		assertThat(result)
			.isNotNull()
			.hasSize(3);
		assertThat(result)
			.extracting(User::getLoginId)
			.containsExactlyInAnyOrder("test1", "test2", "test3");
	}

	@Test
	@Order(11)
	@DisplayName("11. Basic UserUpdatePassword")
	void testUpdateUserPassword() {
		// given
		User created = userService.createUser("test1", "password1", "홍길동");

		// when
		boolean result = userService.updateUserPassword(created.getId(), "newpassword");

		// then
		assertThat(result).isTrue();

		User updated = userRepository.findById(created.getId());
		assertThat(updated.getPassword()).isEqualTo("newpassword");

		User loginResult = userService.login("test1", "newpassword");
		assertThat(loginResult).isNotNull();
	}

	@Test
	@Order(12)
	@DisplayName("12. Basic UserUpdatePassword 사용자 예외")
	void testUpdateUserPassword_UserNotFound() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> userService.updateUserPassword(nonExistentId, "newpassword"))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(13)
	@DisplayName("13. Basic UserDeleteById")
	void testDeleteUserById() {
		// given
		User created = userService.createUser("test1", "password1", "홍길동");

		// when
		boolean result = userService.deleteUser(created.getId());

		// then
		assertThat(result).isTrue();

		assertThatThrownBy(() -> userService.getUserById(created.getId()))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(14)
	@DisplayName("14. Basic UserDeleteById 사용자 예외")
	void testDeleteUserById_UserNotFound() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> userService.deleteUser(nonExistentId))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(15)
	@DisplayName("15. Basic UserDeleteByLoginId")
	void testDeleteUserByLoginId() {
		// given
		userService.createUser("test1", "password1", "홍길동");

		// when
		boolean result = userService.deleteUser("test1");

		// then
		assertThat(result).isTrue();

		assertThatThrownBy(() -> userService.getUserByLoginId("test1"))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(16)
	@DisplayName("16. Basic UserDeleteByLoginId 사용자 예외")
	void testDeleteUserByLoginId_UserNotFound() {
		// given

		// when

		// then
		assertThatThrownBy(() -> userService.deleteUser("nonexistent"))
			.isInstanceOf(UserNotFoundException.class);
	}
}