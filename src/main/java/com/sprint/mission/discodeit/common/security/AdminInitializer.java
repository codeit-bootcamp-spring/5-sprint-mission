package com.sprint.mission.discodeit.common.security;

import com.sprint.mission.discodeit.common.config.properties.AdminProperties;
import com.sprint.mission.discodeit.domain.user.entity.Role;
import com.sprint.mission.discodeit.domain.user.entity.User;
import com.sprint.mission.discodeit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(prefix = "discodeit.admin", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String username = adminProperties.username();

        if (userRepository.existsByUsername(username)) {
            log.debug("관리자 계정이 이미 존재합니다: username={}", username);
            return;
        }

        User admin = new User(
            username,
            adminProperties.email(),
            passwordEncoder.encode(adminProperties.password()),
            null
        );
        admin.updateRole(Role.ADMIN);
        userRepository.save(admin);

        log.info("관리자 계정 생성 완료: username={}", username);
    }
}
