package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.config.TestSecurityConfig;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ChannelController.class, excludeFilters = @ComponentScan.Filter(
    type = FilterType.REGEX, pattern = ".*\\.security\\..*|.*\\.config\\.SecurityConfig"))
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@ActiveProfiles("test")
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("POST /api/channels/public - 성공: 공개 채널 생성")
    void createPublic_Success() throws Exception {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
            "General",
            "General discussion channel"
        );
        ChannelDto response = new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PUBLIC,
            "General",
            "General discussion channel",
            List.of(),
            null
        );

        given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("PUBLIC"))
            .andExpect(jsonPath("$.name").value("General"))
            .andExpect(jsonPath("$.description").value("General discussion channel"));

        then(channelService).should().create(any(PublicChannelCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("POST /api/channels/public - 실패: 잘못된 요청 데이터 (유효성 검증 실패)")
    void createPublic_InvalidData_BadRequest() throws Exception {
        // given - name이 빈 문자열
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
            "",
            "Description"
        );

        // when & then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/channels/private - 성공: 비공개 채널 생성")
    void createPrivate_Success() throws Exception {
        // given
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
            Set.of(user1Id, user2Id)
        );
        UserDto user1 = new UserDto(user1Id, "user1", "user1@example.com", null, true, Role.USER);
        UserDto user2 = new UserDto(user2Id, "user2", "user2@example.com", null, false, Role.USER);
        ChannelDto response = new ChannelDto(
            UUID.randomUUID(),
            ChannelType.PRIVATE,
            null,
            null,
            List.of(user1, user2),
            null
        );

        given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("PRIVATE"))
            .andExpect(jsonPath("$.participants.length()").value(2));

        then(channelService).should().create(any(PrivateChannelCreateRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/channels/private - 실패: 잘못된 요청 데이터 (참가자 수 부족)")
    void createPrivate_InvalidData_BadRequest() throws Exception {
        // given - 참가자가 1명만 있음 (최소 2명 필요)
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
            Set.of(UUID.randomUUID())
        );

        // when & then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/channels - 성공: 채널 목록 조회")
    void findAll_Success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        List<ChannelDto> channels = List.of(
            new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PUBLIC,
                "General",
                "General discussion",
                List.of(),
                Instant.now()
            ),
            new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PRIVATE,
                null,
                null,
                List.of(),
                null
            )
        );

        given(channelService.findAll(userId)).willReturn(channels);

        // when & then
        mockMvc.perform(get("/api/channels")
                .param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].type").value("PUBLIC"))
            .andExpect(jsonPath("$[1].type").value("PRIVATE"));

        then(channelService).should().findAll(userId);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/channels - 성공: 빈 채널 목록")
    void findAll_EmptyList() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(channelService.findAll(userId)).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/channels")
                .param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

        then(channelService).should().findAll(userId);
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("PATCH /api/channels/{channelId} - 성공: 채널 정보 수정")
    void update_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
            "Updated Name",
            "Updated Description"
        );
        ChannelDto response = new ChannelDto(
            channelId,
            ChannelType.PUBLIC,
            "Updated Name",
            "Updated Description",
            List.of(),
            null
        );

        given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
            .willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.description").value("Updated Description"));

        then(channelService).should().update(eq(channelId), any(PublicChannelUpdateRequest.class));
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("PATCH /api/channels/{channelId} - 실패: 존재하지 않는 채널")
    void update_ChannelNotFound() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
            "Updated Name",
            "Updated Description"
        );

        given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
            .willThrow(new ChannelNotFoundException());

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));

        then(channelService).should().update(eq(channelId), any(PublicChannelUpdateRequest.class));
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("DELETE /api/channels/{channelId} - 성공: 채널 삭제")
    void delete_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        willDoNothing().given(channelService).delete(channelId);

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId)
                .with(csrf()))
            .andExpect(status().isNoContent());

        then(channelService).should().delete(channelId);
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("DELETE /api/channels/{channelId} - 실패: 존재하지 않는 채널")
    void delete_ChannelNotFound() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        willThrow(new ChannelNotFoundException()).given(channelService).delete(channelId);

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId)
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));

        then(channelService).should().delete(channelId);
    }
}
