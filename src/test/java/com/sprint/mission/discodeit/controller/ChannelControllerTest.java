package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  @MockitoBean
  private ChannelMapper channelMapper;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  private ChannelDto.Detail channelDetail;
  private ChannelDto.DetailResponse channelDetailResponse;

  @BeforeEach
  void setup() {
    UUID channelId = UUID.randomUUID();

    channelDetail = ChannelDto.Detail.builder()
                                     .id(channelId)
                                     .name("test-channel")
                                     .type(ChannelType.PUBLIC)
                                     .build();

    channelDetailResponse = ChannelDto.DetailResponse.builder()
                                                     .id(channelId)
                                                     .name("test-channel")
                                                     .type(ChannelType.PUBLIC)
                                                     .build();
  }

  @Test
  void testCreatePublicChannel() throws Exception {
    ChannelDto.CreateRequest request = ChannelDto.CreateRequest.builder()
                                                               .name("test-channel")
                                                               .build();

    when(channelService.create(any())).thenReturn(channelDetail);
    when(channelMapper.toDetailResponse(any())).thenReturn(channelDetailResponse);

    mockMvc.perform(post("/api/channels/public").contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.name").value("test-channel"));
  }

  @Test
  void testCreatePrivateChannel() throws Exception {
    ChannelDto.CreateRequest request = ChannelDto.CreateRequest.builder()
                                                               .name("private-channel")
                                                               .build();

    ChannelDto.Detail privateDetail = ChannelDto.Detail.builder()
                                                       .id(UUID.randomUUID())
                                                       .name("private-channel")
                                                       .type(ChannelType.PRIVATE)
                                                       .build();

    ChannelDto.DetailResponse privateResponse = ChannelDto.DetailResponse.builder()
                                                                         .id(privateDetail.getId())
                                                                         .name("private-channel")
                                                                         .type(ChannelType.PRIVATE)
                                                                         .build();

    when(channelService.create(any())).thenReturn(privateDetail);
    when(channelMapper.toDetailResponse(any())).thenReturn(privateResponse);

    mockMvc.perform(post("/api/channels/private").contentType(MediaType.APPLICATION_JSON)
                                                 .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.type").value("PRIVATE"));
  }

  @Test
  void testUpdateChannel() throws Exception {
    UUID channelId = channelDetail.getId();
    ChannelDto.UpdateRequest request = ChannelDto.UpdateRequest.builder()
                                                               .name("updated-channel")
                                                               .build();

    ChannelDto.Detail updatedDetail = ChannelDto.Detail.builder()
                                                       .id(channelId)
                                                       .name("updated-channel")
                                                       .type(ChannelType.PUBLIC)
                                                       .build();

    ChannelDto.DetailResponse updatedResponse = ChannelDto.DetailResponse.builder()
                                                                         .id(channelId)
                                                                         .name("updated-channel")
                                                                         .type(ChannelType.PUBLIC)
                                                                         .build();

    when(channelService.update(any())).thenReturn(updatedDetail);
    when(channelMapper.toDetailResponse(any())).thenReturn(updatedResponse);

    mockMvc.perform(put("/api/channels/" + channelId).contentType(MediaType.APPLICATION_JSON)
                                                     .content(
                                                         objectMapper.writeValueAsString(request)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name").value("updated-channel"));
  }

  @Test
  void testDeleteChannel() throws Exception {
    UUID channelId = channelDetail.getId();

    mockMvc.perform(delete("/api/channels/" + channelId))
           .andExpect(status().isNoContent());
  }

  @Test
  void testFindUserChannels() throws Exception {
    UUID userId = UUID.randomUUID();

    when(channelService.findAllByUserId(eq(userId))).thenReturn(List.of(channelDetail));
    when(channelMapper.toDetailResponse(any())).thenReturn(channelDetailResponse);

    mockMvc.perform(get("/api/channels").param("userId", userId.toString())
                                        .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].name").value("test-channel"));
  }
}
