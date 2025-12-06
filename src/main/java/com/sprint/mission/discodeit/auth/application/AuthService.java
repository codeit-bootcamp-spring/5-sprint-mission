package com.sprint.mission.discodeit.auth.application;

import com.sprint.mission.discodeit.auth.domain.RoleUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.TokenRefreshEvent;
import com.sprint.mission.discodeit.auth.domain.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.auth.domain.exception.InvalidTokenException;
import com.sprint.mission.discodeit.auth.presentation.dto.JwtDto;
import com.sprint.mission.discodeit.auth.presentation.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.global.cache.CacheHelper;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.security.jwt.JwtCookieProvider;
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
import org.springframework.cache.annotation.CacheEvict;
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
public class AuthService {

    private static final String REFRESH_FAILURE_REASON_MISSING_COOKIE = "MISSING_REFRESH_TOKEN_COOKIE";
    private static final String REFRESH_FAILURE_REASON_INVALID_TOKEN = "INVALID_REFRESH_TOKEN";
    private static final String REFRESH_FAILURE_REASON_UNEXPECTED_ERROR = "UNEXPECTED_ERROR";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CacheHelper cacheHelper;
    private final JwtCookieProvider jwtCookieProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;
    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CacheEvict(value = CacheName.USERS, allEntries = true)
    public UserDto updateRole(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Role oldRole = user.getRole();
        Role newRole = request.newRole();
        user.updateRole(newRole);

        jwtRegistry.invalidateJwtInformationByUserId(userId);

        cacheHelper.evictCacheByKey(CacheName.USER_DETAILS, user.getUsername());

        eventPublisher.publishEvent(new RoleUpdatedEvent(userId, user.getUsername(), oldRole, newRole));

        return userMapper.toDto(user);
    }

    public JwtDto refreshToken(HttpServletRequest request) {
        String ipAddress = extractIpAddress(request);
        String userAgent = extractUserAgent(request);
        String cookieName = jwtCookieProvider.getRefreshTokenCookieName();

        String refreshToken = null;
        String eventUsername = null;
        UUID eventUserId = null;

        try {
            refreshToken = extractRefreshTokenFromCookie(request, cookieName);

            DiscodeitUserDetails userDetails = validateAndGetUserDetails(refreshToken);

            eventUsername = userDetails.getUsername();
            eventUserId = userDetails.getUserDetailsDto().id();

            JwtDto jwtDto = generateNewTokens(userDetails);
            jwtRegistry.rotateJwtInformation(refreshToken, jwtDto);

            publishTokenRefreshEvent(userDetails, ipAddress, userAgent);

            return jwtDto;
        } catch (InvalidTokenException e) {
            String reason = determineFailureReason(refreshToken);
            eventUsername = resolveUsername(eventUsername, refreshToken);

            publishTokenRefreshFailureEvent(eventUserId, eventUsername, ipAddress, userAgent, reason);
            throw e;
        } catch (Exception e) {
            eventUsername = resolveUsername(eventUsername, refreshToken);

            publishTokenRefreshFailureEvent(
                eventUserId, eventUsername, ipAddress, userAgent, REFRESH_FAILURE_REASON_UNEXPECTED_ERROR
            );
            throw e;
        }
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

    private String extractRefreshTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null || cookie.getValue() == null || cookie.getValue().isBlank()) {
            throw new InvalidTokenException();
        }
        return cookie.getValue();
    }

    private String tryExtractUsernameSafely(String refreshToken) {
        try {
            return jwtTokenProvider.getUsernameFromToken(refreshToken);
        } catch (Exception ignored) {
            return null;
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
