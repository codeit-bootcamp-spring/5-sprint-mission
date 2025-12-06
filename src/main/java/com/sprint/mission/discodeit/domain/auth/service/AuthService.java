package com.sprint.mission.discodeit.domain.auth.service;

import com.sprint.mission.discodeit.common.cache.CacheHelper;
import com.sprint.mission.discodeit.common.cache.CacheType;
import com.sprint.mission.discodeit.common.security.jwt.JwtCookieProvider;
import com.sprint.mission.discodeit.common.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.common.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.common.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.domain.auth.dto.JwtDto;
import com.sprint.mission.discodeit.domain.auth.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.domain.auth.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.domain.auth.exception.InvalidTokenException;
import com.sprint.mission.discodeit.domain.user.dto.UserDto;
import com.sprint.mission.discodeit.domain.user.entity.Role;
import com.sprint.mission.discodeit.domain.user.entity.User;
import com.sprint.mission.discodeit.domain.user.exception.UserNotFoundException;
import com.sprint.mission.discodeit.domain.user.mapper.UserMapper;
import com.sprint.mission.discodeit.domain.user.repository.UserRepository;
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
    public JwtDto refreshToken(HttpServletRequest request) {
        String cookieName = jwtCookieProvider.getRefreshTokenCookieName();
        Cookie cookie = WebUtils.getCookie(request, cookieName);

        if (cookie == null) {
            throw new InvalidTokenException();
        }

        String refreshToken = cookie.getValue();
        DiscodeitUserDetails userDetails = validateAndGetUserDetails(refreshToken);
        JwtDto newJwtDto = generateNewTokens(userDetails);
        jwtRegistry.rotateJwtInformation(refreshToken, newJwtDto);

        return newJwtDto;
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

    private JwtDto generateNewTokens(DiscodeitUserDetails userDetails) {
        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return new JwtDto(
            userDetails.getUserDetailsDto(),
            newAccessToken,
            newRefreshToken
        );
    }
}
