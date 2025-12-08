package com.sprint.mission.discodeit.auth.application;

import com.sprint.mission.discodeit.auth.domain.RoleUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.TokenRefreshEvent;
import com.sprint.mission.discodeit.auth.domain.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.auth.domain.exception.InvalidTokenException;
import com.sprint.mission.discodeit.auth.domain.exception.MissingRefreshTokenCookieException;
import com.sprint.mission.discodeit.auth.presentation.dto.RoleUpdateRequest;
import com.sprint.mission.discodeit.common.domain.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.cache.CacheName;
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
import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @Value("${discodeit.jwt.refresh-token-cookie-name}")
    private String refreshTokenCookieName;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;

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
        user.updateRole(request.newRole());

        UserDto result = userMapper.toDto(user);

        jwtRegistry.invalidateJwtInformationByUserId(result.id());
        eventPublisher.publishEvent(new RoleUpdatedEvent(
            result.id(),
            result.username(),
            oldRole,
            result.role()
        ));

        log.info("Role updated: [userId={}, oldRole={}, newRole={}]", result.id(), oldRole, result.role());

        return result;
    }

    public JwtDto refreshToken(HttpServletRequest request) {
        String ipAddress = "N/A";
        String userAgent = "N/A";
        String refreshToken = null;

        try {
            ipAddress = extractIpAddress(request);
            userAgent = extractUserAgent(request);

            log.info("Attempting to refresh token from: [ipAddress={}, userAgent={}]", ipAddress, userAgent);

            refreshToken = extractRefreshTokenFromCookie(request);

            return processTokenRefresh(refreshToken, ipAddress, userAgent);
        } catch (Exception exception) {
            handleRefreshFailure(exception, refreshToken, ipAddress, userAgent);
            throw exception;
        }
    }

    private JwtDto processTokenRefresh(
        String refreshToken,
        String ipAddress,
        String userAgent
    ) {
        DiscodeitUserDetails userDetails = validateAndGetUserDetails(refreshToken);
        JwtDto newTokens = generateNewTokens(userDetails);

        jwtRegistry.rotateJwtInformation(refreshToken, newTokens);

        eventPublisher.publishEvent(
            new TokenRefreshEvent(
                userDetails.getUserDetailsDto().id(),
                userDetails.getUsername(),
                ipAddress,
                userAgent
            )
        );

        log.info("Token refreshed: [userId={}, username={}, ipAddress={}, userAgent={}]",
            userDetails.getUserDetailsDto().id(), userDetails.getUsername(), ipAddress, userAgent);

        return newTokens;
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, refreshTokenCookieName);
        if (cookie == null || cookie.getValue() == null || cookie.getValue().isBlank()) {
            throw new MissingRefreshTokenCookieException();
        }

        return cookie.getValue();
    }

    private DiscodeitUserDetails validateAndGetUserDetails(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken) ||
            !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
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

    private void handleRefreshFailure(
        Exception exception,
        String refreshToken,
        String ipAddress,
        String userAgent
    ) {
        UUID userId = null;
        String username = "N/A";
        if (hasText(refreshToken)) {
            try {
                userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
                username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            } catch (Exception ex) {
                log.debug("Invalid claims in refresh token during failure handling: {}", ex.getMessage());
            }
        }

        String reason = (exception instanceof DiscodeitException de)
            ? de.getErrorCode().getMessage()
            : "Unexpected error: " + exception.getClass().getSimpleName();

        eventPublisher.publishEvent(
            new TokenRefreshFailureEvent(
                userId,
                username,
                ipAddress,
                userAgent,
                reason
            )
        );

        log.warn("Token refresh failed: [userId={}, username={}, ipAddress={}, userAgent={}, reason={}]",
            userId, username, ipAddress, userAgent, reason);
    }
}
