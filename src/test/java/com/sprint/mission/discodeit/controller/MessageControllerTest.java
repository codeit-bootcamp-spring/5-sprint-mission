package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.CreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto.Detail;
import com.sprint.mission.discodeit.dto.MessageDto.UpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;

  @MockitoBean
  private MessageMapper messageMapper;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  private UserDto.DetailResponse userDetailResponse;
  private ChannelDto.DetailResponse channelDetailResponse;
  private MessageDto.Detail messageDetail;
  private MessageDto.DetailResponse messageDetailResponse;

  @BeforeEach
  void setup() {
    UUID messageId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();

    Instant fixed = Instant.parse("2025-09-22T00:00:00Z");

    UserDto.Detail userDetail = UserDto.Detail.builder()
                                              .id(userId)
                                              .email("test@test.com")
                                              .username("test-user")
                                              .build();

    ChannelDto.Detail channelDetail = ChannelDto.Detail.builder()
                                                       .id(channelId)
                                                       .name("test-channel")
                                                       .description("test-description")
                                                       .type(ChannelType.PUBLIC)
                                                       .build();

    messageDetail = MessageDto.Detail.builder()
                                     .id(messageId)
                                     .channel(channelDetail)
                                     .author(userDetail)
                                     .content("test-message")
                                     .createdAt(fixed)
                                     .build();

    userDetailResponse = UserDto.DetailResponse.builder()
                                               .id(userId)
                                               .email("test@test.com")
                                               .username("test-user")
                                               .build();

    channelDetailResponse = ChannelDto.DetailResponse.builder()
                                                     .id(channelId)
                                                     .name("test-channel")
                                                     .description("test-description")
                                                     .type(ChannelType.PUBLIC)
                                                     .build();

    messageDetailResponse = MessageDto.DetailResponse.builder()
                                                     .id(messageId)
                                                     .channel(channelDetailResponse)
                                                     .author(userDetailResponse)
                                                     .content("test-message")
                                                     .createdAt(fixed)
                                                     .build();
  }

  @Test
  void testCreateMessage() throws Exception {
    CreateRequest request = CreateRequest.builder()
                                         .channelId(messageDetail.getChannel()
                                                                 .getId())
                                         .authorId(messageDetail.getAuthor()
                                                                .getId())
                                         .content("test-message")
                                         .build();

    MockMultipartFile jsonPart = new MockMultipartFile("messageCreateRequest", "",
        "application/json", objectMapper.writeValueAsBytes(request));

    MockMultipartFile filePart = new MockMultipartFile("attachments", "file.txt", "text/plain",
        "dummy".getBytes());

    when(messageService.create(any())).thenReturn(messageDetail);
    when(messageMapper.toDetailResponse(any())).thenReturn(messageDetailResponse);

    mockMvc.perform(multipart("/api/messages").file(jsonPart)
                                              .file(filePart)
                                              .contentType(MediaType.MULTIPART_FORM_DATA))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.content").value("test-message"));
  }

  @Test
  void testUpdateMessage() throws Exception {
    UUID messageId = messageDetail.getId();
    UpdateRequest request = UpdateRequest.builder()
                                         .newContent("updated-message")
                                         .build();

    MessageDto.Detail updatedDetail = MessageDto.Detail.builder()
                                                       .id(messageId)
                                                       .channel(messageDetail.getChannel())
                                                       .author(messageDetail.getAuthor())
                                                       .content("updated-message")
                                                       .createdAt(messageDetail.getCreatedAt())
                                                       .build();

    MessageDto.DetailResponse updatedResponse = MessageDto.DetailResponse.builder()
                                                                         .id(messageId)
                                                                         .channel(
                                                                             channelDetailResponse)
                                                                         .author(userDetailResponse)
                                                                         .content("updated-message")
                                                                         .createdAt(
                                                                             messageDetail.getCreatedAt())
                                                                         .build();

    when(messageService.update(any())).thenReturn(updatedDetail);
    when(messageMapper.toDetailResponse(any())).thenReturn(updatedResponse);

    mockMvc.perform(patch("/api/messages/" + messageId).contentType(MediaType.APPLICATION_JSON)
                                                       .content(objectMapper.writeValueAsString(
                                                           request)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content").value("updated-message"));
  }

  @Test
  void testDeleteMessage() throws Exception {
    UUID messageId = messageDetail.getId();

    mockMvc.perform(delete("/api/messages/" + messageId))
           .andExpect(status().isNoContent());
  }

  @Test
  void testGetMessagesByChannel() throws Exception {
    UUID channelId = messageDetail.getChannel()
                                  .getId();
    PageResponse<Detail> response = PageResponse.of(List.of(messageDetail), null, 10, false, 1L);

    when(messageService.findAllByChannelId(eq(channelId), any(), any())).thenReturn(response);
    when(messageMapper.toDetailResponse(any())).thenReturn(messageDetailResponse);

    mockMvc.perform(get("/api/messages").param("channelId", channelId.toString())
                                        .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content[0].content").value("test-message"));
  }
}
