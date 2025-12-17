package com.sprint.mission.discodeit.service.basic;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.message.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final UserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;
  private final ApplicationEventPublisher eventPublisher;

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  @Override
  public UserDto updateRole(RoleUpdateRequest request) {
    return updateRoleInternal(request);
  }

  @Transactional
  @Override
  public UserDto updateRoleInternal(RoleUpdateRequest request) {
    UUID userId = request.userId();
    User user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.withId(userId));

    Role previousRole = user.getRole();
    Role newRole = user.getRole();
    user.updateRole(newRole);

    jwtRegistry.invalidateJwtInformationByUserId(userId);
    eventPublisher.publishEvent(
            new RoleUpdatedEvent(user.getId(), previousRole, newRole, user.getUpdatedAt())
    );

    return userMapper.toDto(user);
  }

  @Override
  public JwtInformation refreshToken(String refreshToken) {
    if (!jwtTokenProvider.validateRefreshToken(refreshToken) ||
    !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      log.error("Invalid refresh token : {}", refreshToken);

      throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
    }

    String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
      throw new DiscodeitException(ErrorCode.INVALID_USER_DETAILS);
    }

    try {
      String newAccessToken = jwtTokenProvider.generateAccessToken(discodeitUserDetails);
      String newRefreshToken = jwtTokenProvider.generateRefreshToken(discodeitUserDetails);
      log.info("AccessToken refreshed : {}", username);

      JwtInformation newJwtInformation = new JwtInformation(
              discodeitUserDetails.getUserDto(),
              newAccessToken,
              newRefreshToken
      );

      jwtRegistry.rotateJwtInformation(
              refreshToken,
              newJwtInformation
      );

      return newJwtInformation;
    } catch (JOSEException e) {
      log.error("Failed to generate new tokens : {}", username, e);
      throw new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }
}
