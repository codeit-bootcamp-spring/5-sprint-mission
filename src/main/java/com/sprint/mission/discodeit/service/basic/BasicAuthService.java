package com.sprint.mission.discodeit.service.basic;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.dto.request.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public UserResponse updateUserRole(UserRoleUpdateRequest request) {
        log.info("[AuthService] 사용자 권한 수정 시도: {}", request);

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> UserNotFoundException.withId(request.userId()));

        Role oldRole = user.getRole();

        user.updateRole(request.newRole());
        userRepository.save(user);

        jwtRegistry.invalidateJwtInformationByUserId(user.getId());

        eventPublisher.publishEvent(new RoleUpdatedEvent(
                user.getId(),
                oldRole,
                request.newRole()
        ));
        log.info("[AuthService] RoleUpdatedEvent 발행 - userId: {}, {} -> {}",
                user.getId(), oldRole, request.newRole());

        log.info("[AuthService] 사용자 권한 수정 완료: {} -> {}", user.getUsername(), request.newRole());

        return UserResponse.success(user);
    }

    @Override
    @Transactional(readOnly = true)
    public JwtInformation refreshToken(String refreshToken) {
        log.info("[AuthService] 토큰 재발급 시도");

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            log.warn("[AuthService] 유효하지 않은 Refresh Token");
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            log.warn("[AuthService] Registry에 없는 Refresh Token");
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        UserResponse userResponse = UserResponse.success(user);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userResponse, user.getPassword());

        try {
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            JwtInformation newJwtInformation = new JwtInformation(
                    userResponse,
                    newAccessToken,
                    newRefreshToken
            );

            jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

            log.info("[AuthService] 토큰 재발급 성공: userId={}", userId);
            return newJwtInformation;
        } catch (JOSEException e) {
            log.error("[AuthService] 토큰 생성 실패: {}", e.getMessage());
            throw new RuntimeException("토큰 생성에 실패했습니다.", e);
        }
    }
}