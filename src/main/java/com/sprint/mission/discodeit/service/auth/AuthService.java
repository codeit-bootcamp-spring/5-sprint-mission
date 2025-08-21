package com.sprint.mission.discodeit.service.auth;

import static com.sprint.mission.discodeit.support.StringUtil.nullOrStripAndLowerCase;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import com.sprint.mission.discodeit.dto.request.auth.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.exception.UnauthorizedException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserResponse login(AuthLoginRequest req) {
    String username = nullOrStripAndLowerCase(req.username());
    User u = userRepository.findByUsername(username)
        .orElseThrow(() -> new UnauthorizedException("Username or password incorrect"));
    if (!passwordEncoder.matches(req.password().strip(), u.getPassword())) {
      throw new UnauthorizedException("Username or password incorrect");
    }

    UserStatus userStatus = userStatusRepository.findByUserId(u.getId())
        .orElseGet(() -> new UserStatus(u.getId()));
    userStatus.login();
    userStatusRepository.save(userStatus);

    return UserResponse.from(u, UserStatusType.ONLINE);
  }

  @Transactional
  public void logout(UUID userId) {
    userRepository.getOrThrow(userId);

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseGet(() -> new UserStatus(userId));
    userStatus.logout();
    userStatusRepository.save(userStatus);
  }
}
