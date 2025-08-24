package com.sprint.mission.discodeit.service.auth;

import static com.sprint.mission.discodeit.support.StringUtil.nullOrStripAndLowerCase;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.auth.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.request.auth.AuthLogoutRequest;
import com.sprint.mission.discodeit.dto.response.user.UserSaveResponse;
import com.sprint.mission.discodeit.exception.UnauthorizedException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
  public UserSaveResponse login(AuthLoginRequest req) {
    String username = nullOrStripAndLowerCase(req.username());
    User u = userRepository.findByUsername(username)
        .orElseThrow(() -> new UnauthorizedException("Username or password incorrect"));
    if (!passwordEncoder.matches(req.password(), u.getPassword())) {
      throw new UnauthorizedException("Username or password incorrect");
    }

    UserStatus userStatus = userStatusRepository.findByUserId(u.getId())
        .orElseGet(() -> new UserStatus(u.getId()));
    userStatusRepository.save(userStatus.login());

    return UserSaveResponse.from(u);
  }

  @Transactional
  public void logout(AuthLogoutRequest req) {
    userRepository.getOrThrow(req.userId());

    UserStatus userStatus = userStatusRepository.findByUserId(req.userId())
        .orElseGet(() -> new UserStatus(req.userId()));
    userStatusRepository.save(userStatus.logout());
  }
}
