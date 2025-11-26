package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

//  @Transactional(readOnly = true)
//  @Override
//  public UserDto login(LoginRequest loginRequest) {
//    log.debug("로그인 시도: username={}", loginRequest.username());
//
//    String username = loginRequest.username();
//    String password = loginRequest.password();
//
//    User user = userRepository.findByUsername(username)
//        .orElseThrow(() -> UserNotFoundException.withUsername(username));
//
//    if (!user.getPassword().equals(password)) {
//      throw InvalidCredentialsException.wrongPassword();
//    }
//
//    log.info("로그인 성공: userId={}, username={}", user.getId(), username);
//    return userMapper.toDto(user);
//  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  @Override
  public UserDto updateRole(RoleUpdateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> UserNotFoundException.withId(request.userId()));

    user.updateRole(request.role());
    invalidateUserSessions(user.getUsername());

    return userMapper.toDto(user);
  }

  private void invalidateUserSessions(String username) {
    List<SessionInformation> sessions = sessionRegistry.getAllSessions(username, false);
    sessions.forEach(SessionInformation::expireNow);
    log.info("사용자명 {}의 모든 세션을 무효화 완료. 무효화된 세션 수: {}", username, sessions.size());
  }
}
