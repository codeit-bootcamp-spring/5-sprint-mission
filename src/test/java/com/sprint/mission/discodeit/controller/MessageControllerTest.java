package com.sprint.mission.discodeit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

/* @WebMvcTest를 활용해 테스트를 구현
 * 서비스레이어를 모의(mock)하여 컨트롤러 로직만 테스트
 * JSON 응답을 검증하는 테스트를 포함
 */

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper; // JSON 변환용

  @MockBean
  MessageService messageService; // mock 주입 (진짜 서비스 아님)

  /* 메시지 단건 조회 성공
   * GET /api/messages/{messageId}
   */
  @Test
  void findById_success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    MessageDto dto = new MessageDto();
    dto.setId(messageId);
    dto.setContent("테스트메시지");
    dto.setChannelId(UUID.randomUUID());
    dto.setAuthorId(UUID.randomUUID());

    BDDMockito.given(messageService.findById(messageId)).willReturn(dto);

    // when & then
    mockMvc.perform(get("/api/messages/" + messageId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("테스트메시지"));
  }

  /* 채널 내 모든 메시지 조회 성공
   * GET /api/messages?channelId={channelId}
   */
  @Test
  void findAllByChannelId_success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    MessageDto dto = new MessageDto();
    dto.setId(UUID.randomUUID());
    dto.setContent("메시지1");
    dto.setChannelId(channelId);

    BDDMockito.given(messageService.findAllByChannelId(channelId)).willReturn(List.of(dto));

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].content").value("메시지1"))
        .andExpect(jsonPath("$[0].channelId").value(channelId.toString()));
  }

  /* 메시지 단건 조회 실패 - 존재하지 않는 메시지
   * GET /api/messages/{messageId}
   */
  @Test
  void findById_fail_notFound() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    BDDMockito.given(messageService.findById(messageId))
        .willThrow(new MessageNotFoundException());

    // when & then
    mockMvc.perform(get("/api/messages/" + messageId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  /* 메시지 생성 성공 (multipart/form-data)
   * POST /api/messages (multipart)
   */
  @Test
  void create_success() throws Exception {
    // given
    MessageDto requestDto = new MessageDto();
    requestDto.setContent("새 메시지");
    requestDto.setChannelId(UUID.randomUUID());
    requestDto.setAuthorId(UUID.randomUUID());

    MessageDto responseDto = new MessageDto();
    responseDto.setId(UUID.randomUUID());
    responseDto.setContent("새 메시지");
    responseDto.setChannelId(requestDto.getChannelId());
    responseDto.setAuthorId(requestDto.getAuthorId());

    BDDMockito.given(messageService.create(
            BDDMockito.any(MessageDto.class),
            BDDMockito.anyList()))
        .willReturn(responseDto);

    /* multipart/form-data 요청 파트 구성 */
    MockMultipartFile messageDtoPart = new MockMultipartFile(
        "messageDto", null,
        "application/json",
        objectMapper.writeValueAsBytes(requestDto)
    );
    MockMultipartFile filePart = new MockMultipartFile(
        "attachments", "file.txt",
        "text/plain",
        "test file".getBytes()
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(messageDtoPart)
            .file(filePart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("새 메시지"));
  }

  /* 메시지 생성 실패
   * 내용 누락(유효성검증 실패)
   * POST /api/messages (multipart)
   */
  @Test
  void create_fail_invalid() throws Exception {
    // given
    MessageDto requestDto = new MessageDto();
    // content 누락 (null)
    requestDto.setChannelId(UUID.randomUUID());
    requestDto.setAuthorId(UUID.randomUUID());

    MockMultipartFile messageDtoPart = new MockMultipartFile(
        "messageDto", null,
        "application/json",
        objectMapper.writeValueAsBytes(requestDto)
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(messageDtoPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fieldErrors[0].field").value("content"));
  }

  /* 메시지 수정 성공
   * PATCH /api/messages/{messageId}
   */
  @Test
  void update_success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    MessageDto requestDto = new MessageDto();
    requestDto.setContent("dummy");
    // requestDto.setNewContent("수정된 메시지");

    MessageDto responseDto = new MessageDto();
    responseDto.setId(messageId);
    responseDto.setContent("수정된 메시지");

    BDDMockito.given(messageService.update(messageId, requestDto)).willReturn(responseDto);

    // when & then
    mockMvc.perform(patch("/api/messages/" + messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("수정된 메시지"));
  }

  /* 메시지 삭제 성공
   * DELETE /api/messages/{messageId}
   */
  @Test
  void delete_success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    BDDMockito.doNothing().when(messageService).delete(messageId);

    // when & then
    mockMvc.perform(delete("/api/messages/" + messageId))
        .andExpect(status().isNoContent());
  }
}

