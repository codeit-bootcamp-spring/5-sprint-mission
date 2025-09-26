package com.sprint.mission.discodeit.slice.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositorySliceTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	private User createUser(String username, String email, String password) {
		return new User(username, email, password, null);
	}

	private User createUserWithBinaryContentAndStatus(String username, String email, String password){
		BinaryContent binaryContent = new BinaryContent("testFileName1", (long)20, "testContent1");
		User user = new User(username, email, password, binaryContent);
		UserStatus userStatus = new UserStatus(user, Instant.now());
		return user;
	}


	@Test
	@DisplayName("UserRepository Slice Test - findByUsername - 성공")
	public void findByUsername() {
		//
		userRepository.save(createUser("username", "email", "password"));
		testEntityManager.flush();
		testEntityManager.clear();
		//
		Optional<User> foundUser = userRepository.findByUsername("username");
		//
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getUsername()).isEqualTo("username");
		assertThat(foundUser.get().getEmail()).isEqualTo("email");

	}


	@Test
	@DisplayName("UserRepository Slice Test - findByUsername - 실패")
	public void findByUsernameFail() {
		Optional<User> username = userRepository.findByUsername("username");
		assertThat(username).isEmpty();
	}

	@Test
	@DisplayName("UserRepository Slice Test - findAllWithProfileAndStatus - 성공")
	public void findAllWithProfileAndStatus() {
		userRepository.save(createUserWithBinaryContentAndStatus("username1", "email1", "password1"));
		userRepository.save(createUserWithBinaryContentAndStatus("username2", "email2", "password2"));
		testEntityManager.flush();
		testEntityManager.clear();
		//
		List<User> allWithProfileAndStatus = userRepository.findAllWithProfileAndStatus();
		//
		assertThat(allWithProfileAndStatus.size()).isEqualTo(2);
		for(User u : allWithProfileAndStatus) {
			assertThat(u.getUsername()).isIn("username1", "username2");
			assertThat(u.getStatus().getUser().getUsername()).isIn("username1", "username2");
			assertThat(u.getProfile().getFileName()).isEqualTo("testFileName1");
		}

	}

	@Test
	@DisplayName("UserRepository Slice Test - findAllWithProfileAndStatus - 실패(하나는 UserStatus가 없음)")
	public void findAllWithProfileAndStatusFail() {
		//
		BinaryContent binaryContent = new BinaryContent("testFileName1", (long)20, "testContent1");
		User userWithoutStatus = new User("usernameNoStatus", "emailNoStatus", "passwordNoStatus", binaryContent);
		// UserStatus를 생성하지 않음

		userRepository.save(userWithoutStatus);
		User normalUser = createUserWithBinaryContentAndStatus("normalUser", "normalEmail", "normalPassword");
		userRepository.save(normalUser);

		testEntityManager.flush();
		testEntityManager.clear();

		//
		List<User> allWithProfileAndStatus = userRepository.findAllWithProfileAndStatus();

		// status가 없는 user는 조회되지 않음
		assertThat(allWithProfileAndStatus).hasSize(1); // status가 없는 user는 제외됨
		assertThat(allWithProfileAndStatus.get(0).getUsername()).isEqualTo("normalUser");

		boolean containsUserWithoutStatus = allWithProfileAndStatus.stream()
			.anyMatch(user -> "usernameNoStatus".equals(user.getUsername()));
		assertThat(containsUserWithoutStatus).isFalse();
	}


}
