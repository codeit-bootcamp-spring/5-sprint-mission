package BasicUnitTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.util.List;
import java.util.Optional;
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
	void User_생성시_성공() {
		// given

		// when
		User result = userService.createUser("test1", "password1", "홍길동");

		// then
		assertThat(result).isNotNull();
		assertThat(result.getLoginId()).isEqualTo("test1");
		assertThat(result.getPassword()).isEqualTo("password1");
		assertThat(result.getDefaultNickname()).isEqualTo("홍길동");

		Optional<User> savedOpt = userRepository.findById(result.getId());
		assertThat(savedOpt).isPresent();
		User saved = savedOpt.get();
		assertThat(saved.getLoginId()).isEqualTo("test1");
	}

	@Test
	@Order(2)
	void User_중복된_loginId로_생성시_실패() {
		// given
		userService.createUser("test1", "password1", "홍길동");

		// when

		// then
		assertThatThrownBy(() -> userService.createUser("test1", "password2", "김길동"))
			.isInstanceOf(DuplicateLoginIdException.class);
	}

	@Test
	@Order(3)
	void 로그인_성공() {
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
	void 없는아이디_로그인시_실패() {
		// given

		// when

		// then
		assertThatThrownBy(() -> userService.login("없는아이디", "password1"))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(5)
	void 잘못된_비밀번호_로그인시_실패() {
		// given
		userService.createUser("test1", "password1", "홍길동");

		// when

		// then
		assertThatThrownBy(() -> userService.login("test1", "wrongpassword"))
			.isInstanceOf(InvalidPasswordException.class);
	}

	@Test
	@Order(6)
	void User_UUDID로_User_조회시_성공() {
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
	void 잘못된_UUID로_조회시_실패() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> userService.getUserById(nonExistentId))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(8)
	void LoginID로_조회시_성공() {
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
	void 잘못된_LoginID로_조회시_실패() {
		// given

		// when

		// then
		assertThatThrownBy(() -> userService.getUserByLoginId("nonexistent"))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(10)
	void 모든_User_조회시_성공() {
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
	void User_비밀번호_업데이트시_성공() {
		// given
		User created = userService.createUser("test1", "password1", "홍길동");

		// when
		boolean result = userService.updateUserPassword(created.getId(), "newpassword");

		// then
		assertThat(result).isTrue();

		Optional<User> updatedOpt = userRepository.findById(created.getId());
		assertThat(updatedOpt).isPresent();
		User updated = updatedOpt.get();
		assertThat(updated.getPassword()).isEqualTo("newpassword");

		User loginResult = userService.login("test1", "newpassword");
		assertThat(loginResult).isNotNull();
	}

	@Test
	@Order(12)
	void 잘못된_UUID로_비밀번호_업데이트시_실패() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> userService.updateUserPassword(nonExistentId, "newpassword"))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(13)
	void Id로_삭제시_성공() {
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
	void 잘못된_Id로_삭제시_실패() {
		// given
		UUID nonExistentId = UUID.randomUUID();

		// when

		// then
		assertThatThrownBy(() -> userService.deleteUser(nonExistentId))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@Order(15)
	void loginId로_삭제시_성공() {
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
	void 잘못된_id로_삭제시_실패() {
		// given

		// when

		// then
		assertThatThrownBy(() -> userService.deleteUser("없는사용자"))
			.isInstanceOf(UserNotFoundException.class);
	}
}