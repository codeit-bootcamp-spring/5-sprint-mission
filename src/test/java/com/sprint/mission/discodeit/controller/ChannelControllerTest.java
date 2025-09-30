package com.sprint.mission.discodeit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/* @WebMvcTest를 활용해 테스트를 구현
 * 서비스레이어를 모의(mock)하여 컨트롤러 로직만 테스트
 * JSON 응답을 검증하는 테스트를 포함
 */

@WebMvcTest(ChannelController.class)
public class ChannelControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper; // JSON 변환용

  @MockBean
  ChannelService channelService; // mock 주입 (진짜 서비스 아님)

  /* 전체 채널 조회 성공
   * GET /api/channels
   */
  @Test
  void findAll_success() throws Exception {

    // given
    ChannelDto dto = new ChannelDto();
    dto.setId(UUID.randomUUID());
    dto.setName("테스트채널");
    dto.setDescription("설명");
    dto.setChannelType(ChannelType.PUBLIC);

    BDDMockito.given(channelService.findAll()).willReturn(List.of(dto));

    // when & then
    mockMvc.perform(get("/api/channels")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("테스트채널"))
        .andExpect(jsonPath("$[0].channelType").value("PUBLIC"));
  }

  /* 전체 채널 조회 실패 - 데이터 없음(빈 배열)
   * GET /api/channels
   */
  @Test
  void findAll_empty() throws Exception {

    // given
    BDDMockito.given(channelService.findAll()).willReturn(Collections.emptyList());

    // when & then
    mockMvc.perform(get("/api/channels")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  /* 채널 단건 조회 성공
   * GET /api/channels/{channelId}
   */
  @Test
  void findById_success() throws Exception {

    // given
    UUID channelId = UUID.randomUUID();
    ChannelDto dto = new ChannelDto();
    dto.setId(channelId);
    dto.setName("단일채널");
    dto.setDescription("단일채널설명");
    dto.setChannelType(ChannelType.PRIVATE);

    BDDMockito.given(channelService.findById(channelId)).willReturn(dto);

    // when & then
    mockMvc.perform(get("/api/channels/" + channelId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.name").value("단일채널"))
        .andExpect(jsonPath("$.channelType").value("PRIVATE"));
  }

  /* 채널 단건 조회 실패 - 존재하지 않는 채널
   * GET /api/channels/{channelId}
   */
  @Test
  void findById_fail_notFound() throws Exception {

    // given
    UUID channelId = UUID.randomUUID();
    BDDMockito.given(channelService.findById(channelId))
        .willThrow(new ChannelNotFoundException());

    // when & then
    mockMvc.perform(get("/api/channels/" + channelId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  /* 공개 채널 생성 성공
   * POST /api/channels/public
   */
  @Test
  void createPublic_success() throws Exception {

    // given
    ChannelDto dto = new ChannelDto();
    dto.setName("새채널");
    dto.setDescription("공개설명");
    dto.setChannelType(ChannelType.PUBLIC);

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated());
  }

  /* 공개 채널 생성 실패 - name 누락(유효성검증 실패)
   * POST /api/channels/public
   */
  @Test
  void createPublic_fail_invalid() throws Exception {

    // given
    ChannelDto dto = new ChannelDto();
    dto.setDescription("설명");
    dto.setChannelType(ChannelType.PUBLIC);

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().is4xxClientError())
        //JSON 구조랑 맞춰줘야 함
        .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));

  }
}

