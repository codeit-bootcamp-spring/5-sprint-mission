package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.channel.ChannelLeaveRequest;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelDeleteResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelLeaveResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ChannelController.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChannelService channelService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private UUID channelId;
    private ChannelResponse channelResponse;

    @BeforeEach
    void setUp() {
        channelId = UUID.randomUUID();
        channelResponse = ChannelResponse.builder()
                .id(channelId)
                .name("test")
                .type(ChannelType.PUBLIC)
                .description("test channel")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("공개 채널 생성 성공")
    void create_publicChannel_success() throws Exception {
        // given
        PublicChannelCreateRequest request = PublicChannelCreateRequest.builder()
                .name("test")
                .description("test channel")
                .build();

        given(channelService.create(any(PublicChannelCreateRequest.class)))
                .willReturn(channelResponse);

        // when
        // then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.type").value("PUBLIC"));
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void createPrivateChannel_success() throws Exception {
        // given
        PrivateChannelCreateRequest request = PrivateChannelCreateRequest.builder()
                .participantIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .build();

        ChannelResponse privateChannelResponse = ChannelResponse.builder()
                .id(channelId)
                .name("private-" + channelId)
                .type(ChannelType.PRIVATE)
                .createdAt(Instant.now())
                .build();

        given(channelService.create(any(PrivateChannelCreateRequest.class)))
                .willReturn(privateChannelResponse);

        // when
        // then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("채널 수정 성공")
    void updateChannel_success() throws Exception {
        // given
        ChannelUpdateRequest request = ChannelUpdateRequest.builder()
                .newName("renamed")
                .newDescription("updated desc")
                .build();

        ChannelResponse updatedResponse = ChannelResponse.builder()
                .id(channelId)
                .name("renamed")
                .description("updated desc")
                .type(ChannelType.PUBLIC)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        given(channelService.updateChannel(eq(channelId), any(ChannelUpdateRequest.class)))
                .willReturn(updatedResponse);

        // when
        // then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("renamed"))
                .andExpect(jsonPath("$.description").value("updated desc"));
    }

    @Test
    @DisplayName("유저 ID로 채널 목록 조회 성공")
    void getChannels_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        ChannelResponse ch1 = ChannelResponse.builder()
                .id(UUID.randomUUID())
                .name("test")
                .type(ChannelType.PUBLIC)
                .build();
        ChannelResponse ch2 = ChannelResponse.builder()
                .id(UUID.randomUUID())
                .name("private-" + UUID.randomUUID())
                .type(ChannelType.PRIVATE)
                .build();

        given(channelService.findChannelsByUserId(userId)).willReturn(List.of(ch1, ch2));

        // when
        // then
        mockMvc.perform(get("/api/channels")
                .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("특정 채널 조회 성공")
    void getChannel_success() throws Exception {
        // given
        given(channelService.find(channelId)).willReturn(channelResponse);

        // when
        // then
        mockMvc.perform(get("/api/channels/{channelId}", channelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    @DisplayName("존재 하지 않는 채널 조회 404 반환")
    void getChannel_notFound() throws Exception {
        // given
        given(channelService.find(channelId)).willReturn(null);

        // when
        // then
        mockMvc.perform(get("/api/channels/{channelId}", channelId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널 나가기 성공")
    void leaveChannel_success() throws Exception {
        // given
        ChannelLeaveRequest request = ChannelLeaveRequest.builder()
                .channelId(channelId)
                .userId(UUID.randomUUID())
                .build();

        ChannelLeaveResponse response = ChannelLeaveResponse.builder()
                .channelId(channelId)
                .userId(request.getUserId())
                .success(true)
                .build();

        given(channelService.leaveChannel(any(ChannelLeaveRequest.class)))
                .willReturn(response);

        // when
        // then
        mockMvc.perform(post("/api/channels/{channelId}/leave", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("존재하지 않는 채널 나가기 400 반환")
    void leaveChannel_badRequest() throws Exception {
        // given
        ChannelLeaveRequest request = ChannelLeaveRequest.builder()
                .channelId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .build();

        // when
        // then
        mockMvc.perform(post("/api/channels/{channelId}/leave", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannel_success() throws Exception {
        // given
        ChannelDeleteResponse response = ChannelDeleteResponse.builder()
                .channelId(channelId)
                .success(true)
                .build();

        given(channelService.deleteChannel(channelId)).willReturn(response);

        // when
        // then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.success").value(true));
    }
}