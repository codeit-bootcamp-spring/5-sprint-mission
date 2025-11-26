package com.sprint.mission.discodeit.service;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtRegistry jwtRegistry;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("updateRoleWithoutAuth - 성공: 사용자 권한을 CHANNEL_MANAGER로 변경")
    void updateRoleWithoutAuth_ToChannelManager_Success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("testuser", "test@example.com", "encodedPassword", null);
        RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.CHANNEL_MANAGER);

        UserDto expectedDto = new UserDto(
            userId,
            "testuser",
            "test@example.com",
            null,
            true,
            Role.CHANNEL_MANAGER
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        willDoNothing().given(jwtRegistry).invalidateJwtInformationByUserId(userId);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = authService.updateRoleWithoutAuth(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(Role.CHANNEL_MANAGER);

        then(userRepository).should().findById(userId);
        then(jwtRegistry).should().invalidateJwtInformationByUserId(userId);
        then(userMapper).should().toDto(user);
    }

    @Test
    @DisplayName("updateRoleWithoutAuth - 성공: 사용자 권한을 ADMIN으로 변경")
    void updateRoleWithoutAuth_ToAdmin_Success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("testuser", "test@example.com", "encodedPassword", null);
        RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.ADMIN);

        UserDto expectedDto = new UserDto(
            userId,
            "testuser",
            "test@example.com",
            null,
            true,
            Role.ADMIN
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        willDoNothing().given(jwtRegistry).invalidateJwtInformationByUserId(userId);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = authService.updateRoleWithoutAuth(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(Role.ADMIN);

        then(userRepository).should().findById(userId);
        then(jwtRegistry).should().invalidateJwtInformationByUserId(userId);
        then(userMapper).should().toDto(user);
    }

    @Test
    @DisplayName("updateRoleWithoutAuth - 성공: ADMIN에서 USER로 권한 강등")
    void updateRoleWithoutAuth_DemoteToUser_Success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("adminuser", "admin@example.com", "encodedPassword", null);
        user.updateRole(Role.ADMIN);
        RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.USER);

        UserDto expectedDto = new UserDto(
            userId,
            "adminuser",
            "admin@example.com",
            null,
            true,
            Role.USER
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        willDoNothing().given(jwtRegistry).invalidateJwtInformationByUserId(userId);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = authService.updateRoleWithoutAuth(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(Role.USER);

        then(userRepository).should().findById(userId);
        then(jwtRegistry).should().invalidateJwtInformationByUserId(userId);
        then(userMapper).should().toDto(user);
    }

    @Test
    @DisplayName("updateRoleWithoutAuth - 실패: 존재하지 않는 사용자")
    void updateRoleWithoutAuth_UserNotFound_ThrowsException() {
        // given
        UUID userId = UUID.randomUUID();
        RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.CHANNEL_MANAGER);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.updateRoleWithoutAuth(request))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(jwtRegistry).shouldHaveNoInteractions();
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("updateRoleWithoutAuth - 성공: 권한 변경 후 JWT 무효화 확인")
    void updateRoleWithoutAuth_InvalidatesJwt() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("testuser", "test@example.com", "encodedPassword", null);
        RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.CHANNEL_MANAGER);

        UserDto expectedDto = new UserDto(
            userId,
            "testuser",
            "test@example.com",
            null,
            false,
            Role.CHANNEL_MANAGER
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        willDoNothing().given(jwtRegistry).invalidateJwtInformationByUserId(userId);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        authService.updateRoleWithoutAuth(request);

        // then
        then(jwtRegistry).should().invalidateJwtInformationByUserId(userId);
    }

    @Nested
    @DisplayName("refreshToken 테스트")
    class RefreshTokenTest {

        @Test
        @DisplayName("refreshToken - 성공: 유효한 리프레시 토큰으로 새 토큰 발급")
        void refreshToken_ValidToken_Success() throws JOSEException {
            // given
            String refreshToken = "valid-refresh-token";
            String newAccessToken = "new-access-token";
            String newRefreshToken = "new-refresh-token";
            String username = "testuser";
            UUID userId = UUID.randomUUID();

            UserDto userDto = new UserDto(
                userId,
                username,
                "test@example.com",
                null,
                true,
                Role.USER
            );
            DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "encodedPassword");

            given(tokenProvider.validateRefreshToken(refreshToken)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)).willReturn(true);
            given(tokenProvider.getUsernameFromToken(refreshToken)).willReturn(username);
            given(userDetailsService.loadUserByUsername(username)).willReturn(userDetails);
            given(tokenProvider.generateAccessToken(userDetails)).willReturn(newAccessToken);
            given(tokenProvider.generateRefreshToken(userDetails)).willReturn(newRefreshToken);
            willDoNothing().given(jwtRegistry).rotateJwtInformation(eq(refreshToken), any(JwtInformation.class));

            // when
            JwtInformation result = authService.refreshToken(refreshToken);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo(newAccessToken);
            assertThat(result.getRefreshToken()).isEqualTo(newRefreshToken);
            assertThat(result.getUserDto()).isEqualTo(userDto);

            then(tokenProvider).should().validateRefreshToken(refreshToken);
            then(jwtRegistry).should().hasActiveJwtInformationByRefreshToken(refreshToken);
            then(jwtRegistry).should().rotateJwtInformation(eq(refreshToken), any(JwtInformation.class));
        }

        @Test
        @DisplayName("refreshToken - 실패: 유효하지 않은 리프레시 토큰")
        void refreshToken_InvalidToken_ThrowsException() {
            // given
            String invalidRefreshToken = "invalid-refresh-token";
            given(tokenProvider.validateRefreshToken(invalidRefreshToken)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(invalidRefreshToken))
                .isInstanceOf(DiscodeitException.class)
                .extracting(e -> ((DiscodeitException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_TOKEN);

            then(tokenProvider).should().validateRefreshToken(invalidRefreshToken);
            then(jwtRegistry).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("refreshToken - 실패: 레지스트리에 없는 리프레시 토큰")
        void refreshToken_TokenNotInRegistry_ThrowsException() {
            // given
            String refreshToken = "not-registered-token";
            given(tokenProvider.validateRefreshToken(refreshToken)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(DiscodeitException.class)
                .extracting(e -> ((DiscodeitException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_TOKEN);

            then(tokenProvider).should().validateRefreshToken(refreshToken);
            then(jwtRegistry).should().hasActiveJwtInformationByRefreshToken(refreshToken);
        }

        @Test
        @DisplayName("refreshToken - 실패: UserDetails가 DiscodeitUserDetails가 아닌 경우")
        void refreshToken_NotDiscodeitUserDetails_ThrowsException() {
            // given
            String refreshToken = "valid-refresh-token";
            String username = "testuser";

            org.springframework.security.core.userdetails.User plainUserDetails =
                new org.springframework.security.core.userdetails.User(
                    username, "password", Collections.emptyList()
                );

            given(tokenProvider.validateRefreshToken(refreshToken)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)).willReturn(true);
            given(tokenProvider.getUsernameFromToken(refreshToken)).willReturn(username);
            given(userDetailsService.loadUserByUsername(username)).willReturn(plainUserDetails);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(DiscodeitException.class)
                .extracting(e -> ((DiscodeitException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_TOKEN);
        }

        @Test
        @DisplayName("refreshToken - 실패: 토큰 생성 중 JOSEException 발생")
        void refreshToken_JOSEException_ThrowsException() throws JOSEException {
            // given
            String refreshToken = "valid-refresh-token";
            String username = "testuser";
            UUID userId = UUID.randomUUID();

            UserDto userDto = new UserDto(
                userId,
                username,
                "test@example.com",
                null,
                true,
                Role.USER
            );
            DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "encodedPassword");

            given(tokenProvider.validateRefreshToken(refreshToken)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)).willReturn(true);
            given(tokenProvider.getUsernameFromToken(refreshToken)).willReturn(username);
            given(userDetailsService.loadUserByUsername(username)).willReturn(userDetails);
            willThrow(new JOSEException("Token generation failed"))
                .given(tokenProvider).generateAccessToken(userDetails);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(DiscodeitException.class)
                .extracting(e -> ((DiscodeitException) e).getErrorCode())
                .isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
