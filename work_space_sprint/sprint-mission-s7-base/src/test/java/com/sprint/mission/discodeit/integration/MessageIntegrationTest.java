package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Message API 통합 테스트")
class MessageIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MessageRepository messageRepository;

  private String testUserId;
  private String testChannelId;

  @BeforeEach
  void setUp() throws Exception {
    // 테스트용 사용자 생성
    UserCreateRequest userRequest = new UserCreateRequest(
        "messageuser",
        "message@example.com",
        "password123"
    );

    String userJson = objectMapper.writeValueAsString(userRequest);
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        userJson.getBytes()
    );

    MvcResult userResult = mockMvc.perform(multipart("/api/users")
        .file(userPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    testUserId = objectMapper.readTree(userResult.getResponse().getContentAsString())
        .get("id").asText();

    // 테스트용 채널 생성
    PublicChannelCreateRequest channelRequest = new PublicChannelCreateRequest(
        "Test Channel",
        "Test Description"
    );

    MvcResult channelResult = mockMvc.perform(post("/api/channels/public")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(channelRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    testChannelId = objectMapper.readTree(channelResult.getResponse().getContentAsString())
        .get("id").asText();
  }

  @Test
  @DisplayName("메시지 생성 통합 테스트 - 성공")
  void createMessage_Integration_Success() throws Exception {
    // given
    MessageCreateRequest request = new MessageCreateRequest(
        "Hello, World!",
        java.util.UUID.fromString(testChannelId),
        java.util.UUID.fromString(testUserId)
    );

    String requestJson = objectMapper.writeValueAsString(request);
    MockMultipartFile messagePart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        requestJson.getBytes()
    );

    // when & then
    MvcResult result = mockMvc.perform(multipart("/api/messages")
            .file(messagePart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Hello, World!"))
        .andExpect(jsonPath("$.id").exists())
        .andReturn();

    // 데이터베이스에 실제로 저장되었는지 검증
    String responseJson = result.getResponse().getContentAsString();
    String messageId = objectMapper.readTree(responseJson).get("id").asText();
    
    assertThat(messageRepository.findById(java.util.UUID.fromString(messageId))).isPresent();
  }

  @Test
  @DisplayName("메시지 생성 통합 테스트 - 유효성 검증 실패")
  void createMessage_Integration_ValidationFailed() throws Exception {
    // given - 빈 메시지 내용
    MessageCreateRequest request = new MessageCreateRequest(
        "",
        java.util.UUID.fromString(testChannelId),
        java.util.UUID.fromString(testUserId)
    );

    String requestJson = objectMapper.writeValueAsString(request);
    MockMultipartFile messagePart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        requestJson.getBytes()
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(messagePart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
  }

  @Test
  @DisplayName("메시지 수정 통합 테스트 - 성공")
  void updateMessage_Integration_Success() throws Exception {
    // given - 메시지 먼저 생성
    MessageCreateRequest createRequest = new MessageCreateRequest(
        "Original Message",
        java.util.UUID.fromString(testChannelId),
        java.util.UUID.fromString(testUserId)
    );

    String createJson = objectMapper.writeValueAsString(createRequest);
    MockMultipartFile createPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        createJson.getBytes()
    );

    MvcResult createResult = mockMvc.perform(multipart("/api/messages")
        .file(createPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    String messageId = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asText();

    // when - 메시지 수정
    MessageUpdateRequest updateRequest = new MessageUpdateRequest("Updated Message");

    // then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("Updated Message"));

    // 데이터베이스에서 변경사항 검증
    var updatedMessage = messageRepository.findById(java.util.UUID.fromString(messageId));
    assertThat(updatedMessage).isPresent();
    assertThat(updatedMessage.get().getContent()).isEqualTo("Updated Message");
  }

  @Test
  @DisplayName("메시지 삭제 통합 테스트 - 성공")
  void deleteMessage_Integration_Success() throws Exception {
    // given - 메시지 먼저 생성
    MessageCreateRequest createRequest = new MessageCreateRequest(
        "Message to delete",
        java.util.UUID.fromString(testChannelId),
        java.util.UUID.fromString(testUserId)
    );

    String createJson = objectMapper.writeValueAsString(createRequest);
    MockMultipartFile createPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        createJson.getBytes()
    );

    MvcResult createResult = mockMvc.perform(multipart("/api/messages")
        .file(createPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    String messageId = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asText();

    // when - 메시지 삭제
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());

    // then - 데이터베이스에서 삭제되었는지 검증
    assertThat(messageRepository.findById(java.util.UUID.fromString(messageId))).isEmpty();
  }

  @Test
  @DisplayName("채널의 메시지 목록 조회 통합 테스트 - 성공")
  void findMessagesByChannel_Integration_Success() throws Exception {
    // given - 여러 메시지 생성
    for (int i = 1; i <= 3; i++) {
      MessageCreateRequest request = new MessageCreateRequest(
          "Message " + i,
          java.util.UUID.fromString(testChannelId),
          java.util.UUID.fromString(testUserId)
      );

      String json = objectMapper.writeValueAsString(request);
      MockMultipartFile part = new MockMultipartFile(
          "messageCreateRequest",
          "",
          MediaType.APPLICATION_JSON_VALUE,
          json.getBytes()
      );

      mockMvc.perform(multipart("/api/messages")
          .file(part)
          .contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isCreated());
    }

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", testChannelId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(3))
        .andExpect(jsonPath("$.hasNext").exists())
        .andExpect(jsonPath("$.size").exists());
  }
}