package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;

  @Override
  @Transactional // 상태 변경 Dirty Checking
  public User login(LoginRequest request) {
    User user = userRepository.findByUsernameAndPassword(
            request.getUsername(), request.getPassword()
        )
        .orElseThrow(() -> {
          log.warn("로그인 실패: username={}", request.getUsername());
          return new IllegalArgumentException("잘못된 사용자 정보입니다.");
        });

    log.info("로그인 성공: userId={}, username={}", user.getId(), user.getUsername());
    return user;
  }
}
