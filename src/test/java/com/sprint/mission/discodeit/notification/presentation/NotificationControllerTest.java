package com.sprint.mission.discodeit.notification.presentation;

import com.sprint.mission.discodeit.global.error.GlobalExceptionHandler;
import com.sprint.mission.discodeit.global.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.global.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.global.security.ratelimit.LoginRateLimitFilter;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import com.sprint.mission.discodeit.notification.application.NotificationService;
import com.sprint.mission.discodeit.notification.domain.exception.NotificationCheckForbiddenException;
import com.sprint.mission.discodeit.notification.domain.exception.NotificationNotFoundException;
import com.sprint.mission.discodeit.notification.presentation.dto.NotificationDto;
import com.sprint.mission.discodeit.support.TestSecurityConfig;
import com.sprint.mission.discodeit.user.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = NotificationController.class,
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
@DisplayName("NotificationController 테스트")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final UUID TEST_NOTIFICATION_ID = UUID.randomUUID();
    private static final Instant NOW = Instant.now();

    private void setAuthenticatedUser() {
        UserDetailsDto userDetailsDto = new UserDetailsDto(
            NotificationControllerTest.TEST_USER_ID, "testuser", Role.USER);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDetailsDto, "password");
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private NotificationDto createNotificationDto(UUID notificationId) {
        return new NotificationDto(
            notificationId,
            NOW,
            TEST_USER_ID,
            "testuser (#general)",
            "Hello, world!"
        );
    }

    @Nested
    @DisplayName("GET /api/notifications")
    class FindAll {

        @Test
        @DisplayName("인증된 사용자의 알림 목록 조회 시 200 반환")
        void findAll_withAuthenticatedUser_returns200() throws Exception {
            // given
            setAuthenticatedUser();
            NotificationDto notification1 = createNotificationDto(UUID.randomUUID());
            NotificationDto notification2 = createNotificationDto(UUID.randomUUID());

            given(notificationService.findAllByReceiverId(TEST_USER_ID))
                .willReturn(List.of(notification1, notification2));

            // when & then
            mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].receiverId").value(TEST_USER_ID.toString()));
        }

        @Test
        @DisplayName("알림이 없을 때 빈 배열 반환")
        void findAll_withNoNotifications_returnsEmptyArray() throws Exception {
            // given
            setAuthenticatedUser();

            given(notificationService.findAllByReceiverId(TEST_USER_ID))
                .willReturn(List.of());

            // when & then
            mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("인증 없이 조회 시 403 반환")
        void findAll_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /api/notifications/{notificationId}")
    class Check {

        @Test
        @DisplayName("유효한 요청으로 알림 확인 시 204 반환")
        void check_withValidRequest_returns204() throws Exception {
            // given
            setAuthenticatedUser();

            willDoNothing().given(notificationService).check(TEST_NOTIFICATION_ID, TEST_USER_ID);

            // when & then
            mockMvc.perform(delete("/api/notifications/{notificationId}", TEST_NOTIFICATION_ID))
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("인증 없이 알림 확인 시 403 반환")
        void check_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(delete("/api/notifications/{notificationId}", TEST_NOTIFICATION_ID))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("존재하지 않는 알림 확인 시 404 반환")
        void check_withNonExistingNotification_returns404() throws Exception {
            // given
            setAuthenticatedUser();
            UUID nonExistingId = UUID.randomUUID();

            willThrow(new NotificationNotFoundException(nonExistingId))
                .given(notificationService).check(nonExistingId, TEST_USER_ID);

            // when & then
            mockMvc.perform(delete("/api/notifications/{notificationId}", nonExistingId))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("다른 사용자의 알림 확인 시 403 반환")
        void check_withOtherUserNotification_returns403() throws Exception {
            // given
            setAuthenticatedUser();

            willThrow(new NotificationCheckForbiddenException(TEST_NOTIFICATION_ID, TEST_USER_ID))
                .given(notificationService).check(TEST_NOTIFICATION_ID, TEST_USER_ID);

            // when & then
            mockMvc.perform(delete("/api/notifications/{notificationId}", TEST_NOTIFICATION_ID))
                .andExpect(status().isForbidden());
        }
    }
}
