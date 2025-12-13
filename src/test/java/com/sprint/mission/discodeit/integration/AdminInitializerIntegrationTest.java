package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "discodeit.admin.enabled=true",
    "discodeit.admin.username=testadmin",
    "discodeit.admin.email=testadmin@example.com",
    "discodeit.admin.password=AdminP@ss123!"
})
@DisplayName("AdminInitializer 통합 테스트")
class AdminInitializerIntegrationTest extends IntegrationTestSupport {

    private static final String ADMIN_USERNAME = "testadmin";
    private static final String ADMIN_EMAIL = "testadmin@example.com";

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("admin.enabled=true일 때 어드민 계정이 자동으로 생성된다")
    void adminAccountIsCreated_whenEnabled() {
        // when
        Optional<User> admin = userRepository.findByUsername(ADMIN_USERNAME);

        // then
        assertThat(admin).isPresent();
        assertThat(admin.get().getEmail()).isEqualTo(ADMIN_EMAIL);
        assertThat(admin.get().getRole()).isEqualTo(Role.ADMIN);
    }
}
