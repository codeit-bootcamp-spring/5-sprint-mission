package com.sprint.mission.discodeit.domain.auth.service;

import com.sprint.mission.discodeit.domain.auth.application.AuthService;
import com.sprint.mission.discodeit.domain.auth.domain.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.domain.auth.domain.event.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.domain.auth.domain.event.TokenRefreshSuccessEvent;
import com.sprint.mission.discodeit.domain.auth.domain.exception.InvalidTokenException;
import com.sprint.mission.discodeit.domain.auth.presentation.dto.JwtDto;
import com.sprint.mission.discodeit.domain.auth.presentation.dto.UserDetailsDto;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CacheHelper cacheHelper;

    @Mock
    private JwtCookieProvider jwtCookieProvider;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtRegistry jwtRegistry;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "$2a$10$encrypted";
    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    @Nested
    @DisplayName("updateRole 메서드")
    class UpdateRole {

        private User testUser;
        private UserDto testUserDto;

        @BeforeEach
        void setUp() {
            testUser = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, null);
            ReflectionTestUtils.setField(testUser, "id", TEST_USER_ID);

            testUserDto = new UserDto(
                TEST_USER_ID, TEST_USERNAME, TEST_EMAIL, null, true, Role.CHANNEL_MANAGER
            );
        }

        @Test
        @DisplayName("유효한 사용자 ID로 권한 변경 시 성공")
        void updateRole_withValidUserId_updatesRoleSuccessfully() {
            // given
            RoleUpdateRequest request = new RoleUpdateRequest(TEST_USER_ID, Role.CHANNEL_MANAGER);

            given(userRepository.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userMapper.toDto(testUser)).willReturn(testUserDto);

            // when
            UserDto result = authService.updateRole(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(TEST_USER_ID);
            assertThat(result.role()).isEqualTo(Role.CHANNEL_MANAGER);

            then(jwtRegistry).should().invalidateJwtInformationByUserId(TEST_USER_ID);
            then(cacheHelper).should().evictCacheByKey(CacheName.USER_DETAILS, TEST_USERNAME);
        }

        @Test
        @DisplayName("권한 변경 시 RoleUpdatedEvent 이벤트 발행")
        void updateRole_publishesRoleUpdatedEvent() {
            // given
            RoleUpdateRequest request = new RoleUpdateRequest(TEST_USER_ID, Role.CHANNEL_MANAGER);
            Role oldRole = testUser.getRole();

            given(userRepository.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userMapper.toDto(testUser)).willReturn(testUserDto);

            ArgumentCaptor<RoleUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(RoleUpdatedEvent.class);

            // when
            authService.updateRole(request);

            // then
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            RoleUpdatedEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.userId()).isEqualTo(TEST_USER_ID);
            assertThat(capturedEvent.username()).isEqualTo(TEST_USERNAME);
            assertThat(capturedEvent.oldRole()).isEqualTo(oldRole);
            assertThat(capturedEvent.newRole()).isEqualTo(Role.CHANNEL_MANAGER);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 요청 시 UserNotFoundException 발생")
        void updateRole_withNonExistingUserId_throwsUserNotFoundException() {
            // given
            UUID nonExistingUserId = UUID.randomUUID();
            RoleUpdateRequest request = new RoleUpdateRequest(nonExistingUserId, Role.CHANNEL_MANAGER);

            given(userRepository.findById(nonExistingUserId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.updateRole(request))
                .isInstanceOf(UserNotFoundException.class);

            then(jwtRegistry).should(never()).invalidateJwtInformationByUserId(any());
            then(cacheHelper).should(never()).evictCacheByKey(any(), any());
            then(eventPublisher).should(never()).publishEvent(any());
        }

        @Test
        @DisplayName("ADMIN에서 USER로 권한 변경 시 성공")
        void updateRole_fromAdminToUser_updatesSuccessfully() {
            // given
            User adminUser = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, null);
            ReflectionTestUtils.setField(adminUser, "id", TEST_USER_ID);
            ReflectionTestUtils.setField(adminUser, "role", Role.ADMIN);

            RoleUpdateRequest request = new RoleUpdateRequest(TEST_USER_ID, Role.USER);
            UserDto updatedUserDto = new UserDto(
                TEST_USER_ID, TEST_USERNAME, TEST_EMAIL, null, true, Role.USER
            );

            given(userRepository.findById(TEST_USER_ID)).willReturn(Optional.of(adminUser));
            given(userMapper.toDto(adminUser)).willReturn(updatedUserDto);

            // when
            UserDto result = authService.updateRole(request);

            // then
            assertThat(result.role()).isEqualTo(Role.USER);
            then(jwtRegistry).should().invalidateJwtInformationByUserId(TEST_USER_ID);
        }
    }

    @Nested
    @DisplayName("refreshToken 메서드")
    class RefreshToken {

        @Mock
        private HttpServletRequest request;

        private DiscodeitUserDetails userDetails;
        private UserDetailsDto userDetailsDto;
        private static final String OLD_REFRESH_TOKEN = "old-refresh-token";
        private static final String NEW_ACCESS_TOKEN = "new-access-token";
        private static final String NEW_REFRESH_TOKEN = "new-refresh-token";
        private static final String TEST_IP = "127.0.0.1";
        private static final String TEST_USER_AGENT = "Mozilla/5.0";

        @BeforeEach
        void setUp() {
            userDetailsDto = new UserDetailsDto(TEST_USER_ID, TEST_USERNAME, Role.USER);
            userDetails = new DiscodeitUserDetails(userDetailsDto, TEST_PASSWORD);
        }

        @Test
        @DisplayName("유효한 리프레시 토큰으로 요청 시 새 토큰 발급")
        void refreshToken_withValidToken_returnsNewTokens() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getHeader("X-Forwarded-For")).willReturn(null);
            given(request.getRemoteAddr()).willReturn(TEST_IP);
            given(request.getHeader("User-Agent")).willReturn(TEST_USER_AGENT);
            given(jwtCookieProvider.getRefreshTokenCookieName()).willReturn(REFRESH_COOKIE_NAME);
            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USERNAME);
            given(userDetailsService.loadUserByUsername(TEST_USERNAME)).willReturn(userDetails);

            given(jwtTokenProvider.generateAccessToken(userDetails)).willReturn(NEW_ACCESS_TOKEN);
            given(jwtTokenProvider.generateRefreshToken(userDetails)).willReturn(NEW_REFRESH_TOKEN);

            // when
            JwtDto result = authService.refreshToken(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);
            assertThat(result.refreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
            assertThat(result.userDetailsDto()).isEqualTo(userDetailsDto);

            then(jwtRegistry).should().rotateJwtInformation(eq(OLD_REFRESH_TOKEN), any(JwtDto.class));
        }

        @Test
        @DisplayName("성공 시 TokenRefreshSuccessEvent 이벤트 발행")
        void refreshToken_onSuccess_publishesSuccessEvent() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getHeader("X-Forwarded-For")).willReturn(null);
            given(request.getRemoteAddr()).willReturn(TEST_IP);
            given(request.getHeader("User-Agent")).willReturn(TEST_USER_AGENT);
            given(jwtCookieProvider.getRefreshTokenCookieName()).willReturn(REFRESH_COOKIE_NAME);
            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USERNAME);
            given(userDetailsService.loadUserByUsername(TEST_USERNAME)).willReturn(userDetails);

            given(jwtTokenProvider.generateAccessToken(userDetails)).willReturn(NEW_ACCESS_TOKEN);
            given(jwtTokenProvider.generateRefreshToken(userDetails)).willReturn(NEW_REFRESH_TOKEN);

            ArgumentCaptor<TokenRefreshSuccessEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshSuccessEvent.class);

            // when
            authService.refreshToken(request);

            // then
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshSuccessEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.userId()).isEqualTo(TEST_USER_ID);
            assertThat(capturedEvent.username()).isEqualTo(TEST_USERNAME);
            assertThat(capturedEvent.ipAddress()).isEqualTo(TEST_IP);
            assertThat(capturedEvent.userAgent()).isEqualTo(TEST_USER_AGENT);
        }

        @Test
        @DisplayName("쿠키가 없는 경우 InvalidTokenException 발생 및 MISSING_REFRESH_TOKEN_COOKIE 이유로 이벤트 발행")
        void refreshToken_withoutCookie_throwsInvalidTokenExceptionWithMissingCookieReason() {
            // given
            given(request.getHeader("X-Forwarded-For")).willReturn(null);
            given(request.getRemoteAddr()).willReturn(TEST_IP);
            given(request.getHeader("User-Agent")).willReturn(TEST_USER_AGENT);
            given(jwtCookieProvider.getRefreshTokenCookieName()).willReturn(REFRESH_COOKIE_NAME);
            given(request.getCookies()).willReturn(null);

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class);

            then(jwtRegistry).should(never()).rotateJwtInformation(any(), any());
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.userId()).isNull();
            assertThat(capturedEvent.username()).isNull();
            assertThat(capturedEvent.ipAddress()).isEqualTo(TEST_IP);
            assertThat(capturedEvent.userAgent()).isEqualTo(TEST_USER_AGENT);
            assertThat(capturedEvent.reason()).isEqualTo("MISSING_REFRESH_TOKEN_COOKIE");
        }

        @Test
        @DisplayName("쿠키 값이 빈 문자열인 경우 InvalidTokenException 발생")
        void refreshToken_withEmptyCookieValue_throwsInvalidTokenException() {
            // given
            Cookie emptyCookie = new Cookie(REFRESH_COOKIE_NAME, "");

            given(request.getHeader("X-Forwarded-For")).willReturn(null);
            given(request.getRemoteAddr()).willReturn(TEST_IP);
            given(request.getHeader("User-Agent")).willReturn(TEST_USER_AGENT);
            given(jwtCookieProvider.getRefreshTokenCookieName()).willReturn(REFRESH_COOKIE_NAME);
            given(request.getCookies()).willReturn(new Cookie[]{emptyCookie});

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class);

            then(eventPublisher).should().publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().reason()).isEqualTo("MISSING_REFRESH_TOKEN_COOKIE");
        }

        @Test
        @DisplayName("리프레시 토큰 서명이 유효하지 않은 경우 InvalidTokenException 발생")
        void refreshToken_withInvalidSignature_throwsInvalidTokenException() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getHeader("X-Forwarded-For")).willReturn(null);
            given(request.getRemoteAddr()).willReturn(TEST_IP);
            given(request.getHeader("User-Agent")).willReturn(TEST_USER_AGENT);
            given(jwtCookieProvider.getRefreshTokenCookieName()).willReturn(REFRESH_COOKIE_NAME);
            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(false);
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USERNAME);

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class);

            then(jwtRegistry).should(never()).rotateJwtInformation(any(), any());
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.username()).isEqualTo(TEST_USERNAME);
            assertThat(capturedEvent.reason()).isEqualTo("INVALID_REFRESH_TOKEN");
        }

        @Test
        @DisplayName("레지스트리에 없는 리프레시 토큰인 경우 InvalidTokenException 발생")
        void refreshToken_withTokenNotInRegistry_throwsInvalidTokenException() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getHeader("X-Forwarded-For")).willReturn(null);
            given(request.getRemoteAddr()).willReturn(TEST_IP);
            given(request.getHeader("User-Agent")).willReturn(TEST_USER_AGENT);
            given(jwtCookieProvider.getRefreshTokenCookieName()).willReturn(REFRESH_COOKIE_NAME);
            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(false);
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USERNAME);

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class);

            then(jwtRegistry).should(never()).rotateJwtInformation(any(), any());
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.username()).isEqualTo(TEST_USERNAME);
            assertThat(capturedEvent.reason()).isEqualTo("INVALID_REFRESH_TOKEN");
        }

        @Test
        @DisplayName("UserDetails가 DiscodeitUserDetails가 아닌 경우 InvalidTokenException 발생")
        void refreshToken_withNonDiscodeitUserDetails_throwsInvalidTokenException() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_COOKIE_NAME, OLD_REFRESH_TOKEN);
            org.springframework.security.core.userdetails.UserDetails standardUserDetails =
                mock(org.springframework.security.core.userdetails.UserDetails.class);

            given(request.getHeader("X-Forwarded-For")).willReturn(null);
            given(request.getRemoteAddr()).willReturn(TEST_IP);
            given(request.getHeader("User-Agent")).willReturn(TEST_USER_AGENT);
            given(jwtCookieProvider.getRefreshTokenCookieName()).willReturn(REFRESH_COOKIE_NAME);
            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USERNAME);
            given(userDetailsService.loadUserByUsername(TEST_USERNAME)).willReturn(standardUserDetails);

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class);

            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.username()).isEqualTo(TEST_USERNAME);
            assertThat(capturedEvent.reason()).isEqualTo("INVALID_REFRESH_TOKEN");
        }

        @Test
        @DisplayName("X-Forwarded-For 헤더가 있는 경우 해당 IP 사용")
        void refreshToken_withXForwardedForHeader_usesProxiedIp() {
            // given
            String proxiedIp = "192.168.1.1";
            Cookie refreshCookie = new Cookie(REFRESH_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getHeader("X-Forwarded-For")).willReturn(proxiedIp + ", 10.0.0.1");
            given(request.getHeader("User-Agent")).willReturn(TEST_USER_AGENT);
            given(jwtCookieProvider.getRefreshTokenCookieName()).willReturn(REFRESH_COOKIE_NAME);
            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USERNAME);
            given(userDetailsService.loadUserByUsername(TEST_USERNAME)).willReturn(userDetails);

            given(jwtTokenProvider.generateAccessToken(userDetails)).willReturn(NEW_ACCESS_TOKEN);
            given(jwtTokenProvider.generateRefreshToken(userDetails)).willReturn(NEW_REFRESH_TOKEN);

            ArgumentCaptor<TokenRefreshSuccessEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshSuccessEvent.class);

            // when
            authService.refreshToken(request);

            // then
            then(eventPublisher).should().publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().ipAddress()).isEqualTo(proxiedIp);
        }

        @Test
        @DisplayName("예상치 못한 예외 발생 시 UNEXPECTED_ERROR 이유로 실패 이벤트 발행")
        void refreshToken_withUnexpectedException_publishesFailureEventWithUnexpectedErrorReason() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getHeader("X-Forwarded-For")).willReturn(null);
            given(request.getRemoteAddr()).willReturn(TEST_IP);
            given(request.getHeader("User-Agent")).willReturn(TEST_USER_AGENT);
            given(jwtCookieProvider.getRefreshTokenCookieName()).willReturn(REFRESH_COOKIE_NAME);
            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USERNAME);
            given(userDetailsService.loadUserByUsername(TEST_USERNAME))
                .willThrow(new RuntimeException("Unexpected error"));

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unexpected error");

            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.username()).isEqualTo(TEST_USERNAME);
            assertThat(capturedEvent.reason()).isEqualTo("UNEXPECTED_ERROR");
        }

        @Test
        @DisplayName("토큰에서 username 추출 실패 시에도 실패 이벤트 발행")
        void refreshToken_whenUsernameExtractionFails_stillPublishesFailureEvent() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getHeader("X-Forwarded-For")).willReturn(null);
            given(request.getRemoteAddr()).willReturn(TEST_IP);
            given(request.getHeader("User-Agent")).willReturn(TEST_USER_AGENT);
            given(jwtCookieProvider.getRefreshTokenCookieName()).willReturn(REFRESH_COOKIE_NAME);
            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(false);
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN))
                .willThrow(new IllegalArgumentException("Invalid token format"));

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class);

            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.username()).isNull();
            assertThat(capturedEvent.reason()).isEqualTo("INVALID_REFRESH_TOKEN");
        }

        @Test
        @DisplayName("쿠키 값이 null인 경우 InvalidTokenException 발생")
        void refreshToken_withNullCookieValue_throwsInvalidTokenException() {
            // given
            Cookie nullValueCookie = mock(Cookie.class);
            given(nullValueCookie.getName()).willReturn(REFRESH_COOKIE_NAME);
            given(nullValueCookie.getValue()).willReturn(null);

            given(request.getHeader("X-Forwarded-For")).willReturn(null);
            given(request.getRemoteAddr()).willReturn(TEST_IP);
            given(request.getHeader("User-Agent")).willReturn(TEST_USER_AGENT);
            given(jwtCookieProvider.getRefreshTokenCookieName()).willReturn(REFRESH_COOKIE_NAME);
            given(request.getCookies()).willReturn(new Cookie[]{nullValueCookie});

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class);

            then(eventPublisher).should().publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().reason()).isEqualTo("MISSING_REFRESH_TOKEN_COOKIE");
        }
    }
}
