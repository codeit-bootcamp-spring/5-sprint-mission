package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.InitService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicInitService implements InitService {

  @Value("${discodeit.admin.username}")
  private String adminUsername;
  @Value("${discodeit.admin.password}")
  private String adminPassword;
  @Value("${discodeit.admin.email}")
  private String adminEmail;

  @Value("${discodeit.user.username}")
  private String userUsername;
  @Value("${discodeit.user.password}")
  private String userPassword;
  @Value("${discodeit.user.email}")
  private String userEmail;


  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserStatusRepository userStatusRepository;

  @Transactional
  public void initAdmin() {
    if (userRepository.existsByUsername(adminUsername) || userRepository.existsByEmail(
        adminEmail)) {
      log.warn("이미 관리자가 존재합니다.");
      return;
    }

    String encodedPassword = passwordEncoder.encode(adminPassword);
    System.out.println("비밀번호 확인: " + new BCryptPasswordEncoder().matches("admin", encodedPassword));

    User admin = new User(
        adminUsername,
        adminEmail,
        encodedPassword,
        null,
        Role.ADMIN
    );

    userRepository.save(admin);
    UserStatus userStatus = new UserStatus(admin, Instant.now());
    userStatusRepository.save(userStatus);

    log.info("관리자가 초기화되었습니다.");
  }

  @Transactional
  public void initDefaultUser() {
    if (userRepository.existsByUsername(userUsername) || userRepository.existsByEmail(userEmail)) {
      log.warn("이미 사용자가 존재합니다.");
      return;
    }

    String encodedPassword = passwordEncoder.encode(userPassword);
    User user = new User(
        userUsername,
        userEmail,
        encodedPassword,
        null
    );

    userRepository.save(user);
    UserStatus userStatus = new UserStatus(user, Instant.now());
    userStatusRepository.save(userStatus);
    log.info("사용자가 초기화되었습니다.");
  }

}
