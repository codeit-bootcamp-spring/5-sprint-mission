package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import com.sprint.mission.discodeit.dto.request.auth.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.exception.AccessDeniedException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.UUID;
import java.util.function.Consumer;
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

  protected void update(UUID id, Consumer<User> updater) {
    User entity = userRepository.getOrThrow(id);
    updater.accept(entity);
    userRepository.save(entity);
  }

  @Transactional
  public UserResponse login(AuthLoginRequest req) {
    String email = req.email().strip().toLowerCase();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));
    String password = req.password().strip();
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    if (user.isBanned()) {
      throw new AccessDeniedException("정지된 계정입니다.");
    }

    update(user.getId(), User::activate);

    UserStatus userStatus = userStatusRepository
        .findByUserId(user.getId())
        .orElseGet(() -> userStatusRepository.save(new UserStatus(user.getId())));
    userStatus.login();
    userStatusRepository.save(userStatus);

    return UserResponse.from(user, UserStatusType.ONLINE);
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
