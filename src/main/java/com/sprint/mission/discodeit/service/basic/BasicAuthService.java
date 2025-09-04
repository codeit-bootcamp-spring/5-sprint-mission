package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service("authService")
@RequiredArgsConstructor
@Validated
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserDto login(@Valid LoginRequest loginRequest) {
    User user = userRepository.findByUsername(loginRequest.username())
        .orElseThrow(() -> new NoSuchElementException(
            "사용자를 찾을 수 없음. [" + loginRequest.username() + "]"));
    if (!user.getPassword().equals(loginRequest.password())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않음.");
    }
    return userMapper.toDto(user);
  }
}
