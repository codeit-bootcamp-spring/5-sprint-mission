package com.sprint.mission.discodeit.domain.auth;

import com.sprint.mission.discodeit.domain.auth.dto.LoginRequest;
import com.sprint.mission.discodeit.domain.user.UserRepository;
import com.sprint.mission.discodeit.domain.user.dto.UserDto;
import com.sprint.mission.discodeit.domain.user.mapper.UserMapper;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional(readOnly = true)
  public UserDto login(LoginRequest loginRequest) {
    String username = loginRequest.username();
    String password = loginRequest.password();

    User user = userRepository.findByUsername(username)
        .orElseThrow(
            () -> new NoSuchElementException("User with username " + username + " not found"));

    if (!user.getPassword().equals(password)) {
      throw new IllegalArgumentException("Wrong password");
    }

    return userMapper.toDto(user);
  }
}
