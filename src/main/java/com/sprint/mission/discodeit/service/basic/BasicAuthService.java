package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service("authService")
@RequiredArgsConstructor
@Validated
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;

  @Override
  public User login(@Valid UserLoginRequest userLoginRequest) {
    User user = userRepository.findByUsername(userLoginRequest.username())
        .orElseThrow(() -> new NoSuchElementException(
            "사용자를 찾을 수 없음. [" + userLoginRequest.username() + "]"));
    if (!user.getPassword().equals(userLoginRequest.password())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않음.");
    }
    return user;
  }
}
