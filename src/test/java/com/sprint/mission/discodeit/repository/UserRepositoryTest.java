package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserStatusRepository userStatusRepository;

    @Test
    @DisplayName("username으로 유저 조회 성공")
    void findByUsernameSuccess() {
        User user = new User("mike", "mike@test.com", "1234", null);
        userRepository.save(user);
        userStatusRepository.save(new UserStatus(user, Instant.now()));

        Optional<User> found = userRepository.findByUsername("mike");

        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("없는 username 조회 실패")
    void findByUsernameFail() {
        assertThat(userRepository.findByUsername("ghost")).isEmpty();
    }

    @Test
    @DisplayName("이메일 존재 확인 성공")
    void existsByEmailSuccess() {
        User user = new User("alex", "alex@test.com", "pwd", null);
        userRepository.save(user);
        userStatusRepository.save(new UserStatus(user, Instant.now()));

        assertThat(userRepository.existsByEmail("alex@test.com")).isTrue();
    }

    @Test
    @DisplayName("없는 이메일 존재 확인 실패")
    void existsByEmailFail() {
        assertThat(userRepository.existsByEmail("none@test.com")).isFalse();
    }

    @Test
    @DisplayName("상태와 프로필을 함께 조회")
    void findAllWithProfileAndStatus() {
        User user = new User("john", "john@test.com", "pw", null);
        userRepository.save(user);
        userStatusRepository.save(new UserStatus(user, Instant.now()));

        List<User> results = userRepository.findAllWithProfileAndStatus();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isNotNull();
    }
}