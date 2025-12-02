package com.sprint.mission.discodeit.service;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.auth.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.jwt.data.JwtInformation;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.audit.AuthAuditService;
import com.sprint.mission.discodeit.security.audit.AuthMetricsService;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final AuthAuditService authAuditService;
    private final AuthMetricsService authMetricsService;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @CacheEvict(value = "users", allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateRole(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Role oldRole = user.getRole();
        Role newRole = request.newRole();
        user.updateRole(newRole);

        jwtRegistry.invalidateJwtInformationByUserId(userId);

        authAuditService.logRoleChange(userId, user.getUsername(), oldRole.name(), newRole.name());

        eventPublisher.publishEvent(new RoleUpdatedEvent(userId, oldRole, newRole));

        return userMapper.toDto(user);
    }

    public JwtInformation refreshToken(String refreshToken) {
        if (!tokenProvider.validateRefreshToken(refreshToken)
            || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            log.error("Invalid or expired refresh token: {}", refreshToken);
            authMetricsService.recordTokenRefreshFailure();
            throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
            authMetricsService.recordTokenRefreshFailure();
            throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
        }

        try {
            String newAccessToken = tokenProvider.generateAccessToken(discodeitUserDetails);
            String newRefreshToken = tokenProvider.generateRefreshToken(discodeitUserDetails);
            log.info("Access token refreshed for user: {}", username);

            JwtInformation newJwtInformation = new JwtInformation(
                discodeitUserDetails.getUserDto(),
                newAccessToken,
                newRefreshToken
            );
            jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

            return newJwtInformation;
        } catch (JOSEException e) {
            log.error("Failed to generate new tokens for user: {}", username, e);
            authMetricsService.recordTokenRefreshFailure();
            throw new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }
}
