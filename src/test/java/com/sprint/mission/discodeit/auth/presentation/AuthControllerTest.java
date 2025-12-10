package com.sprint.mission.discodeit.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.application.AuthService;
import com.sprint.mission.discodeit.auth.domain.exception.InvalidTokenException;
import com.sprint.mission.discodeit.auth.presentation.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.global.error.GlobalExceptionHandler;
import com.sprint.mission.discodeit.global.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.global.security.jwt.JwtCookieProvider;
import com.sprint.mission.discodeit.global.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.global.security.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.global.security.ratelimit.LoginRateLimitFilter;
import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import com.sprint.mission.discodeit.support.TestSecurityConfig;
import com.sprint.mission.discodeit.user.application.UserService;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            JwtAuthenticationFilter.class,
            LoginRateLimitFilter.class,
            JwtLogoutHandler.class
        }
    )
)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtCookieProvider cookieProvider;

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_ACCESS_TOKEN = "test-access-token";
    private static final String TEST_REFRESH_TOKEN = "test-refresh-token";
    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    @Nested
    @DisplayName("GET /api/auth/csrf-token")
    class GetCsrfToken {

        @Test
        @DisplayName("CSRF 토큰 요청 시 204 No Content 반환")
        void getCsrfToken_returns204() throws Exception {
            mockMvc.perform(get("/api/auth/csrf-token"))
                .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/refresh")
    class Refresh {

        @Test
        @DisplayName("유효한 리프레시 토큰으로 요청 시 새 토큰 발급")
        void refresh_withValidToken_returnsNewTokens() throws Exception {
            // given
            UserDetailsDto userDetailsDto = new UserDetailsDto(TEST_USER_ID, TEST_USERNAME, Role.USER);
            JwtDto jwtDto = new JwtDto(
                userDetailsDto, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN
            );
            UserDto userDto = new UserDto(
                TEST_USER_ID, TEST_USERNAME, TEST_EMAIL, null, true, Role.USER
            );
            Cookie refreshCookie = new Cookie(REFRESH_COOKIE_NAME, TEST_REFRESH_TOKEN);

            given(authService.refreshToken(any())).willReturn(jwtDto);
            given(userService.findById(TEST_USER_ID)).willReturn(userDto);
            given(cookieProvider.createRefreshTokenCookie(TEST_REFRESH_TOKEN)).willReturn(refreshCookie);

            // when & then
            mockMvc.perform(post("/api/auth/refresh")
                    .cookie(new Cookie(REFRESH_COOKIE_NAME, "old-refresh-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userDto.id").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$.userDto.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.userDto.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.accessToken").value(TEST_ACCESS_TOKEN))
                .andExpect(cookie().value(REFRESH_COOKIE_NAME, TEST_REFRESH_TOKEN));
        }

        @Test
        @DisplayName("유효하지 않은 리프레시 토큰으로 요청 시 401 반환")
        void refresh_withInvalidToken_returns401() throws Exception {
            // given
            given(authService.refreshToken(any())).willThrow(new InvalidTokenException());

            // when & then
            mockMvc.perform(post("/api/auth/refresh")
                    .cookie(new Cookie(REFRESH_COOKIE_NAME, "invalid-token")))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("리프레시 토큰 쿠키 없이 요청 시 401 반환")
        void refresh_withoutCookie_returns401() throws Exception {
            // given
            given(authService.refreshToken(any())).willThrow(new InvalidTokenException());

            // when & then
            mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /api/auth/role")
    class UpdateRole {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN 권한으로 사용자 권한 변경 시 성공")
        void updateRole_withAdminRole_returnsUpdatedUser() throws Exception {
            // given
            UserRoleUpdateRequest request = new UserRoleUpdateRequest(TEST_USER_ID, Role.CHANNEL_MANAGER);
            UserDto updatedUser = new UserDto(
                TEST_USER_ID, TEST_USERNAME, TEST_EMAIL, null, true, Role.CHANNEL_MANAGER
            );

            given(authService.updateRole(any(UserRoleUpdateRequest.class))).willReturn(updatedUser);

            // when & then
            mockMvc.perform(put("/api/auth/role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.role").value("CHANNEL_MANAGER"));
        }

        @Test
        @DisplayName("인증 없이 사용자 권한 변경 시 403 반환")
        void updateRole_withoutAuth_returns403() throws Exception {
            // given
            UserRoleUpdateRequest request = new UserRoleUpdateRequest(TEST_USER_ID, Role.CHANNEL_MANAGER);

            // when & then
            mockMvc.perform(put("/api/auth/role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("존재하지 않는 사용자 권한 변경 시 404 반환")
        void updateRole_withNonExistingUser_returns404() throws Exception {
            // given
            UUID nonExistingUserId = UUID.randomUUID();
            UserRoleUpdateRequest request = new UserRoleUpdateRequest(nonExistingUserId, Role.CHANNEL_MANAGER);

            given(authService.updateRole(any(UserRoleUpdateRequest.class)))
                .willThrow(new UserNotFoundException(nonExistingUserId));

            // when & then
            mockMvc.perform(put("/api/auth/role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("요청 본문이 없으면 400 반환")
        void updateRole_withoutBody_returns400() throws Exception {
            // when & then
            mockMvc.perform(put("/api/auth/role")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }
    }
}
