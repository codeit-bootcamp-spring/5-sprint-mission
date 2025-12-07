package com.sprint.mission.discodeit.user.domain;

import com.sprint.mission.discodeit.global.config.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
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
    //import jakarta.persistence.EntityManager;
    // import jakarta.persistence.PersistenceUnitUtil;
    // import org.junit.jupiter.api.Test;
    // import org.springframework.beans.factory.annotation.Autowired;
    // import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
    // import static org.assertj.core.api.Assertions.assertThat;
    //
    // @DataJpaTest
    // class UserRepositoryTest {
    //
    //     @Autowired
    //     private UserRepository userRepository;
    //
    //     @Autowired
    //     private EntityManager em;
    //
    //     @Test
    //     void findWithProfileByUsername_은_Profile을_함께_조회한다() {
    //         // given
    //         User user = new User("testUser", ...);
    //         Profile profile = new Profile("Bio...", ...);
    //         user.setProfile(profile); // 연관관계 설정
    //
    //         userRepository.save(user);
    //
    //         // 중요: 쿼리가 실제로 DB로 나가도록 강제하고, 1차 캐시를 비움
    //         em.flush();
    //         em.clear();
    //
    //         // when
    //         User foundUser = userRepository.findWithProfileByUsername("testUser")
    //                 .orElseThrow();
    //
    //         // then
    //         PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
    //
    //         // 1. User 객체가 로드되었는지 확인 (당연함)
    //         assertThat(util.isLoaded(foundUser)).isTrue();
    //
    //         // 2. Profile 객체(연관관계)가 '초기화(로딩)' 되었는지 확인
    //         // @EntityGraph가 정상 작동했다면 true여야 함
    //         // 작동 안 했다면 Proxy 상태일 것이므로 false가 나오거나(접근 전), 접근 시 추가 쿼리 발생
    //         assertThat(util.isLoaded(foundUser.getProfile())).as("Profile은 Fetch Join으로 이미 로딩되어 있어야 함").isTrue();
    //
    //         // 3. (선택) 실제 데이터 검증
    //         assertThat(foundUser.getProfile().getBio()).isEqualTo("Bio...");
    //     }
    // }

    @Nested
    @DisplayName("findAllWithProfile")
    class FindAllWithProfile {

        @Test
        @DisplayName("모든 사용자를 조회한다")
        void findAllWithProfile_returnsAllUsers() {
            // when
            List<User> users = userRepository.findAllWithProfile();

            // then
            assertThat(users).hasSize(2);
            assertThat(users).extracting(User::getUsername)
                .containsExactlyInAnyOrder("testuser1", "testuser2");
        }
    }

    @Nested
    @DisplayName("findAllWithProfileByIdIn")
    class FindAllByIdIn {

        @Test
        @DisplayName("ID 목록에 해당하는 사용자들을 조회한다")
        void findAllWithProfileByIdIn_returnsMatchingUsers() {
            // given
            List<UUID> ids = List.of(user1.getId(), user2.getId());

            // when
            List<User> users = userRepository.findAllWithProfileByIdIn(ids);

            // then
            assertThat(users).hasSize(2);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 목록을 반환한다")
        void findAllWithProfileByIdIn_withNonExistingIds_returnsEmptyList() {
            // given
            List<UUID> ids = List.of(UUID.randomUUID());

            // when
            List<User> users = userRepository.findAllWithProfileByIdIn(ids);

            // then
            assertThat(users).isEmpty();
        }
    }

    @Nested
    @DisplayName("findWithProfileByUsername")
    class findWithProfileByUsername {

        @Test
        @DisplayName("username으로 사용자를 조회한다")
        void findWithProfileByUsername_returnsUser() {
            // when
            Optional<User> found = userRepository.findWithProfileByUsername("testuser1");

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getEmail()).isEqualTo("test1@example.com");
        }

        @Test
        @DisplayName("존재하지 않는 username으로 조회하면 빈 Optional을 반환한다")
        void findWithProfileByUsername_withNonExistingUsername_returnsEmpty() {
            // when
            Optional<User> found = userRepository.findWithProfileByUsername("nonexistent");

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
