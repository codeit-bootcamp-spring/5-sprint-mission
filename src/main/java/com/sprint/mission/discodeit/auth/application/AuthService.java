package com.sprint.mission.discodeit.auth.application;

import com.sprint.mission.discodeit.auth.domain.RoleUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.TokenRefreshEvent;
import com.sprint.mission.discodeit.auth.domain.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.auth.domain.exception.InvalidTokenException;
import com.sprint.mission.discodeit.auth.presentation.dto.RoleUpdateRequest;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.security.jwt.JwtCookieProvider;
import com.sprint.mission.discodeit.global.security.jwt.JwtDto;
import com.sprint.mission.discodeit.global.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.user.application.UserMapper;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import java.util.UUID;

import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractIpAddress;
import static com.sprint.mission.discodeit.global.util.RequestExtractor.extractUserAgent;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final String REFRESH_FAILURE_REASON_MISSING_COOKIE = "MISSING_REFRESH_TOKEN_COOKIE";
    private static final String REFRESH_FAILURE_REASON_INVALID_TOKEN = "INVALID_REFRESH_TOKEN";
    private static final String REFRESH_FAILURE_REASON_UNEXPECTED_ERROR = "UNEXPECTED_ERROR";

    @Value("${discodeit.jwt.refresh-token-cookie-name}")
    private String refreshTokenCookieName;

    private final JwtCookieProvider jwtCookieProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheName.USERS, allEntries = true),
        @CacheEvict(value = CacheName.USER_DETAILS, key = "#result.username")
    })
    public UserDto updateRole(RoleUpdateRequest request) {
        log.info("Attempting to update role: [userId={}: newRole={}]", request.userId(), request.newRole());

        UUID userId = request.userId();

        User user = userRepository.findWithProfileById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Role oldRole = user.getRole();
        Role newRole = request.newRole();
        user.updateRole(newRole);

        UserDto result = userMapper.toDto(user);

        jwtRegistry.invalidateJwtInformationByUserId(userId);
        eventPublisher.publishEvent(new RoleUpdatedEvent(userId, user.getUsername(), oldRole, newRole));

        log.info("Role updated: [userId={}, oldRole={}, newRole={}]", userId, oldRole, newRole);

        return result;
    }

    public JwtDto refreshToken(HttpServletRequest request) {
        String ipAddress = extractIpAddress(request);
        String userAgent = extractUserAgent(request);

        log.info("Attempting to refresh token from: [ipAddress={}, userAgent={}]", ipAddress, userAgent);

        String refreshToken = null;
        String eventUsername = null;
        UUID eventUserId = null;

        try {
            refreshToken = extractRefreshTokenFromCookie(request, refreshTokenCookieName);

            DiscodeitUserDetails userDetails = validateAndGetUserDetails(refreshToken);

            eventUsername = userDetails.getUsername();
            eventUserId = userDetails.getUserDetailsDto().id();

            JwtDto jwtDto = generateNewTokens(userDetails);
            jwtRegistry.rotateJwtInformation(refreshToken, jwtDto);

            publishTokenRefreshEvent(userDetails, ipAddress, userAgent);

            log.info("Token refreshed: [userId={}, username={}, ipAddress={}, userAgent={}",
                userDetails.getUserDetailsDto().id(), userDetails.getUsername(), ipAddress, userAgent);

            return jwtDto;
        } catch (Exception e) {
            handleRefreshFailure(e, refreshToken, eventUserId, eventUsername, ipAddress, userAgent);
            throw e;
        }
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null || cookie.getValue() == null || cookie.getValue().isBlank()) {
            throw new InvalidTokenException();
        }

        return cookie.getValue();
    }

    private DiscodeitUserDetails validateAndGetUserDetails(String refreshToken) {
        boolean validSignature = jwtTokenProvider.validateRefreshToken(refreshToken);
        boolean activeInRegistry = jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken);

        if (!validSignature || !activeInRegistry) {
            throw new InvalidTokenException();
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
            throw new InvalidTokenException(username);
        }

        return discodeitUserDetails;
    }

    private JwtDto generateNewTokens(DiscodeitUserDetails userDetails) {
        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return new JwtDto(
            userDetails.getUserDetailsDto(),
            newAccessToken,
            newRefreshToken
        );
    }

    private String determineFailureReason(String refreshToken) {
        return (refreshToken == null)
            ? REFRESH_FAILURE_REASON_MISSING_COOKIE
            : REFRESH_FAILURE_REASON_INVALID_TOKEN;
    }

    private String resolveUsername(String currentUsername, String refreshToken) {
        if (currentUsername != null || refreshToken == null) {
            return currentUsername;
        }
        return tryExtractUsernameSafely(refreshToken);
    }

    private String tryExtractUsernameSafely(String refreshToken) {
        try {
            return jwtTokenProvider.getUsernameFromToken(refreshToken);
        } catch (Exception ignored) {
            return "N/A";
        }
    }

    private void handleRefreshFailure(
        Exception e,
        String refreshToken,
        UUID eventUserId,
        String eventUsername,
        String ipAddress,
        String userAgent
    ) {
        String reason = (e instanceof InvalidTokenException)
            ? determineFailureReason(refreshToken)
            : REFRESH_FAILURE_REASON_UNEXPECTED_ERROR;

        eventUsername = resolveUsername(eventUsername, refreshToken);

        publishTokenRefreshFailureEvent(eventUserId, eventUsername, ipAddress, userAgent, reason);

        if (e instanceof InvalidTokenException) {
            log.warn("Token refresh failed: [userId={}, username={}, ipAddress={}, userAgent={}, reason={}]",
                eventUserId, eventUsername, ipAddress, userAgent, reason);
        } else {
            log.error("Token refresh failed: [userId={}, username={}, ipAddress={}, userAgent={}, reason={}]",
                eventUserId, eventUsername, ipAddress, userAgent, reason, e);
        }
    }


    private void publishTokenRefreshEvent(
        DiscodeitUserDetails userDetails,
        String ipAddress,
        String userAgent
    ) {
        eventPublisher.publishEvent(
            new TokenRefreshEvent(
                userDetails.getUserDetailsDto().id(),
                userDetails.getUsername(),
                ipAddress,
                userAgent
            )
        );
    }

    private void publishTokenRefreshFailureEvent(
        UUID userId,
        String username,
        String ipAddress,
        String userAgent,
        String reason
    ) {
        eventPublisher.publishEvent(
            new TokenRefreshFailureEvent(
                userId,
                username,
                ipAddress,
                userAgent,
                reason
            )
        );
    }
}
