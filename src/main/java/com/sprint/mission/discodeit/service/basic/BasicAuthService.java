package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.PasswordMismatchException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional(readOnly = true)
  @Override
  public UserDto login(LoginRequest loginRequest) {
    String username = loginRequest.username();
    String password = loginRequest.password();

    log.info("로그인 시도: username={}", username);

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
              log.error("로그인 실패 - 존재하지 않는 사용자: username={}", username);
              return new UserNotFoundException();
            });

    if (!user.getPassword().equals(password)) {
      log.warn("로그인 실패 - 잘못된 비밀번호: username={}", username);
      throw new PasswordMismatchException();
    }

    log.info("로그인 성공: username={}, userId={}", username, user.getId());
    return userMapper.toDto(user);
  }
}