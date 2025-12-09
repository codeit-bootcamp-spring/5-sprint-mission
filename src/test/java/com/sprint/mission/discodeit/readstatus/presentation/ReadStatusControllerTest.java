package com.sprint.mission.discodeit.readstatus.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.global.error.GlobalExceptionHandler;
import com.sprint.mission.discodeit.global.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.global.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.global.security.ratelimit.LoginRateLimitFilter;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import com.sprint.mission.discodeit.readstatus.application.ReadStatusService;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusForbiddenException;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusDto;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusUpdateRequest;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = ReadStatusController.class,
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
@DisplayName("ReadStatusController 테스트")
class ReadStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReadStatusService readStatusService;

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final UUID TEST_CHANNEL_ID = UUID.randomUUID();
    private static final UUID TEST_READ_STATUS_ID = UUID.randomUUID();
    private static final Instant NOW = Instant.now();

    private void setAuthenticatedUser() {
        UserDetailsDto userDetailsDto = new UserDetailsDto(TEST_USER_ID, "testuser", Role.USER);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDetailsDto, "password");
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private ReadStatusDto createReadStatusDto() {
        return new ReadStatusDto(
            TEST_READ_STATUS_ID,
            TEST_USER_ID,
            TEST_CHANNEL_ID,
            NOW,
            true
        );
    }

    @Nested
    @DisplayName("POST /api/readStatuses")
    class Create {

        @Test
        @DisplayName("유효한 요청으로 ReadStatus 생성 시 201 반환")
        void create_withValidRequest_returns201() throws Exception {
            // given
            setAuthenticatedUser();
            ReadStatusCreateRequest request = new ReadStatusCreateRequest(TEST_CHANNEL_ID, NOW);
            ReadStatusDto responseDto = createReadStatusDto();

            given(readStatusService.create(eq(TEST_USER_ID), any(ReadStatusCreateRequest.class)))
                .willReturn(responseDto);

            // when & then
            mockMvc.perform(post("/api/readStatuses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_READ_STATUS_ID.toString()))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$.channelId").value(TEST_CHANNEL_ID.toString()))
                .andExpect(jsonPath("$.notificationEnabled").value(true));
        }

        @Test
        @DisplayName("인증 없이 ReadStatus 생성 시 403 반환")
        void create_withoutAuth_returns403() throws Exception {
            // given
            ReadStatusCreateRequest request = new ReadStatusCreateRequest(TEST_CHANNEL_ID, NOW);

            // when & then
            mockMvc.perform(post("/api/readStatuses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("channelId가 null인 경우 400 반환")
        void create_withNullChannelId_returns400() throws Exception {
            // given
            setAuthenticatedUser();
            String invalidRequest = """
                {
                    "channelId": null,
                    "lastReadAt": "%s"
                }
                """.formatted(NOW.toString());

            // when & then
            mockMvc.perform(post("/api/readStatuses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidRequest))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("lastReadAt이 null인 경우 400 반환")
        void create_withNullLastReadAt_returns400() throws Exception {
            // given
            setAuthenticatedUser();
            String invalidRequest = """
                {
                    "channelId": "%s",
                    "lastReadAt": null
                }
                """.formatted(TEST_CHANNEL_ID.toString());

            // when & then
            mockMvc.perform(post("/api/readStatuses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidRequest))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("존재하지 않는 채널 ID로 생성 시 404 반환")
        void create_withNonExistingChannel_returns404() throws Exception {
            // given
            setAuthenticatedUser();
            UUID nonExistingChannelId = UUID.randomUUID();
            ReadStatusCreateRequest request = new ReadStatusCreateRequest(nonExistingChannelId, NOW);

            given(readStatusService.create(eq(TEST_USER_ID), any(ReadStatusCreateRequest.class)))
                .willThrow(new ChannelNotFoundException(nonExistingChannelId));

            // when & then
            mockMvc.perform(post("/api/readStatuses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/readStatuses")
    class FindAllByUserId {

        @Test
        @DisplayName("인증된 사용자의 ReadStatus 목록 조회 시 200 반환")
        void findAllByUserId_withAuthenticatedUser_returns200() throws Exception {
            // given
            setAuthenticatedUser();
            ReadStatusDto dto1 = new ReadStatusDto(
                UUID.randomUUID(), TEST_USER_ID, UUID.randomUUID(), NOW, true);
            ReadStatusDto dto2 = new ReadStatusDto(
                UUID.randomUUID(), TEST_USER_ID, UUID.randomUUID(), NOW, false);

            given(readStatusService.findAllByUserId(TEST_USER_ID))
                .willReturn(List.of(dto1, dto2));

            // when & then
            mockMvc.perform(get("/api/readStatuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID.toString()));
        }

        @Test
        @DisplayName("ReadStatus가 없을 때 빈 배열 반환")
        void findAllByUserId_withNoReadStatuses_returnsEmptyArray() throws Exception {
            // given
            setAuthenticatedUser();

            given(readStatusService.findAllByUserId(TEST_USER_ID))
                .willReturn(List.of());

            // when & then
            mockMvc.perform(get("/api/readStatuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("인증 없이 조회 시 403 반환")
        void findAllByUserId_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(get("/api/readStatuses"))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/readStatuses/{readStatusId}")
    class Update {

        @Test
        @DisplayName("유효한 요청으로 ReadStatus 수정 시 200 반환")
        void update_withValidRequest_returns200() throws Exception {
            // given
            setAuthenticatedUser();
            ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(NOW.plusSeconds(3600), false);
            ReadStatusDto responseDto = new ReadStatusDto(
                TEST_READ_STATUS_ID, TEST_USER_ID, TEST_CHANNEL_ID, NOW.plusSeconds(3600), false);

            given(readStatusService.update(
                eq(TEST_READ_STATUS_ID), eq(TEST_USER_ID), any(ReadStatusUpdateRequest.class)))
                .willReturn(responseDto);

            // when & then
            mockMvc.perform(patch("/api/readStatuses/{readStatusId}", TEST_READ_STATUS_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_READ_STATUS_ID.toString()))
                .andExpect(jsonPath("$.notificationEnabled").value(false));
        }

        @Test
        @DisplayName("lastReadAt만 수정 시 200 반환")
        void update_withOnlyLastReadAt_returns200() throws Exception {
            // given
            setAuthenticatedUser();
            ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(NOW.plusSeconds(3600), null);
            ReadStatusDto responseDto = new ReadStatusDto(
                TEST_READ_STATUS_ID, TEST_USER_ID, TEST_CHANNEL_ID, NOW.plusSeconds(3600), true);

            given(readStatusService.update(
                eq(TEST_READ_STATUS_ID), eq(TEST_USER_ID), any(ReadStatusUpdateRequest.class)))
                .willReturn(responseDto);

            // when & then
            mockMvc.perform(patch("/api/readStatuses/{readStatusId}", TEST_READ_STATUS_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationEnabled").value(true));
        }

        @Test
        @DisplayName("notificationEnabled만 수정 시 200 반환")
        void update_withOnlyNotificationEnabled_returns200() throws Exception {
            // given
            setAuthenticatedUser();
            ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(null, false);
            ReadStatusDto responseDto = new ReadStatusDto(
                TEST_READ_STATUS_ID, TEST_USER_ID, TEST_CHANNEL_ID, NOW, false);

            given(readStatusService.update(
                eq(TEST_READ_STATUS_ID), eq(TEST_USER_ID), any(ReadStatusUpdateRequest.class)))
                .willReturn(responseDto);

            // when & then
            mockMvc.perform(patch("/api/readStatuses/{readStatusId}", TEST_READ_STATUS_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationEnabled").value(false));
        }

        @Test
        @DisplayName("인증 없이 수정 시 403 반환")
        void update_withoutAuth_returns403() throws Exception {
            // given
            ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(NOW, true);

            // when & then
            mockMvc.perform(patch("/api/readStatuses/{readStatusId}", TEST_READ_STATUS_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("존재하지 않는 ReadStatus 수정 시 404 반환")
        void update_withNonExistingReadStatus_returns404() throws Exception {
            // given
            setAuthenticatedUser();
            UUID nonExistingId = UUID.randomUUID();
            ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(NOW, true);

            given(readStatusService.update(eq(nonExistingId), eq(TEST_USER_ID), any(ReadStatusUpdateRequest.class)))
                .willThrow(new ReadStatusNotFoundException(nonExistingId));

            // when & then
            mockMvc.perform(patch("/api/readStatuses/{readStatusId}", nonExistingId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("다른 사용자의 ReadStatus 수정 시 403 반환")
        void update_withOtherUserReadStatus_returns403() throws Exception {
            // given
            setAuthenticatedUser();
            ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(NOW, true);

            given(readStatusService.update(
                eq(TEST_READ_STATUS_ID), eq(TEST_USER_ID), any(ReadStatusUpdateRequest.class)))
                .willThrow(new ReadStatusForbiddenException(TEST_READ_STATUS_ID, TEST_USER_ID));

            // when & then
            mockMvc.perform(patch("/api/readStatuses/{readStatusId}", TEST_READ_STATUS_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }
    }
}
