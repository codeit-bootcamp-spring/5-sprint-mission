package com.sprint.mission.discodeit.service.auth;

import static com.sprint.mission.discodeit.support.StringUtil.nullOrStripAndLowerCase;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.dto.auth.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.auth.AuthLogoutRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.exception.UnauthorizedException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
  private final BinaryContentRepository binaryContentRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserDto login(AuthLoginRequest req) {
    String username = nullOrStripAndLowerCase(req.username());
    User u = userRepository.findByUsername(username)
        .orElseThrow(() -> new UnauthorizedException("Username or password incorrect"));
    if (!passwordEncoder.matches(req.password(), u.getPassword())) {
      throw new UnauthorizedException("Username or password incorrect");
    }

    UserStatus userStatus = userStatusRepository.findByUserId(u.getId())
        .orElseGet(() -> new UserStatus(u.getId()));

    UserStatus us = userStatusRepository.save(userStatus.login());

    BinaryContent bc = binaryContentRepository.find(u.getProfileId()).orElse(null);

    BinaryContentDto binaryContentDto = bc != null ? BinaryContentDto.from(bc) : null;
    return UserDto.from(u, us.getType(), binaryContentDto);
  }

  @Transactional
  public void logout(AuthLogoutRequest req) {
    userRepository.getOrThrow(req.userId());

    UserStatus userStatus = userStatusRepository.findByUserId(req.userId())
        .orElseGet(() -> new UserStatus(req.userId()));
    userStatusRepository.save(userStatus.logout());
  }
}
