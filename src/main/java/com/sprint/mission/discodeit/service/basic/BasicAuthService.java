package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
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

    log.info("[AUTH][LOGIN] username={}", username);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.warn("[AUTH][LOGIN] user not found username={}", username);
          return new NoSuchElementException("User with username " + username + " not found");
        });

    if (!user.getPassword().equals(password)) {
      log.warn("[AUTH][LOGIN] invalid password username={}", username);
      throw new IllegalArgumentException("Wrong password");
    }

    log.info("[AUTH][LOGIN][SUCCESS] userId={}", user.getId());
    return userMapper.toDto(user);
  }
}