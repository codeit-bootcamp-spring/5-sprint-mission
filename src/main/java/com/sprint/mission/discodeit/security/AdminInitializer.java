package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!userRepository.existsByRole(Role.ADMIN)) {
            log.info("[AdminInitializer] ADMIN 계정이 없습니다. 초기화를 시작합니다.");

            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin"),
                    "관리자",
                    "admin@discodeit.com",
                    Role.ADMIN,
                    null
            );

            userRepository.save(admin);

            log.info("[AdminInitializer] ADMIN 계정 초기화 완료: {}", admin.getUsername());
        } else {
            log.info("[AdminInitializer] ADMIN 계정이 이미 존재합니다.");
        }
    }
}