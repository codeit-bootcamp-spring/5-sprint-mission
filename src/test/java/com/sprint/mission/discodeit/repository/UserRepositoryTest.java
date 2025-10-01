package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    private User createUser(String username, String email) {
        BinaryContent profile = new BinaryContent("profile.png", 100L, "image/png");
        User user = new User(username, email, "password1234", profile);
        UserStatus userStatus = new UserStatus(user, Instant.now());
        return userRepository.save(user);
    }

    @Test
    @DisplayName("username으로 존재하는 사용자 조회하면 Optional<User> 반환")
        // givenExistingUsername_whenFindByUsername_thenReturnOptionalUser
    void findByUsername_withExistingUsername_returnsOptionalUser() {
        // given
        User user = createUser("test01", "test01@email.com");
        em.flush();
        em.clear();

        // when
        Optional<User> result = userRepository.findByUsername("test01");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("test01");
    }

    @Test
    @DisplayName("username으로 존재하지 않는 사용자 조회하면 Optional<User> 반환")
    void findByUsername_withNonExistingUsername_returnsEmptyOptional() {
        // given
        User user = createUser("test01", "test01@email.com");
        em.flush();
        em.clear();

        // when
        Optional<User> result = userRepository.findByUsername("test");

        // then
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("email로 사용자 조회했을 때 일치하는 사용자가 있다면 true 반환")
    void existsByEmail_withExistingEmail_returnsTrue() {
        // given
        userRepository.save(createUser("test01", "test01@email.com"));
        em.flush();
        em.clear();

        // when
        boolean result = userRepository.existsByEmail("test01@email.com");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("email로 사용자 조회했을 때 일치하는 사용자가 없다면 false 반환")
    void existsByEmail_withNonExistingEmail_returnsFalse() {
        // given
        User user = createUser("test01", "test01@email.com");
        em.flush();
        em.clear();

        // when
        boolean result = userRepository.existsByEmail("test@email.com");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("username으로 사용자 조회했을 때 일치하는 사용자가 있다면 true 반환")
    void existsByUsername_withExistingUsernamel_returnsTrue() {
        // given
        User user = createUser("test01", "test01@email.com");
        em.flush();
        em.clear();

        // when
        boolean result = userRepository.existsByUsername("test01");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("username으로 사용자 조회했을 때 일치하는 사용자가 없다면 false 반환")
    void existsByUsername_withNonExistingUsername_returnsFalse() {
        // given
        User user = createUser("test01", "test01@email.com");
        em.flush();
        em.clear();

        // when
        boolean result = userRepository.existsByUsername("test");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("모든 사용자 정보 List로 반환")
    void findAllWithProfileAndStatus() {
        // given
        User user1 = createUser("test01", "test01@email.com");
        User user2 = createUser("test02", "test02@email.com");
        em.flush();
        em.clear();

        // when
        List<User> userList = userRepository.findAllWithProfileAndStatus();

        // then
        assertThat(userList).hasSize(2);
        assertThat(userList).allSatisfy(user -> {
            assertThat(user.getProfile()).isNotNull();
            assertThat(user.getStatus()).isNotNull();
        });
    }
}