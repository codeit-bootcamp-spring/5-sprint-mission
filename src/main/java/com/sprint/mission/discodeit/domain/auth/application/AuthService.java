package com.sprint.mission.discodeit.domain.auth.application;

import com.sprint.mission.discodeit.domain.auth.domain.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.domain.auth.domain.event.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.domain.auth.domain.event.TokenRefreshSuccessEvent;
import com.sprint.mission.discodeit.domain.auth.domain.exception.InvalidTokenException;
import com.sprint.mission.discodeit.domain.auth.presentation.dto.JwtDto;
import com.sprint.mission.discodeit.domain.auth.presentation.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.domain.user.application.UserMapper;
import com.sprint.mission.discodeit.domain.user.domain.Role;
import com.sprint.mission.discodeit.domain.user.domain.User;
import com.sprint.mission.discodeit.domain.user.domain.UserRepository;
import com.sprint.mission.discodeit.domain.user.domain.exception.UserNotFoundException;
import com.sprint.mission.discodeit.domain.user.presentation.dto.UserDto;
import com.sprint.mission.discodeit.global.cache.CacheHelper;
import com.sprint.mission.discodeit.global.cache.CacheName;
import com.sprint.mission.discodeit.global.security.jwt.JwtCookieProvider;
import com.sprint.mission.discodeit.global.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
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

            publishTokenRefreshSuccessEvent(userDetails, ipAddress, userAgent);

            return jwtDto;
        } catch (InvalidTokenException invalidTokenException) {
            if (eventUsername == null && refreshToken != null) {
                eventUsername = tryExtractUsernameSafely(refreshToken);
            }

            String reason = (refreshToken == null)
                ? REFRESH_FAILURE_REASON_MISSING_COOKIE
                : REFRESH_FAILURE_REASON_INVALID_TOKEN;

            publishTokenRefreshFailureEvent(
                eventUserId,
                eventUsername,
                ipAddress,
                userAgent,
                reason
            );

            throw invalidTokenException;
        } catch (Exception exception) {
            if (eventUsername == null && refreshToken != null) {
                eventUsername = tryExtractUsernameSafely(refreshToken);
            }

            publishTokenRefreshFailureEvent(
                eventUserId,
                eventUsername,
                ipAddress,
                userAgent,
                REFRESH_FAILURE_REASON_UNEXPECTED_ERROR
            );

            throw exception;
        }
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

    private void publishTokenRefreshSuccessEvent(
        DiscodeitUserDetails userDetails,
        String ipAddress,
        String userAgent
    ) {
        UUID userId = userDetails.getUserDetailsDto().id();

        eventPublisher.publishEvent(
            new TokenRefreshSuccessEvent(
                userId,
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
