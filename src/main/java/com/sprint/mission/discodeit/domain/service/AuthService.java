package com.sprint.mission.discodeit.domain.service;

import com.sprint.mission.discodeit.common.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.common.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.common.security.jwt.JwtCookieProvider;
import com.sprint.mission.discodeit.common.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.common.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.common.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.domain.dto.auth.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.domain.dto.jwt.data.JwtInformation;
import com.sprint.mission.discodeit.domain.dto.user.data.UserDto;
import com.sprint.mission.discodeit.domain.entity.Role;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.domain.mapper.UserMapper;
import com.sprint.mission.discodeit.domain.repository.UserRepository;
import com.sprint.mission.discodeit.infra.cache.CacheHelper;
import com.sprint.mission.discodeit.infra.cache.CacheType;
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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final UserDetailsService userDetailsService;

    private final ApplicationEventPublisher eventPublisher;

    private final CacheHelper cacheHelper;

    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtCookieProvider jwtCookieProvider;

    private final UserMapper userMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CacheEvict(value = CacheType.USERS, allEntries = true)
    public UserDto updateRole(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Role oldRole = user.getRole();
        Role newRole = request.newRole();
        user.updateRole(newRole);

        jwtRegistry.invalidateJwtInformationByUserId(userId);

        cacheHelper.evictCacheByKey("userDetails", user.getUsername());

        eventPublisher.publishEvent(new RoleUpdatedEvent(userId, user.getUsername(), oldRole, newRole));

        return userMapper.toDto(user);
    }

    @Transactional
    public JwtInformation refreshToken(HttpServletRequest request) {
        String cookieName = jwtCookieProvider.getRefreshTokenCookieName();
        Cookie cookie = WebUtils.getCookie(request, cookieName);

        if (cookie == null) {
            throw new InvalidTokenException();
        }

        String refreshToken = cookie.getValue();
        DiscodeitUserDetails userDetails = validateAndGetUserDetails(refreshToken);
        JwtInformation newJwtInformation = generateNewTokens(userDetails);
        jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

        return newJwtInformation;
    }

    private DiscodeitUserDetails validateAndGetUserDetails(String refreshToken) {
        if (!(jwtTokenProvider.validateRefreshToken(refreshToken)
            && jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken))) {
            throw new InvalidTokenException();
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
            throw new InvalidTokenException(username);
        }

        return discodeitUserDetails;
    }

    private JwtInformation generateNewTokens(DiscodeitUserDetails userDetails) {
        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return new JwtInformation(
            userDetails.getUserDetailsDto(),
            newAccessToken,
            newRefreshToken
        );
    }
}
