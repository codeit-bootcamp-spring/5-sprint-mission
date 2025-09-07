package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;

  @Transactional
  @Override
  public UserResponseDto login(LoginRequest loginRequest) {
    User user = userRepository.findByUsername(loginRequest.username())
        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. " + loginRequest.username()));

    if (!user.getPassword().equals(loginRequest.password())) {
      throw new IllegalArgumentException("잘못된 비밀번호입니다.");
    }

    // UserStatus 조회 (없으면 새로 생성)
    UserStatus status = userStatusRepository.findByUserId(user.getId())
        .orElseGet(() -> {
          UserStatus newStatus = new UserStatus();
          newStatus.setUser(user);
          newStatus.setLastActiveAt(Instant.now());
          return userStatusRepository.save(newStatus);
        });

    // 로그인 시 상태 갱신
    status.update(Instant.now());
    // save() 불필요 → 변경감지로 자동 UPDATE

    return userMapper.toDto(user, status);
  }
}
