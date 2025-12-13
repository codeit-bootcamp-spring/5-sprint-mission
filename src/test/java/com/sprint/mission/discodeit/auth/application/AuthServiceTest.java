package com.sprint.mission.discodeit.auth.application;

import com.sprint.mission.discodeit.auth.domain.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.event.TokenRefreshEvent;
import com.sprint.mission.discodeit.auth.domain.event.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.auth.domain.exception.InvalidTokenException;
import com.sprint.mission.discodeit.auth.domain.exception.MissingRefreshTokenCookieException;
import com.sprint.mission.discodeit.auth.presentation.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.global.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.global.security.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import com.sprint.mission.discodeit.user.application.UserMapper;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.common.domain.exception.ErrorCode.INVALID_TOKEN;
import static com.sprint.mission.discodeit.common.domain.exception.ErrorCode.MISSING_REFRESH_TOKEN;
import static com.sprint.mission.discodeit.user.domain.Role.ADMIN;
import static com.sprint.mission.discodeit.user.domain.Role.CHANNEL_MANAGER;
import static com.sprint.mission.discodeit.user.domain.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    private static final String USER_AGENT = "User-Agent";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "DISCODEIT_REFRESH_TOKEN";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

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

    @BeforeEach
    void globalSetUp() {
        ReflectionTestUtils.setField(authService, "refreshTokenCookieName", REFRESH_TOKEN_COOKIE_NAME);
    }

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
                TEST_USER_ID, TEST_USERNAME, TEST_EMAIL, null, true, CHANNEL_MANAGER
            );
        }

        @Test
        @DisplayName("유효한 사용자 ID 요청 시 권한 변경 성공")
        void updateRole_withValidUserId_updatesRoleSuccessfully() {
            // given
            UserRoleUpdateRequest request = new UserRoleUpdateRequest(TEST_USER_ID, CHANNEL_MANAGER);

            given(userRepository.findWithProfileById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userMapper.toDto(testUser)).willReturn(testUserDto);

            // when
            UserDto result = authService.updateRole(request);

            // then
            assertThat(testUser.getRole()).isEqualTo(CHANNEL_MANAGER);
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(TEST_USER_ID);
            assertThat(result.role()).isEqualTo(CHANNEL_MANAGER);

            then(jwtRegistry).should().invalidateJwtInformationByUserId(TEST_USER_ID);
        }

        @Test
        @DisplayName("권한 변경 성공 시 RoleUpdatedEvent 이벤트 발행")
        void updateRole_publishesRoleUpdatedEvent() {
            // given
            UserRoleUpdateRequest request = new UserRoleUpdateRequest(TEST_USER_ID, CHANNEL_MANAGER);
            Role oldRole = testUser.getRole();

            given(userRepository.findWithProfileById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userMapper.toDto(testUser)).willReturn(testUserDto);

            ArgumentCaptor<RoleUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(RoleUpdatedEvent.class);

            // when
            authService.updateRole(request);

            // then
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            RoleUpdatedEvent event = eventCaptor.getValue();
            assertThat(event.userId()).isEqualTo(TEST_USER_ID);
            assertThat(event.username()).isEqualTo(TEST_USERNAME);
            assertThat(event.oldRole()).isEqualTo(oldRole);
            assertThat(event.newRole()).isEqualTo(CHANNEL_MANAGER);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID 요청 시 UserNotFoundException 발생")
        void updateRole_withNonExistingUserId_throwsUserNotFoundException() {
            // given
            UUID nonExistingUserId = UUID.randomUUID();
            UserRoleUpdateRequest request = new UserRoleUpdateRequest(nonExistingUserId, CHANNEL_MANAGER);

            given(userRepository.findWithProfileById(nonExistingUserId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.updateRole(request))
                .isInstanceOf(UserNotFoundException.class);

            then(jwtRegistry).should(never()).invalidateJwtInformationByUserId(any());
            then(eventPublisher).should(never()).publishEvent(any());
        }

        @Test
        @DisplayName("ADMIN에서 USER로 권한 변경 요청 시 성공")
        void updateRole_fromAdminToUser_updatesSuccessfully() {
            // given
            User adminUser = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, null);
            ReflectionTestUtils.setField(adminUser, "id", TEST_USER_ID);
            ReflectionTestUtils.setField(adminUser, "role", ADMIN);

            UserRoleUpdateRequest request = new UserRoleUpdateRequest(TEST_USER_ID, USER);
            UserDto updatedUserDto = new UserDto(
                TEST_USER_ID, TEST_USERNAME, TEST_EMAIL, null, true, USER
            );

            given(userRepository.findWithProfileById(TEST_USER_ID)).willReturn(Optional.of(adminUser));
            given(userMapper.toDto(adminUser)).willReturn(updatedUserDto);

            // when
            UserDto result = authService.updateRole(request);

            // then
            assertThat(result.role()).isEqualTo(USER);
            then(jwtRegistry).should().invalidateJwtInformationByUserId(TEST_USER_ID);
        }
    }

    @Nested
    @DisplayName("refreshToken 메서드")
    class RefreshToken {

        @Mock
        private HttpServletRequest request;

        private User testUser;
        private DiscodeitUserDetails userDetails;
        private UserDetailsDto userDetailsDto;
        private static final String OLD_REFRESH_TOKEN = "old-refresh-token";
        private static final String NEW_ACCESS_TOKEN = "new-access-token";
        private static final String NEW_REFRESH_TOKEN = "new-refresh-token";
        private static final String TEST_IP = "127.0.0.1";
        private static final String TEST_USER_AGENT = "Mozilla/5.0";

        @BeforeEach
        void setUp() {
            testUser = new User(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, null);
            ReflectionTestUtils.setField(testUser, "id", TEST_USER_ID);

            userDetailsDto = new UserDetailsDto(TEST_USER_ID, TEST_USERNAME, USER);
            userDetails = new DiscodeitUserDetails(userDetailsDto, TEST_PASSWORD);
            ReflectionTestUtils.setField(authService, "refreshTokenCookieName", REFRESH_TOKEN_COOKIE_NAME);

            lenient().when(request.getHeader(X_FORWARDED_FOR)).thenReturn(null);
            lenient().when(request.getRemoteAddr()).thenReturn(TEST_IP);
            lenient().when(request.getHeader(USER_AGENT)).thenReturn(TEST_USER_AGENT);
        }

        @Test
        @DisplayName("유효한 리프레시 토큰 요청 시 새 토큰 발급")
        void refreshToken_withValidToken_returnsNewTokens() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUserIdFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USER_ID);
            given(userRepository.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userDetailsService.loadUserByUsername(TEST_USER_ID.toString())).willReturn(userDetails);

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
        @DisplayName("토큰 갱신 성공 시 TokenRefreshEvent 이벤트 발행")
        void refreshToken_onSuccess_publishesEvent() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUserIdFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USER_ID);
            given(userRepository.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userDetailsService.loadUserByUsername(TEST_USER_ID.toString())).willReturn(userDetails);

            given(jwtTokenProvider.generateAccessToken(userDetails)).willReturn(NEW_ACCESS_TOKEN);
            given(jwtTokenProvider.generateRefreshToken(userDetails)).willReturn(NEW_REFRESH_TOKEN);

            ArgumentCaptor<TokenRefreshEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshEvent.class);

            // when
            authService.refreshToken(request);

            // then
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshEvent event = eventCaptor.getValue();
            assertThat(event.userId()).isEqualTo(TEST_USER_ID);
            assertThat(event.username()).isEqualTo(TEST_USERNAME);
            assertThat(event.ipAddress()).isEqualTo(TEST_IP);
            assertThat(event.userAgent()).isEqualTo(TEST_USER_AGENT);
        }

        @Test
        @DisplayName("쿠키 없음 시 MissingRefreshTokenCookieException 발생")
        void refreshToken_withoutCookie_throwsMissingRefreshTokenCookieException() {
            // given
            given(request.getCookies()).willReturn(null);

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(MissingRefreshTokenCookieException.class);

            then(jwtRegistry).should(never()).rotateJwtInformation(any(), any());
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent event = eventCaptor.getValue();
            assertThat(event.userId()).isNull();
            assertThat(event.username()).isEqualTo("N/A");
            assertThat(event.ipAddress()).isEqualTo(TEST_IP);
            assertThat(event.userAgent()).isEqualTo(TEST_USER_AGENT);
            assertThat(event.reason()).isEqualTo(MISSING_REFRESH_TOKEN.getMessage());
        }

        @Test
        @DisplayName("쿠키 값 빈 문자열 시 MissingRefreshTokenCookieException 발생")
        void refreshToken_withEmptyCookieValue_throwsMissingRefreshTokenCookieException() {
            // given
            Cookie emptyCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");

            given(request.getCookies()).willReturn(new Cookie[]{emptyCookie});

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(MissingRefreshTokenCookieException.class);

            then(eventPublisher).should().publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().reason()).isEqualTo(MISSING_REFRESH_TOKEN.getMessage());
        }

        @Test
        @DisplayName("리프레시 토큰 서명 유효하지 않음 시 InvalidTokenException 발생")
        void refreshToken_withInvalidSignature_throwsInvalidTokenException() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(false);
            given(jwtTokenProvider.getUserIdFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USER_ID);
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USERNAME);

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class);

            then(jwtRegistry).should(never()).rotateJwtInformation(any(), any());
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent event = eventCaptor.getValue();
            assertThat(event.userId()).isEqualTo(TEST_USER_ID);
            assertThat(event.username()).isEqualTo(TEST_USERNAME);
            assertThat(event.reason()).isEqualTo(INVALID_TOKEN.getMessage());
        }

        @Test
        @DisplayName("레지스트리에 없는 리프레시 토큰 시 InvalidTokenException 발생")
        void refreshToken_withTokenNotInRegistry_throwsInvalidTokenException() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(false);
            given(jwtTokenProvider.getUserIdFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USER_ID);
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USERNAME);

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class);

            then(jwtRegistry).should(never()).rotateJwtInformation(any(), any());
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent event = eventCaptor.getValue();
            assertThat(event.userId()).isEqualTo(TEST_USER_ID);
            assertThat(event.username()).isEqualTo(TEST_USERNAME);
            assertThat(event.reason()).isEqualTo(INVALID_TOKEN.getMessage());
        }

        @Test
        @DisplayName("UserDetails 타입 불일치 시 InvalidTokenException 발생")
        void refreshToken_withNonDiscodeitUserDetails_throwsInvalidTokenException() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, OLD_REFRESH_TOKEN);
            UserDetails standardUserDetails = mock(UserDetails.class);

            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUserIdFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USER_ID);
            given(userRepository.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userDetailsService.loadUserByUsername(TEST_USER_ID.toString())).willReturn(standardUserDetails);
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USERNAME);

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class);

            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent event = eventCaptor.getValue();
            assertThat(event.userId()).isEqualTo(TEST_USER_ID);
            assertThat(event.username()).isEqualTo(TEST_USERNAME);
            assertThat(event.reason()).isEqualTo(INVALID_TOKEN.getMessage());
        }

        @Test
        @DisplayName("X-Forwarded-For 헤더 있음 시 해당 IP 사용")
        void refreshToken_withXForwardedForHeader_usesProxiedIp() {
            // given
            String proxiedIp = "192.168.1.1";
            Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getHeader(X_FORWARDED_FOR)).willReturn(proxiedIp + ", 10.0.0.1");
            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUserIdFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USER_ID);
            given(userRepository.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userDetailsService.loadUserByUsername(TEST_USER_ID.toString())).willReturn(userDetails);

            given(jwtTokenProvider.generateAccessToken(userDetails)).willReturn(NEW_ACCESS_TOKEN);
            given(jwtTokenProvider.generateRefreshToken(userDetails)).willReturn(NEW_REFRESH_TOKEN);

            ArgumentCaptor<TokenRefreshEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshEvent.class);

            // when
            authService.refreshToken(request);

            // then
            then(eventPublisher).should().publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().ipAddress()).isEqualTo(proxiedIp);
        }

        @Test
        @DisplayName("예상치 못한 예외 발생 시 TokenRefreshFailureEvent 발행")
        void refreshToken_withUnexpectedException_publishesFailureEvent() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtRegistry.hasActiveJwtInformationByRefreshToken(OLD_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUserIdFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USER_ID);
            given(userRepository.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userDetailsService.loadUserByUsername(TEST_USER_ID.toString()))
                .willThrow(new RuntimeException("Unexpected error"));
            given(jwtTokenProvider.getUsernameFromToken(OLD_REFRESH_TOKEN)).willReturn(TEST_USERNAME);

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(RuntimeException.class);

            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent event = eventCaptor.getValue();
            assertThat(event.userId()).isEqualTo(TEST_USER_ID);
            assertThat(event.username()).isEqualTo(TEST_USERNAME);
        }

        @Test
        @DisplayName("토큰 클레임 추출 실패 시에도 TokenRefreshFailureEvent 발행")
        void refreshToken_whenClaimExtractionFails_stillPublishesFailureEvent() {
            // given
            Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, OLD_REFRESH_TOKEN);

            given(request.getCookies()).willReturn(new Cookie[]{refreshCookie});

            given(jwtTokenProvider.validateRefreshToken(OLD_REFRESH_TOKEN)).willReturn(false);
            given(jwtTokenProvider.getUserIdFromToken(OLD_REFRESH_TOKEN))
                .willThrow(new IllegalArgumentException("Invalid token format"));

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class);

            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            TokenRefreshFailureEvent event = eventCaptor.getValue();
            assertThat(event.userId()).isNull();
            assertThat(event.username()).isEqualTo("N/A");
            assertThat(event.reason()).isEqualTo(INVALID_TOKEN.getMessage());
        }

        @Test
        @DisplayName("쿠키 값 null 시 MissingRefreshTokenCookieException 발생")
        void refreshToken_withNullCookieValue_throwsMissingRefreshTokenCookieException() {
            // given
            Cookie nullValueCookie = mock(Cookie.class);
            given(nullValueCookie.getName()).willReturn(REFRESH_TOKEN_COOKIE_NAME);
            given(nullValueCookie.getValue()).willReturn(null);

            given(request.getCookies()).willReturn(new Cookie[]{nullValueCookie});

            ArgumentCaptor<TokenRefreshFailureEvent> eventCaptor =
                ArgumentCaptor.forClass(TokenRefreshFailureEvent.class);

            // when & then
            assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(MissingRefreshTokenCookieException.class);

            then(eventPublisher).should().publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().reason()).isEqualTo(MISSING_REFRESH_TOKEN.getMessage());
        }
    }
}
