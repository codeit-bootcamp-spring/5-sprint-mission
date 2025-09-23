package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BinaryContentRepository binaryContentRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;

    @Test
    @DisplayName("사용자 조회 성공")
    void findAllWithProfile_success() {
        BinaryContent profile = binaryContentRepository.save(
                new BinaryContent("avatar.png", "image/png", 1024L)
        );
        User user = new User("test01", "pw123", "nick01", "test01@email.com", profile);
        userRepository.save(user);

        List<User> result = userRepository.findAllWithProfile();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getProfile()).isNotNull();
        assertThat(result.get(0).getProfile().getFileName()).isEqualTo("avatar.png");
    }

    @Test
    @DisplayName("사용자가 없으면 빈 리스트 반환")
    void findAllWithProfile_empty() {
        List<User> result = userRepository.findAllWithProfile();

        assertThat(result).isEmpty();
    }

}
