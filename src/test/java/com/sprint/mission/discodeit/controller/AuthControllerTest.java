package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.TestSecurityConfig;
import com.sprint.mission.discodeit.dto.auth.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.jwt.data.JwtInformation;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.security.WithMockDiscodeitUser;
import com.sprint.mission.discodeit.security.audit.AuthAuditService;
import com.sprint.mission.discodeit.security.audit.AuthMetricsService;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController 단위 테스트.
 *
 * <p>@WebMvcTest를 사용하여 컨트롤러 로직만 테스트합니다.
 * 실제 인증/인가 동작(인증되지 않은 사용자 차단 등)은 통합 테스트에서 검증합니다.</p>
 */
@WebMvcTest(value = AuthController.class, excludeFilters = @ComponentScan.Filter(
    type = FilterType.REGEX, pattern = ".*\\.security\\..*|.*\\.config\\.SecurityConfig"))
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider tokenProvider;

    @MockitoBean
    private AuthAuditService authAuditService;

    @MockitoBean
    private AuthMetricsService authMetricsService;

    @Test
    @WithMockDiscodeitUser(role = Role.ADMIN)
    @DisplayName("PUT /api/auth/role - 성공: 관리자가 사용자 권한 변경")
    void updateRole_AsAdmin_Success() throws Exception {
        // given
        UUID targetUserId = UUID.randomUUID();
        RoleUpdateRequest request = new RoleUpdateRequest(targetUserId, Role.CHANNEL_MANAGER);

        UserDto updatedUser = new UserDto(
            targetUserId,
            "targetuser",
            "target@example.com",
            null,
            true,
            Role.CHANNEL_MANAGER
        );

        given(authService.updateRole(any(RoleUpdateRequest.class))).willReturn(updatedUser);

        // when & then
        mockMvc.perform(put("/api/auth/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(targetUserId.toString()))
            .andExpect(jsonPath("$.role").value("CHANNEL_MANAGER"));

        then(authService).should().updateRole(any(RoleUpdateRequest.class));
    }

    @Test
    @WithMockDiscodeitUser(role = Role.ADMIN)
    @DisplayName("PUT /api/auth/role - 성공: ADMIN 권한 부여")
    void updateRole_GrantAdmin_Success() throws Exception {
        // given
        UUID targetUserId = UUID.randomUUID();
        RoleUpdateRequest request = new RoleUpdateRequest(targetUserId, Role.ADMIN);

        UserDto updatedUser = new UserDto(
            targetUserId,
            "targetuser",
            "target@example.com",
            null,
            true,
            Role.ADMIN
        );

        given(authService.updateRole(any(RoleUpdateRequest.class))).willReturn(updatedUser);

        // when & then
        mockMvc.perform(put("/api/auth/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("ADMIN"));

        then(authService).should().updateRole(any(RoleUpdateRequest.class));
    }

    @Test
    @WithMockDiscodeitUser(role = Role.ADMIN)
    @DisplayName("PUT /api/auth/role - 실패: 존재하지 않는 사용자")
    void updateRole_UserNotFound_NotFound() throws Exception {
        // given
        UUID nonExistentUserId = UUID.randomUUID();
        RoleUpdateRequest request = new RoleUpdateRequest(nonExistentUserId, Role.CHANNEL_MANAGER);

        given(authService.updateRole(any(RoleUpdateRequest.class)))
            .willThrow(new UserNotFoundException());

        // when & then
        mockMvc.perform(put("/api/auth/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));

        then(authService).should().updateRole(any(RoleUpdateRequest.class));
    }

    @Test
    @WithMockDiscodeitUser(role = Role.USER)
    @DisplayName("PUT /api/auth/role - 실패: 일반 사용자가 권한 변경 시도 시 Service에서 AccessDenied 예외 발생")
    void updateRole_AsUser_ServiceThrowsAccessDenied() throws Exception {
        // given
        UUID targetUserId = UUID.randomUUID();
        RoleUpdateRequest request = new RoleUpdateRequest(targetUserId, Role.ADMIN);

        // Service의 @PreAuthorize에서 권한 검사 후 예외 발생 시뮬레이션
        given(authService.updateRole(any(RoleUpdateRequest.class)))
            .willThrow(new AuthorizationDeniedException("Access Denied: hasRole('ADMIN')"));

        // when & then
        mockMvc.perform(put("/api/auth/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("INSUFFICIENT_ROLE"));

        then(authService).should().updateRole(any(RoleUpdateRequest.class));
    }

    // Note: 인증되지 않은 사용자 테스트는 실제 Security 설정이 필요하므로 통합 테스트에서 검증합니다.

    @Test
    @WithMockDiscodeitUser
    @DisplayName("POST /api/auth/refresh - 성공: 유효한 리프레시 토큰으로 새 토큰 발급")
    void refresh_ValidToken_Success() throws Exception {
        // given
        String refreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        UUID userId = UUID.randomUUID();

        UserDto userDto = new UserDto(
            userId,
            "testuser",
            "test@example.com",
            null,
            true,
            Role.USER
        );
        JwtInformation jwtInformation = new JwtInformation(userDto, newAccessToken, newRefreshToken);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", newRefreshToken);
        refreshCookie.setHttpOnly(true);

        given(authService.refreshToken(refreshToken)).willReturn(jwtInformation);
        given(tokenProvider.generateRefreshTokenCookie(newRefreshToken)).willReturn(refreshCookie);

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                .cookie(new Cookie("REFRESH_TOKEN", refreshToken))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userDto.id").value(userId.toString()))
            .andExpect(jsonPath("$.userDto.username").value("testuser"))
            .andExpect(jsonPath("$.accessToken").value(newAccessToken))
            .andExpect(cookie().value("REFRESH_TOKEN", newRefreshToken));

        then(authService).should().refreshToken(refreshToken);
        then(tokenProvider).should().generateRefreshTokenCookie(newRefreshToken);
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("POST /api/auth/refresh - 실패: 유효하지 않은 리프레시 토큰")
    void refresh_InvalidToken_Unauthorized() throws Exception {
        // given
        String invalidRefreshToken = "invalid-refresh-token";

        given(authService.refreshToken(invalidRefreshToken))
            .willThrow(new DiscodeitException(ErrorCode.INVALID_TOKEN));

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                .cookie(new Cookie("REFRESH_TOKEN", invalidRefreshToken))
                .with(csrf()))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));

        then(authService).should().refreshToken(invalidRefreshToken);
    }
}
