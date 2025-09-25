package com.sprint.mission.discodeit.controller.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.channel.ChannelController;
import com.sprint.mission.discodeit.domain.channel.ChannelService;
import com.sprint.mission.discodeit.domain.channel.dto.ChannelDto;
import com.sprint.mission.discodeit.domain.channel.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.domain.channel.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.domain.channel.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.domain.channel.exception.ChannelAlreadyExistsException;
import com.sprint.mission.discodeit.domain.channel.exception.ChannelNotFoundException;
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
public class ChannelControllerFailTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChannelService channelService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("공개 채널 생성 실패 - 이미 존재하는 이름")
    void create_public_channel_fail_already_exists() throws Exception {
        // Given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "General discussion");

        BDDMockito.given(channelService.create(any(PublicChannelCreateRequest.class)))
                .willThrow(new ChannelAlreadyExistsException("general"));

        // When & Then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()); // Expect 409 Conflict
    }

    @Test
    @DisplayName("채널 수정 실패 - 존재하지 않는 채널")
    void update_channel_fail_not_found() throws Exception {
        // Given
        UUID nonExistentChannelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new-name", "new-description");

        // Mocking the service to throw an exception for a non-existent channel
        BDDMockito.given(channelService.update(eq(nonExistentChannelId), any(PublicChannelUpdateRequest.class)))
                .willThrow(new ChannelNotFoundException(nonExistentChannelId));

        // When & Then
        mockMvc.perform(patch("/api/channels/{channelId}", nonExistentChannelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound()); // Expect 404 Not Found
    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않는 채널")
    void delete_channel_fail_not_found() throws Exception {
        // Given
        UUID nonExistentChannelId = UUID.randomUUID();

        // Mocking the service (a void method) to throw an exception
        BDDMockito.willThrow(new ChannelNotFoundException(nonExistentChannelId))
                .given(channelService).delete(eq(nonExistentChannelId));

        // When & Then
        mockMvc.perform(delete("/api/channels/{channelId}", nonExistentChannelId))
                .andExpect(status().isNotFound()); // Expect 404 Not Found
    }
}
