package com.sprint.mission.discodeit.controller.success;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.channel.ChannelController;
import com.sprint.mission.discodeit.domain.channel.ChannelService;
import com.sprint.mission.discodeit.domain.channel.dto.ChannelDto;
import com.sprint.mission.discodeit.domain.channel.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.domain.channel.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.domain.channel.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ChannelController.class)
public class ChannelControllerSuccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChannelService channelService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void create_public_channel_success() throws Exception {
        // Given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "General discussion");
        ChannelDto response = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "general", "General discussion", Collections.emptyList(), Instant.now());

        BDDMockito.given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(response);

        // When & Then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("general"))
                .andExpect(jsonPath("$.description").value("General discussion"))
                .andExpect(jsonPath("$.type").value("PUBLIC"));
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void create_private_channel_success() throws Exception {
        // Given
        List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        ChannelDto response = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null, Collections.emptyList(), Instant.now());

        BDDMockito.given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(response);

        // When & Then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("PRIVATE"));
    }


    @Test
    @DisplayName("공개 채널 정보 수정 성공")
    void update_public_channel_success() throws Exception {
        // Given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new-name", "new-description");
        ChannelDto response = new ChannelDto(channelId, ChannelType.PUBLIC, "new-name", "new-description", Collections.emptyList(), Instant.now());

        BDDMockito.given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class))).willReturn(response);

        // When & Then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.name").value("new-name"))
                .andExpect(jsonPath("$.description").value("new-description"));
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void delete_channel_success() throws Exception {
        // Given
        UUID channelId = UUID.randomUUID();
        BDDMockito.willDoNothing().given(channelService).delete(channelId);

        // When & Then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());

        // Verify that the delete method was called exactly once
        BDDMockito.then(channelService).should().delete(channelId);
    }

}
