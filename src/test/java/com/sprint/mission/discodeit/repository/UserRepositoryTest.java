package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // given - 테스트 데이터 생성
        user1 = new User("testuser1", "test1@example.com", "encoded1", null);
        user2 = new User("testuser2", "test2@example.com", "encoded2", null);

        BinaryContent profile = new BinaryContent("profile.jpg", 1024L, "image/jpeg");
        binaryContentRepository.save(profile);
        User user3 = new User("testuser3", "test3@example.com", "encoded3", profile);

        userRepository.saveAll(List.of(user1, user2, user3));
    }

    @Test
    @DisplayName("findAllGraph - 모든 사용자를 프로필과 함께 조회 성공")
    void findAllGraph_Success() {
        // when
        List<User> users = userRepository.findAllGraph();

        // then
        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getUsername)
            .containsExactlyInAnyOrder("testuser1", "testuser2", "testuser3");
    }

    @Test
    @DisplayName("findAllGraph - 사용자가 없으면 빈 리스트 반환")
    void findAllGraph_EmptyResult() {
        // given
        userRepository.deleteAll();

        // when
        List<User> users = userRepository.findAllGraph();

        // then
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("findAllByIdIn - ID 컬렉션으로 사용자들 조회 성공")
    void findAllByIdIn_Success() {
        // given
        Set<UUID> ids = Set.of(user1.getId(), user2.getId());

        // when
        List<User> users = userRepository.findAllByIdIn(ids);

        // then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername)
            .containsExactlyInAnyOrder("testuser1", "testuser2");
    }

    @Test
    @DisplayName("findAllByIdIn - 존재하지 않는 ID로 조회 시 빈 리스트 반환")
    void findAllByIdIn_NotFound() {
        // given
        Set<UUID> nonExistentIds = Set.of(UUID.randomUUID(), UUID.randomUUID());

        // when
        List<User> users = userRepository.findAllByIdIn(nonExistentIds);

        // then
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("findAllByIdIn - 빈 컬렉션으로 조회 시 빈 리스트 반환")
    void findAllByIdIn_EmptyCollection() {
        // given
        Set<UUID> emptyIds = Set.of();

        // when
        List<User> users = userRepository.findAllByIdIn(emptyIds);

        // then
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("findByUsername - username으로 사용자 조회 성공")
    void findByUsername_Success() {
        // when
        Optional<User> foundUser = userRepository.findByUsername("testuser1");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser1");
        assertThat(foundUser.get().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    @DisplayName("findByUsername - 존재하지 않는 username으로 조회 시 Optional.empty 반환")
    void findByUsername_NotFound() {
        // when
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("save - 사용자 생성 시 JPA Audit 필드가 자동 설정됨")
    void save_AuditFieldsAutoSet() {
        // given
        User newUser = new User("newuser", "new@example.com", "encoded", null);

        // when
        User savedUser = userRepository.save(newUser);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("save - 프로필이 있는 사용자 생성 성공")
    void save_WithProfile_Success() {
        // given
        BinaryContent profile = new BinaryContent("avatar.png", 2048L, "image/png");
        binaryContentRepository.save(profile);

        User newUser = new User("profileuser", "profile@example.com", "encoded", profile);

        // when
        User savedUser = userRepository.save(newUser);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getProfile()).isNotNull();
        assertThat(savedUser.getProfile().getFileName()).isEqualTo("avatar.png");
    }

    @Test
    @DisplayName("delete - 사용자 삭제 성공")
    void delete_Success() {
        // given
        UUID userId = user1.getId();

        // when
        userRepository.delete(user1);

        // then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }
}
