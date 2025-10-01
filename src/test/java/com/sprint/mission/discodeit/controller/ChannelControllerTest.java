package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void createPublicChannelSuccess() throws Exception {
        ChannelDto responseDto = new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PUBLIC,
                "general",
                "테스트 채널",
                List.of(),
                Instant.now()
        );

        Mockito.when(channelService.create(any(PublicChannelCreateRequest.class)))
                .thenReturn(responseDto);

        PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "테스트 채널");

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("general"))
                .andExpect(jsonPath("$.description").value("테스트 채널"))
                .andExpect(jsonPath("$.type").value("PUBLIC"));
    }

    @Test
    @DisplayName("비공개 채널 생성 실패 - 요청 값 없음")
    void createPrivateChannelFail() throws Exception {
        // participantIds가 null → @Validated 실패 유도
        PrivateChannelCreateRequest invalidRequest = new PrivateChannelCreateRequest(null);

        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("채널 수정 성공")
    void updateChannelSuccess() throws Exception {
        UUID channelId = UUID.randomUUID();
        ChannelDto responseDto = new ChannelDto(
                channelId,
                ChannelType.PUBLIC,
                "newName",
                "newDesc",
                List.of(),
                Instant.now()
        );

        Mockito.when(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
                .thenReturn(responseDto);

        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDesc");

        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newName"))
                .andExpect(jsonPath("$.description").value("newDesc"));
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannelSuccess() throws Exception {
        UUID channelId = UUID.randomUUID();

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());

        Mockito.verify(channelService).delete(eq(channelId));
    }

    @Test
    @DisplayName("사용자 채널 전체 조회 성공")
    void findAllChannelsByUserSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        List<ChannelDto> channels = List.of(
                new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "general", "desc1", List.of(), Instant.now()),
                new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, "private", "desc2", List.of(), Instant.now())
        );

        Mockito.when(channelService.findAllByUserId(eq(userId))).thenReturn(channels);

        mockMvc.perform(get("/api/channels")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("general"))
                .andExpect(jsonPath("$[1].type").value("PRIVATE"));
    }
}