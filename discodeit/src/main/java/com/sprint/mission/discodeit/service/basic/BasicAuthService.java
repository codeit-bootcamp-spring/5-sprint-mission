package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionManager sessionManager;
  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry<UUID>  jwtRegistry;
  private final UserDetailsService userDetailsService;


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

    Role newRole = request.newRole();
    user.updateRole(newRole);

    jwtRegistry.invalidateJwtInformationByUserId(userId);
    return userMapper.toDto(user);
  }


  // 토큰 재발급 시나리오
  public JwtInformation refreshToken(String refreshToken) {
    // Validate refresh token
    if(!tokenProvider.validateRefreshToken(refreshToken)
            || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken) ) {
      log.info("Invalid or expired refresh Token: {}", refreshToken);
      throw new RuntimeException("Invalid or expired refresh Token");
    }

    String username = tokenProvider.getUsernameFromToken(refreshToken);
    UserDetails userDetails= userDetailsService.loadUserByUsername(username);

    if(userDetails==null ) {
      throw new UsernameNotFoundException("Invalid username or password");
    }

    try {
      DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails) userDetails;
      String newAccessToken = tokenProvider. generateAccessToken(discodeitUserDetails);
      String newRefreshToken = tokenProvider. generateRefreshToken(discodeitUserDetails);
      log.info("Refresh Token: {}", newAccessToken);

      JwtInformation newJwtInformation = new JwtInformation(
              discodeitUserDetails.getUserDto(),
              newAccessToken,
              newRefreshToken
      );
      jwtRegistry.rotateJwtInformation(refreshToken,newJwtInformation);
      return newJwtInformation;

    }catch(Exception e) {

      log.error("Failed to generate new tokens for user: {}", username, e);
      throw new RuntimeException("INTERNAL_SERVER_ERROR");

    }
  }



}
