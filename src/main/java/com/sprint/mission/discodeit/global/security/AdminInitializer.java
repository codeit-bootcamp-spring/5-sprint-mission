package com.sprint.mission.discodeit.global.security;

import com.sprint.mission.discodeit.global.config.properties.AdminProperties;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
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

        if (userRepository.existsByUsername(username)
        || userRepository.existsByEmail(adminProperties.email())) {
            log.info("Admin account already exists");
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

        log.info("Admin account created");
    }
}
