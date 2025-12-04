package com.sprint.mission.discodeit.domain.service;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import com.sprint.mission.discodeit.common.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.common.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.common.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.common.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.common.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.domain.dto.auth.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtInformation;
import com.sprint.mission.discodeit.domain.dto.user.data.UserDto;
import com.sprint.mission.discodeit.domain.entity.Role;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.mapper.UserMapper;
import com.sprint.mission.discodeit.domain.repository.UserRepository;
import com.sprint.mission.discodeit.infra.cache.CacheService;
import com.sprint.mission.discodeit.infra.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.infra.event.auth.TokenRefreshFailedEvent;
import com.sprint.mission.discodeit.infra.event.auth.TokenRefreshedEvent;
import lombok.RequiredArgsConstructor;
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
public class AuthService {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final CacheService cacheService;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserDto updateRole(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Role oldRole = user.getRole();
        Role newRole = request.newRole();
        user.updateRole(newRole);

        jwtRegistry.invalidateJwtInformationByUserId(userId);

        cacheService.evictCacheByKey("userDetails", user.getUsername());

        eventPublisher.publishEvent(new RoleUpdatedEvent(userId, user.getUsername(), oldRole, newRole));

        return userMapper.toDto(user);
    }

    @Transactional
    public JwtInformation refreshToken(String refreshToken) {
        DiscodeitUserDetails userDetails = validateAndGetUserDetails(refreshToken);

        try {
            JwtInformation newJwtInformation = generateNewTokens(userDetails);
            jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

            eventPublisher.publishEvent(new TokenRefreshedEvent(userDetails.getUserDetailsDto()));

            return newJwtInformation;
        } catch (JOSEException e) {
            eventPublisher.publishEvent(new TokenRefreshFailedEvent(userDetails.getUsername(), "Token generation failed"));
            throw new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    private DiscodeitUserDetails validateAndGetUserDetails(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)
            || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            eventPublisher.publishEvent(new TokenRefreshFailedEvent(null, "Invalid or expired refresh token"));
            throw new InvalidTokenException();
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
            eventPublisher.publishEvent(new TokenRefreshFailedEvent(username, "Invalid user details type"));
            throw new InvalidTokenException();
        }

        return discodeitUserDetails;
    }

    private JwtInformation generateNewTokens(DiscodeitUserDetails userDetails) throws JOSEException {
        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return new JwtInformation(
            userDetails.getUserDetailsDto(),
            newAccessToken,
            newRefreshToken
        );
    }
}
