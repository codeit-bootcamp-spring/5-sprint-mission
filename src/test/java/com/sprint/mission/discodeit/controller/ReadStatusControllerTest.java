package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.TestSecurityConfig;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusForbiddenException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.security.userdetails.WithMockDiscodeitUser;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static com.sprint.mission.discodeit.support.TestFixtures.MOCK_USER_ID;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ReadStatusController.class, excludeFilters = @ComponentScan.Filter(
    type = FilterType.REGEX, pattern = ".*\\.security\\..*|.*\\.config\\.SecurityConfig"))
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@ActiveProfiles("test")
class ReadStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReadStatusService readStatusService;

    @Test
    @WithMockDiscodeitUser
    @DisplayName("POST /api/readStatuses - 성공: 읽음 상태 생성")
    void create_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        Instant lastReadAt = Instant.now();
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
            channelId,
            lastReadAt
        );
        ReadStatusDto response = new ReadStatusDto(
            UUID.randomUUID(),
            MOCK_USER_ID,
            channelId,
            lastReadAt,
            false
        );

        given(readStatusService.create(eq(MOCK_USER_ID), any(ReadStatusCreateRequest.class)))
            .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/readStatuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value(MOCK_USER_ID.toString()))
            .andExpect(jsonPath("$.channelId").value(channelId.toString()))
            .andExpect(jsonPath("$.lastReadAt").exists());

        then(readStatusService).should().create(eq(MOCK_USER_ID), any(ReadStatusCreateRequest.class));
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("POST /api/readStatuses - 실패: 잘못된 요청 데이터 (유효성 검증 실패)")
    void create_InvalidData_BadRequest() throws Exception {
        // given - channelId가 null
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
            null,
            Instant.now()
        );

        // when & then
        mockMvc.perform(post("/api/readStatuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("GET /api/readStatuses - 성공: 사용자의 읽음 상태 목록 조회")
    void findAllByUserId_Success() throws Exception {
        // given
        UUID channel1Id = UUID.randomUUID();
        UUID channel2Id = UUID.randomUUID();
        List<ReadStatusDto> readStatuses = List.of(
            new ReadStatusDto(UUID.randomUUID(), MOCK_USER_ID, channel1Id, Instant.now(), false),
            new ReadStatusDto(
                UUID.randomUUID(), MOCK_USER_ID, channel2Id, Instant.now().minusSeconds(3600), false)
        );

        given(readStatusService.findAllByUserId(MOCK_USER_ID)).willReturn(readStatuses);

        // when & then
        mockMvc.perform(get("/api/readStatuses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userId").value(MOCK_USER_ID.toString()))
            .andExpect(jsonPath("$[0].channelId").value(channel1Id.toString()))
            .andExpect(jsonPath("$[1].userId").value(MOCK_USER_ID.toString()))
            .andExpect(jsonPath("$[1].channelId").value(channel2Id.toString()));

        then(readStatusService).should().findAllByUserId(MOCK_USER_ID);
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("GET /api/readStatuses - 성공: 빈 읽음 상태 목록")
    void findAllByUserId_EmptyList() throws Exception {
        // given
        given(readStatusService.findAllByUserId(MOCK_USER_ID)).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/readStatuses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

        then(readStatusService).should().findAllByUserId(MOCK_USER_ID);
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("PATCH /api/readStatuses/{readStatusId} - 성공: 읽음 상태 수정")
    void update_Success() throws Exception {
        // given
        UUID readStatusId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant newLastReadAt = Instant.now();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt, false);
        ReadStatusDto response = new ReadStatusDto(
            readStatusId,
            MOCK_USER_ID,
            channelId,
            newLastReadAt,
            false
        );

        given(readStatusService.update(eq(readStatusId), eq(MOCK_USER_ID), any(ReadStatusUpdateRequest.class)))
            .willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(readStatusId.toString()))
            .andExpect(jsonPath("$.lastReadAt").exists());

        then(readStatusService).should()
            .update(eq(readStatusId), eq(MOCK_USER_ID), any(ReadStatusUpdateRequest.class));
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("PATCH /api/readStatuses/{readStatusId} - 실패: 존재하지 않는 읽음 상태")
    void update_ReadStatusNotFound() throws Exception {
        // given
        UUID readStatusId = UUID.randomUUID();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now(), false);

        given(readStatusService.update(eq(readStatusId), eq(MOCK_USER_ID), any(ReadStatusUpdateRequest.class)))
            .willThrow(new ReadStatusNotFoundException());

        // when & then
        mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("READ_STATUS_NOT_FOUND"));

        then(readStatusService).should()
            .update(eq(readStatusId), eq(MOCK_USER_ID), any(ReadStatusUpdateRequest.class));
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("PATCH /api/readStatuses/{readStatusId} - 실패: 다른 사용자의 읽음 상태 수정 시도")
    void update_Forbidden() throws Exception {
        // given
        UUID readStatusId = UUID.randomUUID();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now(), false);

        given(readStatusService.update(eq(readStatusId), eq(MOCK_USER_ID), any(ReadStatusUpdateRequest.class)))
            .willThrow(new ReadStatusForbiddenException(readStatusId, MOCK_USER_ID));

        // when & then
        mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("READ_STATUS_FORBIDDEN"));

        then(readStatusService).should()
            .update(eq(readStatusId), eq(MOCK_USER_ID), any(ReadStatusUpdateRequest.class));
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("PATCH /api/readStatuses/{readStatusId} - 성공: 읽음 상태 수정 (null 값으로 업데이트)")
    void update_WithNullValue_Success() throws Exception {
        // given
        UUID readStatusId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(null, false);
        ReadStatusDto response = new ReadStatusDto(
            readStatusId,
            MOCK_USER_ID,
            channelId,
            Instant.now(),
            false
        );

        given(readStatusService.update(eq(readStatusId), eq(MOCK_USER_ID), any(ReadStatusUpdateRequest.class)))
            .willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(readStatusId.toString()));

        then(readStatusService).should()
            .update(eq(readStatusId), eq(MOCK_USER_ID), any(ReadStatusUpdateRequest.class));
    }
}
