package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ChannelIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void 공개채널생성성공() throws Exception {
        // given
        var request = new PublicChannelCreateRequest("general", "테스트 채널");

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("general"))
                .andExpect(jsonPath("$.description").value("테스트 채널"))
                .andExpect(jsonPath("$.type").value("PUBLIC"));
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void 비공개채널생성성공() throws Exception {
        // given
        User u1 = userRepository.save(new User("u1", "u1@test.com", "pw123456", null));
        User u2 = userRepository.save(new User("u2", "u2@test.com", "pw123456", null));
        var request = new PrivateChannelCreateRequest(List.of(u1.getId(), u2.getId()));

        // when & then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("공개 채널 수정 성공")
    void 공개채널수정성공() throws Exception {
        // given
        var created = channelRepository.save(new com.sprint.mission.discodeit.entity.Channel(
                com.sprint.mission.discodeit.entity.ChannelType.PUBLIC, "oldName", "oldDesc"
        ));

        var updateRequest = new PublicChannelUpdateRequest("newName", "newDesc");

        // when & then
        mockMvc.perform(patch("/api/channels/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newName"))
                .andExpect(jsonPath("$.description").value("newDesc"));
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void 채널삭제성공() throws Exception {
        // given
        var created = channelRepository.save(new com.sprint.mission.discodeit.entity.Channel(
                com.sprint.mission.discodeit.entity.ChannelType.PUBLIC, "deleteMe", "toDelete"
        ));

        // when & then
        mockMvc.perform(delete("/api/channels/{id}", created.getId()))
                .andExpect(status().isNoContent());

        assertThat(channelRepository.existsById(created.getId())).isFalse();
    }

    @Test
    @DisplayName("사용자 채널 목록 조회 성공")
    void 사용자채널목록조회성공() throws Exception {
        // given
        User u1 = userRepository.save(new User("u1", "u1@test.com", "pw123456", null));
        User u2 = userRepository.save(new User("u2", "u2@test.com", "pw123456", null));
        var request = new PrivateChannelCreateRequest(List.of(u1.getId(), u2.getId()));

        String response = mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        // when & then
        mockMvc.perform(get("/api/channels")
                        .param("userId", u1.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("PRIVATE"))
                .andExpect(jsonPath("$[0].type").isNotEmpty());
    }
}