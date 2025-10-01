package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MessageIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Test
    @DisplayName("메시지 생성 성공")
    void 메시지생성성공() throws Exception {
        // given
        User user = userRepository.save(new User("mike", "mike@test.com", "password123", null));
        userStatusRepository.save(new UserStatus(user, Instant.now()));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "테스트 채널"));

        var request = new MessageCreateRequest("Hello world!", channel.getId(), user.getId());

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(new MockMultipartFile("messageCreateRequest",
                                "message.json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsBytes(request)))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello world!"))
                .andExpect(jsonPath("$.channelId").value(channel.getId().toString()))
                .andExpect(jsonPath("$.author.id").value(user.getId().toString()));
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void 메시지수정성공() throws Exception {
        // given: 유저, 채널, 메시지 생성 먼저
        User user = userRepository.save(new User("mike", "mike@test.com", "password123", null));
        userStatusRepository.save(new UserStatus(user, Instant.now()));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "테스트 채널"));

        var createRequest = new MessageCreateRequest("원본 내용", channel.getId(), user.getId());
        String response = mockMvc.perform(multipart("/api/messages")
                        .file(new MockMultipartFile("messageCreateRequest", "msg.json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsBytes(createRequest)))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andReturn().getResponse().getContentAsString();

        var createdMessage = objectMapper.readTree(response);
        UUID messageId = UUID.fromString(createdMessage.get("id").asText());

        // when & then
        var updateRequest = new MessageUpdateRequest("수정된 내용");

        mockMvc.perform(patch("/api/messages/{id}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 내용"));
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void 메시지삭제성공() throws Exception {
        // given
        User user = userRepository.save(new User("mike", "mike@test.com", "password123", null));
        userStatusRepository.save(new UserStatus(user, Instant.now()));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "테스트 채널"));

        var createRequest = new MessageCreateRequest("삭제할 메시지", channel.getId(), user.getId());
        String response = mockMvc.perform(multipart("/api/messages")
                        .file(new MockMultipartFile("messageCreateRequest", "msg.json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsBytes(createRequest)))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andReturn().getResponse().getContentAsString();

        var createdMessage = objectMapper.readTree(response);
        UUID messageId = UUID.fromString(createdMessage.get("id").asText());

        // when & then
        mockMvc.perform(delete("/api/messages/{id}", messageId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("채널별 메시지 목록 조회 성공")
    void 메시지목록조회성공() throws Exception {
        // given
        User user = userRepository.save(new User("mike", "mike@test.com", "password123", null));
        userStatusRepository.save(new UserStatus(user, Instant.now()));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "테스트 채널"));

        var createRequest = new MessageCreateRequest("첫번째 메시지", channel.getId(), user.getId());
        mockMvc.perform(multipart("/api/messages")
                        .file(new MockMultipartFile("messageCreateRequest", "msg.json",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsBytes(createRequest)))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        // when & then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("첫번째 메시지"));
    }
}