package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.user.UserRepository;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // JPA 슬라이스 테스트를 위한 어노테이션
@ActiveProfiles("test") // application-test.yaml 설정 사용
@EnableJpaAuditing // 테스트 환경에서 JPA Auditing 기능 활성화
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("새로운 사용자를 저장하면 생성 시간이 자동으로 기록")
    void saveUserWithAuditing() {

        // given - 테스트 준비
        User newUser = new User("testUser", "test@email.com", "1234", null);

        // when - 테스트 실행
        User savedUser = userRepository.save(newUser);

        // then - 결과 검증
        assertThat(savedUser.getUsername()).isEqualTo("testUser");
        assertThat(savedUser.getId()).isNotNull(); // ID가 생성되었는지 확인

        // Auditing 기능 검증
        System.out.println(">>>>>>>>> createdAt: " + savedUser.getCreatedAt());
        assertThat(savedUser.getCreatedAt()).isNotNull(); // 생성 시간이 null이 아닌지 확인
        assertThat(savedUser.getCreatedAt()).isBefore(Instant.now()); // 생성 시간이 현재 시간보다 이전인지 확인
    }

    @Test
    @DisplayName("사용자를 ID로 조회하는 기능을 테스트한다.")
    void findById() {
        User user = new User("findUser", "find@email.com", "1234", null);
        // given
        User createdUser = userRepository.save(user);

        // when
        User foundUser = userRepository.findById(user.getId()).orElse(null);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
    }
}
