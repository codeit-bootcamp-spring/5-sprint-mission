package com.sprint.mission.discodeit.user.domain;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.global.config.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
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
@Import(JpaConfig.class)
@DisplayName("UserRepository 슬라이스 테스트")
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private EntityManager entityManager;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        BinaryContent profile1 = binaryContentRepository.save(
            new BinaryContent("profile1.png", 1024L, "image/png"));
        BinaryContent profile2 = binaryContentRepository.save(
            new BinaryContent("profile2.png", 2048L, "image/png"));

        user1 = userRepository.save(
            new User("testuser1", "test1@example.com", "password1234", profile1));
        user2 = userRepository.save(
            new User("testuser2", "test2@example.com", "password1234", profile2));
        user3 = userRepository.save(
            new User("testuser3", "test3@example.com", "password1234", null));
    }

    @Nested
    @DisplayName("findAllWithProfile")
    class FindAllWithProfile {

        @Test
        @DisplayName("모든 사용자를 Profile과 함께 조회 성공")
        void findAllWithProfile_returnsAllUsersWithProfile() {
            // given
            entityManager.flush();
            entityManager.clear();

            // when
            List<User> result = userRepository.findAllWithProfile();

            // then
            assertThat(result).hasSize(3);
            assertThat(result).extracting(User::getUsername)
                .containsExactlyInAnyOrder("testuser1", "testuser2", "testuser3");
        }

        @Test
        @DisplayName("Profile Eager Loading 검증 (N+1 방지)")
        void findAllWithProfile_profileIsEagerLoaded() {
            // given
            entityManager.flush();
            entityManager.clear();

            // when
            List<User> result = userRepository.findAllWithProfile();

            // then
            PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

            for (User user : result) {
                assertThat(util.isLoaded(user, "profile"))
                    .as("EntityGraph Profile Eager Loading: " + user.getUsername())
                    .isTrue();
            }
        }

        @Test
        @DisplayName("Profile 유무와 관계없이 모든 사용자 조회")
        void findAllWithProfile_includesUsersWithAndWithoutProfile() {
            // given
            entityManager.flush();
            entityManager.clear();

            // when
            List<User> result = userRepository.findAllWithProfile();

            // then
            long usersWithProfile = result.stream()
                .filter(u -> u.getProfile() != null)
                .count();
            long usersWithoutProfile = result.stream()
                .filter(u -> u.getProfile() == null)
                .count();

            assertThat(usersWithProfile).isEqualTo(2);
            assertThat(usersWithoutProfile).isEqualTo(1);
        }

        @Test
        @DisplayName("사용자가 없는 경우 빈 목록 반환")
        void findAllWithProfile_withNoUsers_returnsEmptyList() {
            // given
            userRepository.deleteAll();
            entityManager.flush();
            entityManager.clear();

            // when
            List<User> result = userRepository.findAllWithProfile();

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllWithProfileByIdIn")
    class FindAllWithProfileByIdIn {

        @Test
        @DisplayName("ID 목록으로 사용자를 Profile과 함께 조회 성공")
        void findAllWithProfileByIdIn_returnsUsersWithProfile() {
            // given
            entityManager.flush();
            entityManager.clear();

            List<UUID> ids = List.of(user1.getId(), user2.getId());

            // when
            List<User> result = userRepository.findAllWithProfileByIdIn(ids);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(User::getId)
                .containsExactlyInAnyOrder(user1.getId(), user2.getId());
        }

        @Test
        @DisplayName("Profile Eager Loading 검증 (N+1 방지)")
        void findAllWithProfileByIdIn_profileIsEagerLoaded() {
            // given
            entityManager.flush();
            entityManager.clear();

            List<UUID> ids = List.of(user1.getId(), user2.getId());

            // when
            List<User> result = userRepository.findAllWithProfileByIdIn(ids);

            // then
            PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

            for (User user : result) {
                assertThat(util.isLoaded(user, "profile"))
                    .as("EntityGraph Profile Eager Loading: " + user.getUsername())
                    .isTrue();
            }
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 목록 반환")
        void findAllWithProfileByIdIn_withEmptyIds_returnsEmptyList() {
            // when
            List<User> result = userRepository.findAllWithProfileByIdIn(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID 포함 시 존재하는 사용자만 반환")
        void findAllWithProfileByIdIn_withNonExistingIds_returnsOnlyExistingUsers() {
            // given
            entityManager.flush();
            entityManager.clear();

            List<UUID> ids = List.of(user1.getId(), UUID.randomUUID());

            // when
            List<User> result = userRepository.findAllWithProfileByIdIn(ids);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(user1.getId());
        }

        @Test
        @DisplayName("모든 ID가 존재하지 않는 경우 빈 목록 반환")
        void findAllWithProfileByIdIn_withAllNonExistingIds_returnsEmptyList() {
            // given
            List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());

            // when
            List<User> result = userRepository.findAllWithProfileByIdIn(ids);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findWithProfileById")
    class FindWithProfileById {

        @Test
        @DisplayName("ID로 사용자를 Profile과 함께 조회 성공")
        void findWithProfileById_returnsUserWithProfile() {
            // given
            entityManager.flush();
            entityManager.clear();

            // when
            Optional<User> result = userRepository.findWithProfileById(user1.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser1");
            assertThat(result.get().getProfile()).isNotNull();
            assertThat(result.get().getProfile().getFileName()).isEqualTo("profile1.png");
        }

        @Test
        @DisplayName("Profile Eager Loading 검증 (N+1 방지)")
        void findWithProfileById_profileIsEagerLoaded() {
            // given
            entityManager.flush();
            entityManager.clear();

            // when
            Optional<User> result = userRepository.findWithProfileById(user1.getId());

            // then
            assertThat(result).isPresent();

            PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
            assertThat(util.isLoaded(result.get(), "profile"))
                .as("EntityGraph Profile Eager Loading")
                .isTrue();
        }

        @Test
        @DisplayName("Profile이 없는 사용자 조회 성공")
        void findWithProfileById_withNullProfile_returnsUserWithNullProfile() {
            // given
            entityManager.flush();
            entityManager.clear();

            // when
            Optional<User> result = userRepository.findWithProfileById(user3.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser3");
            assertThat(result.get().getProfile()).isNull();

            PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
            assertThat(util.isLoaded(result.get(), "profile"))
                .as("Left Join Fetch가 적용되어 profile 필드가 초기화 상태여야 함")
                .isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional 반환")
        void findWithProfileById_withNonExistingId_returnsEmpty() {
            // when
            Optional<User> result = userRepository.findWithProfileById(UUID.randomUUID());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByUsername")
    class FindByUsername {

        @Test
        @DisplayName("username으로 사용자 조회 성공")
        void findByUsername_returnsUser() {
            // when
            Optional<User> result = userRepository.findByUsername("testuser1");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("test1@example.com");
        }

        @Test
        @DisplayName("존재하지 않는 username으로 조회 시 빈 Optional 반환")
        void findByUsername_withNonExistingUsername_returnsEmpty() {
            // when
            Optional<User> result = userRepository.findByUsername("nonexistent");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("대소문자 구분 조회")
        void findByUsername_isCaseSensitive() {
            // when
            Optional<User> result = userRepository.findByUsername("TESTUSER1");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByUsername")
    class ExistsByUsername {

        @Test
        @DisplayName("존재하는 username으로 조회 시 true 반환")
        void existsByUsername_withExistingUsername_returnsTrue() {
            // when
            boolean result = userRepository.existsByUsername("testuser1");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 username으로 조회 시 false 반환")
        void existsByUsername_withNonExistingUsername_returnsFalse() {
            // when
            boolean result = userRepository.existsByUsername("nonexistent");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("대소문자 구분 조회")
        void existsByUsername_isCaseSensitive() {
            // when
            boolean result = userRepository.existsByUsername("TESTUSER1");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmail {

        @Test
        @DisplayName("존재하는 email로 조회 시 true 반환")
        void existsByEmail_withExistingEmail_returnsTrue() {
            // when
            boolean result = userRepository.existsByEmail("test1@example.com");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 email로 조회 시 false 반환")
        void existsByEmail_withNonExistingEmail_returnsFalse() {
            // when
            boolean result = userRepository.existsByEmail("nonexistent@example.com");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("대소문자 구분 조회")
        void existsByEmail_isCaseSensitive() {
            // when
            boolean result = userRepository.existsByEmail("TEST1@EXAMPLE.COM");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByRole")
    class FindAllByRole {

        @Test
        @DisplayName("특정 Role을 가진 사용자 조회 성공")
        void findAllByRole_withExistingRole_returnsUsers() {
            // given - 기본 사용자들은 모두 USER Role
            entityManager.flush();
            entityManager.clear();

            // when
            List<User> result = userRepository.findAllByRole(Role.USER);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).extracting(User::getRole)
                .containsOnly(Role.USER);
        }

        @Test
        @DisplayName("ADMIN Role 사용자 조회")
        void findAllByRole_withAdminRole_returnsAdmins() {
            // given
            User admin = new User("admin", "admin@example.com", "password1234", null);
            admin.updateRole(Role.ADMIN);
            userRepository.save(admin);

            entityManager.flush();
            entityManager.clear();

            // when
            List<User> result = userRepository.findAllByRole(Role.ADMIN);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUsername()).isEqualTo("admin");
            assertThat(result.get(0).getRole()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("해당 Role 사용자가 없는 경우 빈 목록 반환")
        void findAllByRole_withNoMatchingRole_returnsEmptyList() {
            // given - 기본 사용자들은 모두 USER Role, ADMIN은 없음
            entityManager.flush();
            entityManager.clear();

            // when
            List<User> result = userRepository.findAllByRole(Role.ADMIN);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 ADMIN 사용자가 있는 경우 모두 조회")
        void findAllByRole_withMultipleAdmins_returnsAllAdmins() {
            // given
            User admin1 = new User("admin1", "admin1@example.com", "password1234", null);
            admin1.updateRole(Role.ADMIN);
            userRepository.save(admin1);

            User admin2 = new User("admin2", "admin2@example.com", "password1234", null);
            admin2.updateRole(Role.ADMIN);
            userRepository.save(admin2);

            entityManager.flush();
            entityManager.clear();

            // when
            List<User> result = userRepository.findAllByRole(Role.ADMIN);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(User::getUsername)
                .containsExactlyInAnyOrder("admin1", "admin2");
        }
    }
}
