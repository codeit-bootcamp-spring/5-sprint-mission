package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.common.config.TestSecurityConfig;
import com.sprint.mission.discodeit.common.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.common.exception.notification.NotificationForbiddenException;
import com.sprint.mission.discodeit.common.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.common.security.userdetails.WithMockDiscodeitUser;
import com.sprint.mission.discodeit.domain.controller.NotificationController;
import com.sprint.mission.discodeit.domain.dto.notification.data.NotificationDto;
import com.sprint.mission.discodeit.domain.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.MOCK_USER_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = NotificationController.class, excludeFilters = @ComponentScan.Filter(
    type = FilterType.REGEX, pattern = ".*\\.security\\..*|.*\\.config\\.SecurityConfig"))
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    @WithMockDiscodeitUser
    @DisplayName("GET /api/notifications - 성공: 알림 목록 조회")
    void findAll_Success() throws Exception {
        // given
        UUID notificationId1 = UUID.randomUUID();
        UUID notificationId2 = UUID.randomUUID();
        Instant now = Instant.now();

        List<NotificationDto> notifications = List.of(
            new NotificationDto(notificationId1, now, MOCK_USER_ID, "Title 1", "Content 1"),
            new NotificationDto(notificationId2, now.minusSeconds(60), MOCK_USER_ID, "Title 2", "Content 2")
        );

        given(notificationService.findAllByReceiverId(MOCK_USER_ID)).willReturn(notifications);

        // when & then
        mockMvc.perform(get("/api/notifications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(notificationId1.toString()))
            .andExpect(jsonPath("$[0].title").value("Title 1"))
            .andExpect(jsonPath("$[0].content").value("Content 1"))
            .andExpect(jsonPath("$[1].id").value(notificationId2.toString()))
            .andExpect(jsonPath("$[1].title").value("Title 2"));

        then(notificationService).should().findAllByReceiverId(MOCK_USER_ID);
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("GET /api/notifications - 성공: 빈 알림 목록")
    void findAll_EmptyList() throws Exception {
        // given
        given(notificationService.findAllByReceiverId(MOCK_USER_ID)).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/notifications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

        then(notificationService).should().findAllByReceiverId(MOCK_USER_ID);
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("DELETE /api/notifications/{notificationId} - 성공: 알림 확인")
    void check_Success() throws Exception {
        // given
        UUID notificationId = UUID.randomUUID();

        willDoNothing().given(notificationService).check(notificationId, MOCK_USER_ID);

        // when & then
        mockMvc.perform(delete("/api/notifications/{notificationId}", notificationId)
                .with(csrf()))
            .andExpect(status().isNoContent());

        then(notificationService).should().check(notificationId, MOCK_USER_ID);
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("DELETE /api/notifications/{notificationId} - 실패: 존재하지 않는 알림")
    void check_NotificationNotFound() throws Exception {
        // given
        UUID notificationId = UUID.randomUUID();

        willThrow(new NotificationNotFoundException(notificationId))
            .given(notificationService).check(notificationId, MOCK_USER_ID);

        // when & then
        mockMvc.perform(delete("/api/notifications/{notificationId}", notificationId)
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("NOTIFICATION_NOT_FOUND"));

        then(notificationService).should().check(notificationId, MOCK_USER_ID);
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("DELETE /api/notifications/{notificationId} - 실패: 다른 사용자의 알림 확인 시도")
    void check_Forbidden() throws Exception {
        // given
        UUID notificationId = UUID.randomUUID();

        willThrow(new NotificationForbiddenException(notificationId, MOCK_USER_ID))
            .given(notificationService).check(notificationId, MOCK_USER_ID);

        // when & then
        mockMvc.perform(delete("/api/notifications/{notificationId}", notificationId)
                .with(csrf()))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("NOTIFICATION_FORBIDDEN"));

        then(notificationService).should().check(notificationId, MOCK_USER_ID);
    }
}
