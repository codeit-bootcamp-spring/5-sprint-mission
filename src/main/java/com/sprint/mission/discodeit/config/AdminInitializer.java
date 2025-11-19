package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${discodeit.admin.username:admin}")
  private String adminUsername;

  @Value("${discodeit.admin.email:admin@example.com}")
  private String adminEmail;

  @Value("${discodeit.admin.password:Admin123!}")
  private String adminPassword;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (userRepository.existsByRole(Role.ADMIN)) {
      log.info("ADMIN 계정이 이미 존재합니다. 초기화를 건너뜁니다.");
      return;
    }

    String encodedPassword = passwordEncoder.encode(adminPassword);

    User admin = new User(adminUsername, adminEmail, encodedPassword, null);
    admin.updateRole(Role.ADMIN);

    userRepository.save(admin);

    log.info("기본 ADMIN 계정을 초기화했습니다: username={}, email={}",
        adminUsername, adminEmail);
  }
}