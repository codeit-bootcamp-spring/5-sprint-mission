package com.sprint.mission.discodeit.domain.repository;

import com.sprint.mission.discodeit.domain.user.domain.User;
import com.sprint.mission.discodeit.domain.user.domain.UserRepository;
import com.sprint.mission.discodeit.global.config.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
@DisplayName("UserRepository 슬라이스 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User("testuser1", "test1@example.com", "password1234", null));
        user2 = userRepository.save(new User("testuser2", "test2@example.com", "password1234", null));
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("모든 사용자를 조회한다")
        void findAll_returnsAllUsers() {
            // when
            List<User> users = userRepository.findAll();

            // then
            assertThat(users).hasSize(2);
            assertThat(users).extracting(User::getUsername)
                .containsExactlyInAnyOrder("testuser1", "testuser2");
        }
    }

    @Nested
    @DisplayName("findAllByIdIn")
    class FindAllByIdIn {

        @Test
        @DisplayName("ID 목록에 해당하는 사용자들을 조회한다")
        void findAllByIdIn_returnsMatchingUsers() {
            // given
            List<UUID> ids = List.of(user1.getId(), user2.getId());

            // when
            List<User> users = userRepository.findAllByIdIn(ids);

            // then
            assertThat(users).hasSize(2);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 목록을 반환한다")
        void findAllByIdIn_withNonExistingIds_returnsEmptyList() {
            // given
            List<UUID> ids = List.of(UUID.randomUUID());

            // when
            List<User> users = userRepository.findAllByIdIn(ids);

            // then
            assertThat(users).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByUsername")
    class FindByUsername {

        @Test
        @DisplayName("username으로 사용자를 조회한다")
        void findByUsername_returnsUser() {
            // when
            Optional<User> found = userRepository.findByUsername("testuser1");

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getEmail()).isEqualTo("test1@example.com");
        }

        @Test
        @DisplayName("존재하지 않는 username으로 조회하면 빈 Optional을 반환한다")
        void findByUsername_withNonExistingUsername_returnsEmpty() {
            // when
            Optional<User> found = userRepository.findByUsername("nonexistent");

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByUsername")
    class ExistsByUsername {

        @Test
        @DisplayName("존재하는 username이면 true를 반환한다")
        void existsByUsername_withExistingUsername_returnsTrue() {
            // when
            boolean exists = userRepository.existsByUsername("testuser1");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 username이면 false를 반환한다")
        void existsByUsername_withNonExistingUsername_returnsFalse() {
            // when
            boolean exists = userRepository.existsByUsername("nonexistent");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmail {

        @Test
        @DisplayName("존재하는 email이면 true를 반환한다")
        void existsByEmail_withExistingEmail_returnsTrue() {
            // when
            boolean exists = userRepository.existsByEmail("test1@example.com");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 email이면 false를 반환한다")
        void existsByEmail_withNonExistingEmail_returnsFalse() {
            // when
            boolean exists = userRepository.existsByEmail("nonexistent@example.com");

            // then
            assertThat(exists).isFalse();
        }
    }
}
