package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 정보입니다."));
    return user;
  }
}
