package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Channel API 통합 테스트")
class ChannelIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("공개 채널 생성 통합 테스트 - 성공")
  void createPublicChannel_Integration_Success() throws Exception {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(
        "General Chat",
        "General discussion channel"
    );

    // when & then
    MvcResult result = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("General Chat"))
        .andExpect(jsonPath("$.description").value("General discussion channel"))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.id").exists())
        .andReturn();

    // 데이터베이스에 실제로 저장되었는지 검증
    String responseJson = result.getResponse().getContentAsString();
    String channelId = objectMapper.readTree(responseJson).get("id").asText();
    
    assertThat(channelRepository.findById(java.util.UUID.fromString(channelId))).isPresent();
  }

  @Test
  @DisplayName("공개 채널 생성 통합 테스트 - 유효성 검증 실패")
  void createPublicChannel_Integration_ValidationFailed() throws Exception {
    // given - 빈 채널명
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(
        "",
        "Description"
    );

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
  }

  @Test
  @DisplayName("채널 수정 통합 테스트 - 성공")
  void updateChannel_Integration_Success() throws Exception {
    // given - 채널 먼저 생성
    PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(
        "Original Channel",
        "Original Description"
    );

    MvcResult createResult = mockMvc.perform(post("/api/channels/public")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    String channelId = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asText();

    // when - 채널 수정
    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
        "Updated Channel",
        "Updated Description"
    );

    // then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Channel"))
        .andExpect(jsonPath("$.description").value("Updated Description"));

    // 데이터베이스에서 변경사항 검증
    var updatedChannel = channelRepository.findById(java.util.UUID.fromString(channelId));
    assertThat(updatedChannel).isPresent();
    assertThat(updatedChannel.get().getName()).isEqualTo("Updated Channel");
    assertThat(updatedChannel.get().getDescription()).isEqualTo("Updated Description");
  }

  @Test
  @DisplayName("채널 삭제 통합 테스트 - 성공")
  void deleteChannel_Integration_Success() throws Exception {
    // given - 채널 먼저 생성
    PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(
        "Delete Channel",
        "To be deleted"
    );

    MvcResult createResult = mockMvc.perform(post("/api/channels/public")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    String channelId = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asText();

    // when - 채널 삭제
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());

    // then - 데이터베이스에서 삭제되었는지 검증
    assertThat(channelRepository.findById(java.util.UUID.fromString(channelId))).isEmpty();
  }
}