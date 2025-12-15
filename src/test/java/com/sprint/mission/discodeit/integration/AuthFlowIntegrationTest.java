package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.presentation.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.global.config.properties.RateLimitProperties;
import com.sprint.mission.discodeit.global.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.global.security.ratelimit.registry.LoginRateLimitRegistry;
import com.sprint.mission.discodeit.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.presentation.dto.UserCreateRequest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("인증 플로우 통합 테스트")
class AuthFlowIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginRateLimitRegistry loginRateLimitRegistry;

    @Autowired
    private JwtRegistry jwtRegistry;

    @Autowired
    private RateLimitProperties rateLimitProperties;

    @Value("${discodeit.jwt.refresh-token-cookie-name}")
    private String refreshTokenCookieName;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "P@ssw0rd!";

    private static final String DEFAULT_TEST_IP = "127.0.0.1";

    @BeforeEach
    void setUp() {
        // Rate limit 상태 초기화 (테스트 간 영향 방지)
        loginRateLimitRegistry.resetAttempts(DEFAULT_TEST_IP);
        // JWT Registry 초기화 (테스트 간 토큰 공유 방지)
        jwtRegistry.getActiveUserIds().forEach(jwtRegistry::invalidateJwtInformationByUserId);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        loginRateLimitRegistry.resetAttempts(DEFAULT_TEST_IP);
        jwtRegistry.getActiveUserIds().forEach(jwtRegistry::invalidateJwtInformationByUserId);
    }

    @Nested
    @DisplayName("회원가입 → 로그인 → 토큰 갱신 → 로그아웃 전체 플로우")
    class FullAuthFlow {

        @Test
        @DisplayName("전체 인증 플로우가 정상적으로 동작한다")
        void fullAuthFlow_success() throws Exception {
            // 1. 회원가입
            UserCreateRequest createRequest = new UserCreateRequest(
                TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createRequest)
            );

            mockMvc.perform(multipart("/api/users")
                    .file(requestPart)
                    .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));

            // 2. 로그인
            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.userDto.username").value(TEST_USERNAME))
                .andExpect(cookie().exists(refreshTokenCookieName))
                .andReturn();

            Cookie refreshTokenCookie = loginResult.getResponse().getCookie(refreshTokenCookieName);
            assertThat(refreshTokenCookie).isNotNull();
            String accessToken = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
            ).get("accessToken").asText();

            // 3. 인증된 엔드포인트 접근 (사용자 목록 조회)
            mockMvc.perform(get("/api/users")
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(TEST_USERNAME));

            // 4. 토큰 갱신
            MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                    .with(csrf())
                    .cookie(refreshTokenCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(cookie().exists(refreshTokenCookieName))
                .andReturn();

            String newAccessToken = objectMapper.readTree(
                refreshResult.getResponse().getContentAsString()
            ).get("accessToken").asText();
            Cookie newRefreshTokenCookie = refreshResult.getResponse().getCookie(refreshTokenCookieName);

            assertThat(newAccessToken).isNotEqualTo(accessToken);
            assertThat(newRefreshTokenCookie).isNotNull();

            // 5. 새 액세스 토큰으로 인증된 엔드포인트 접근
            mockMvc.perform(get("/api/users")
                    .header("Authorization", "Bearer " + newAccessToken))
                .andExpect(status().isOk());

            // 6. 로그아웃
            mockMvc.perform(post("/api/auth/logout")
                    .with(csrf())
                    .cookie(newRefreshTokenCookie))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge(refreshTokenCookieName, 0));
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @BeforeEach
        void setUpUser() {
            User user = new User(
                TEST_USERNAME,
                TEST_EMAIL,
                passwordEncoder.encode(TEST_PASSWORD),
                null
            );
            userRepository.save(user);
        }

        @Test
        @DisplayName("올바른 자격 증명으로 로그인 성공")
        void login_withValidCredentials_success() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.userDto.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.userDto.email").value(TEST_EMAIL))
                .andExpect(cookie().exists(refreshTokenCookieName))
                .andExpect(cookie().httpOnly(refreshTokenCookieName, true))
                .andExpect(cookie().secure(refreshTokenCookieName, true));
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인 실패")
        void login_withWrongPassword_returns401() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", "wrongPassword"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 로그인 실패")
        void login_withNonExistentUser_returns401() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "nonexistent")
                    .param("password", TEST_PASSWORD))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("토큰 갱신")
    class TokenRefresh {

        @Test
        @DisplayName("유효한 리프레시 토큰으로 토큰 갱신 성공")
        void refresh_withValidToken_success() throws Exception {
            // Given: 사용자 생성 및 로그인
            User user = new User(
                TEST_USERNAME,
                TEST_EMAIL,
                passwordEncoder.encode(TEST_PASSWORD),
                null
            );
            userRepository.save(user);

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

            Cookie refreshTokenCookie = loginResult.getResponse().getCookie(refreshTokenCookieName);

            // When & Then
            mockMvc.perform(post("/api/auth/refresh")
                    .with(csrf())
                    .cookie(refreshTokenCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.userDto.username").value(TEST_USERNAME))
                .andExpect(cookie().exists(refreshTokenCookieName));
        }

        @Test
        @DisplayName("리프레시 토큰 없이 갱신 요청 시 401 반환")
        void refresh_withoutToken_returns401() throws Exception {
            mockMvc.perform(post("/api/auth/refresh")
                    .with(csrf()))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("유효하지 않은 리프레시 토큰으로 갱신 요청 시 401 반환")
        void refresh_withInvalidToken_returns401() throws Exception {
            Cookie invalidCookie = new Cookie(refreshTokenCookieName, "invalid-token");

            mockMvc.perform(post("/api/auth/refresh")
                    .with(csrf())
                    .cookie(invalidCookie))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("로그아웃 시 리프레시 토큰 쿠키 만료")
        void logout_expiresRefreshTokenCookie() throws Exception {
            // Given: 사용자 생성 및 로그인
            User user = new User(
                TEST_USERNAME,
                TEST_EMAIL,
                passwordEncoder.encode(TEST_PASSWORD),
                null
            );
            userRepository.save(user);

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

            Cookie refreshTokenCookie = loginResult.getResponse().getCookie(refreshTokenCookieName);

            // When & Then
            mockMvc.perform(post("/api/auth/logout")
                    .with(csrf())
                    .cookie(refreshTokenCookie))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge(refreshTokenCookieName, 0));
        }

        @Test
        @DisplayName("로그아웃 후 기존 리프레시 토큰으로 갱신 불가")
        void logout_invalidatesRefreshToken() throws Exception {
            // Given: 사용자 생성 및 로그인
            User user = new User(
                TEST_USERNAME,
                TEST_EMAIL,
                passwordEncoder.encode(TEST_PASSWORD),
                null
            );
            userRepository.save(user);

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

            Cookie refreshTokenCookie = loginResult.getResponse().getCookie(refreshTokenCookieName);

            // When: 로그아웃
            mockMvc.perform(post("/api/auth/logout")
                    .with(csrf())
                    .cookie(refreshTokenCookie))
                .andExpect(status().isNoContent());

            // Then: 기존 토큰으로 갱신 시도 시 실패
            mockMvc.perform(post("/api/auth/refresh")
                    .with(csrf())
                    .cookie(refreshTokenCookie))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("인증 필요 엔드포인트 접근")
    class AuthenticatedEndpoint {

        @Test
        @DisplayName("인증 없이 보호된 엔드포인트 접근 시 403 반환")
        void accessProtectedEndpoint_withoutAuth_returns403() throws Exception {
            mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("유효한 액세스 토큰으로 보호된 엔드포인트 접근 성공")
        void accessProtectedEndpoint_withValidToken_success() throws Exception {
            // Given: 사용자 생성 및 로그인
            User user = new User(
                TEST_USERNAME,
                TEST_EMAIL,
                passwordEncoder.encode(TEST_PASSWORD),
                null
            );
            userRepository.save(user);

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

            String accessToken = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
            ).get("accessToken").asText();

            // When & Then
            mockMvc.perform(get("/api/users")
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("유효하지 않은 액세스 토큰으로 보호된 엔드포인트 접근 시 401 반환")
        void accessProtectedEndpoint_withInvalidToken_returns401() throws Exception {
            mockMvc.perform(get("/api/users")
                    .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("CSRF 토큰")
    class CsrfToken {

        @Test
        @DisplayName("CSRF 토큰 요청 시 204 반환")
        void getCsrfToken_returns204() throws Exception {
            // MockMvc에서는 CookieCsrfTokenRepository의 쿠키 설정이 응답에 반영되지 않음
            // 상태 코드만 검증하고, 실제 쿠키 설정은 E2E 테스트에서 검증
            mockMvc.perform(get("/api/auth/csrf-token"))
                .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("권한 검증")
    class Authorization {

        @Test
        @DisplayName("USER 권한으로 CHANNEL_MANAGER 전용 엔드포인트 접근 시 403과 ErrorResponse 반환")
        void accessChannelManagerEndpoint_withUserRole_returns403WithErrorResponse() throws Exception {
            // Given: USER 권한 사용자 생성 및 로그인
            User user = new User(
                TEST_USERNAME,
                TEST_EMAIL,
                passwordEncoder.encode(TEST_PASSWORD),
                null
            );
            userRepository.save(user);

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

            String accessToken = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
            ).get("accessToken").asText();

            // When & Then: 채널 생성 시도 (CHANNEL_MANAGER 권한 필요)
            // @PreAuthorize 권한 거부는 GlobalExceptionHandler에서 처리됨
            PublicChannelCreateRequest channelRequest = new PublicChannelCreateRequest(
                "test-channel", "Test Channel Description"
            );

            mockMvc.perform(post("/api/channels/public")
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(channelRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("권한이 없습니다."))
                .andExpect(jsonPath("$.exceptionType").value("AuthorizationDeniedException"))
                .andExpect(jsonPath("$.status").value(403));
        }

        @Test
        @DisplayName("USER 권한으로 ADMIN 전용 엔드포인트 접근 시 403 반환")
        void accessAdminEndpoint_withUserRole_returns403() throws Exception {
            // Given: USER 권한 사용자 생성 및 로그인
            User user = new User(
                TEST_USERNAME,
                TEST_EMAIL,
                passwordEncoder.encode(TEST_PASSWORD),
                null
            );
            userRepository.save(user);

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

            String accessToken = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
            ).get("accessToken").asText();

            // When & Then: 역할 변경 시도 (ADMIN 권한 필요)
            UserRoleUpdateRequest roleRequest = new UserRoleUpdateRequest(
                user.getId(), Role.CHANNEL_MANAGER
            );

            mockMvc.perform(put("/api/auth/role")
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("ADMIN은 CHANNEL_MANAGER 권한을 상속받아 채널 생성 가능")
        void accessChannelManagerEndpoint_withAdminRole_success() throws Exception {
            // Given: ADMIN 권한 사용자 생성 및 로그인
            User admin = new User(
                TEST_USERNAME,
                TEST_EMAIL,
                passwordEncoder.encode(TEST_PASSWORD),
                null
            );
            admin.updateRole(Role.ADMIN);
            userRepository.save(admin);

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

            String accessToken = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
            ).get("accessToken").asText();

            // When & Then: ADMIN이 채널 생성 (Role Hierarchy로 CHANNEL_MANAGER 권한 상속)
            PublicChannelCreateRequest channelRequest = new PublicChannelCreateRequest(
                "admin-channel", "Admin Created Channel"
            );

            mockMvc.perform(post("/api/channels/public")
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(channelRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("admin-channel"));
        }

        @Test
        @DisplayName("CHANNEL_MANAGER 권한으로 ADMIN 전용 엔드포인트 접근 시 403 반환")
        void accessAdminEndpoint_withChannelManagerRole_returns403() throws Exception {
            // Given: CHANNEL_MANAGER 권한 사용자 생성 및 로그인
            User channelManager = new User(
                TEST_USERNAME,
                TEST_EMAIL,
                passwordEncoder.encode(TEST_PASSWORD),
                null
            );
            channelManager.updateRole(Role.CHANNEL_MANAGER);
            userRepository.save(channelManager);

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", TEST_USERNAME)
                    .param("password", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

            String accessToken = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
            ).get("accessToken").asText();

            // When & Then: 역할 변경 시도 (ADMIN 권한 필요)
            UserRoleUpdateRequest roleRequest = new UserRoleUpdateRequest(
                channelManager.getId(), Role.ADMIN
            );

            mockMvc.perform(put("/api/auth/role")
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isForbidden());
        }
    }
}
