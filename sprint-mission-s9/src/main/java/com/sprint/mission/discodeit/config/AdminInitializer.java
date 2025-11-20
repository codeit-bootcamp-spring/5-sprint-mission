package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationRunner {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    if (!userRepository.existsByUsername("admin")) {
      User admin = new User(
          "admin",
          "admin@discodeit.com",
          passwordEncoder.encode("Admin123!"),
          null
      );
      admin.updateRole(Role.ADMIN);
      userRepository.save(admin);

      log.info("admin 계정 초기화 완료");
    }

  }
}
