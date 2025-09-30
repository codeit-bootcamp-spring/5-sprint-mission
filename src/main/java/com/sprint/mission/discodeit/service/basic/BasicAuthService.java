package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthDto.Login;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.auth.InvalidUserStatusException;
import com.sprint.mission.discodeit.exception.auth.LoginFailedException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;

  @Override
  public UserDto.Detail login(Login login) {

    String username = login.getUsername();
    String password = login.getPassword();

    User user = userRepository.findByUsername(username)
                              .orElse(null);

    if (user == null || user.getPassword() == null || !user.getPassword()
                                                           .equals(password)) {
      throw new LoginFailedException();
    }

    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                                                .orElse(null);
    if (userStatus == null) {
      throw new InvalidUserStatusException(user.getId());
    }

    userStatus.update();
    userStatusRepository.save(userStatus);

    return userMapper.toDetail(user);
  }
}
