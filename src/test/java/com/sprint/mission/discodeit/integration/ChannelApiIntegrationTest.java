package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ChannelApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelService channelService;

    private User createTestUser(String username, String email) {
        User user = new User(username, "password123", username, email, null);
        return userRepository.save(user);
    }

    private ChannelResponse createPublicChannel(String name, String description) throws Exception {
        PublicChannelCreateRequest request = PublicChannelCreateRequest
                .builder()
                .name(name)
                .description(description)
                .build();

        String response = mockMvc
                .perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        return objectMapper.readValue(response, ChannelResponse.class);
    }

    private ChannelResponse createPrivateChannel(List<UUID> participantIds) throws Exception {
        PrivateChannelCreateRequest request = PrivateChannelCreateRequest
                .builder()
                .participantIds(participantIds)
                .build();

        String response = mockMvc
                .perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        return objectMapper.readValue(response, ChannelResponse.class);
    }

    @Test
    @DisplayName("1.1 공개 채널 생성 후 조회 성공")
    void createPublicChannelAndGet_success() throws Exception {
        // given
        // when
        ChannelResponse channelResponse = createPublicChannel("public", "public channel");

        // then
        mockMvc.perform(get("/api/channels/{channelId}", channelResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(channelResponse.getId().toString()))
                .andExpect(jsonPath("$.name").value(channelResponse.getName()))
                .andExpect(jsonPath("$.type").value(channelResponse.getType().toString()))
                .andExpect(jsonPath("$.description").value(channelResponse.getDescription()));

        assertThat(channelRepository.findById(channelResponse.getId())).isPresent();
    }

    @Test
    @DisplayName("1.2 비공개 채널 생성 후 조회 성공")
    void createPrivateChannelAndGet_success() throws Exception {
        // given
        User user1 = createTestUser("user1", "user1@example.com");
        User user2 = createTestUser("user2", "user2@example.com");
        List<UUID> participantIds = List.of(user1.getId(), user2.getId());

        // when
        ChannelResponse channelResponse = createPrivateChannel(participantIds);

        // then
        mockMvc.perform(get("/api/channels/{channelId}", channelResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(channelResponse.getId().toString()))
                .andExpect(jsonPath("$.name").value(channelResponse.getName()))
                .andExpect(jsonPath("$.type").value(channelResponse.getType().toString()))
                .andExpect(jsonPath("$.participants.length()").value(2));

        assertThat(channelRepository.findById(channelResponse.getId())).isPresent();
    }

    @Test
    @DisplayName("1.3 중복 채널명으로 공개 채널 생성 실패")
    void createDuplicatePublicChannel_failure() throws Exception {
        // given
        createPublicChannel("public", "public channel");

        PublicChannelCreateRequest request = PublicChannelCreateRequest
                .builder()
                .name("public")
                .description("another public channel")
                .build();

        // when
        // then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_CHANNEL_NAME.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_CHANNEL_NAME.getMessage()));

        assertThat(channelRepository.findByName("public")).isPresent();
        assertThat(channelRepository.findAll().stream()
                .filter(ch -> "public".equals(ch.getName()))
                .count()).isEqualTo(1);
    }

    @Test
    @DisplayName("1.4 비공개 채널 생성 실패 - 참여자 1명")
    void createPrivateChannelWithOneParticipant_failure() throws Exception {
        // given
        User user1 = createTestUser("user1", "user1@example.com");
        List<UUID> participantIds = List.of(user1.getId());

        PrivateChannelCreateRequest request = PrivateChannelCreateRequest.builder()
                .participantIds(participantIds)
                .build();

        // when
        // then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                // 서비스 레이어에도 예외처리가 있지만 ErrorCode.INVALID_PARTICIPANT
                // 컨트롤러 @Valid 검증에 걸려서 handleValidationExceptions에 먼저 에러 처리 되는 시나리오
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("요청 데이터 유효성 검사에 실패했습니다"))
                .andExpect(jsonPath("$.details.participantIds").value("PRIVATE 채널은 최소 2명 이상이어야 합니다"));

        assertThat(channelRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("2.1 공개 채널 생성 후 수정 성공")
    void createPublicChannelAndUpdate_success() throws Exception {
        // given
        ChannelResponse created = createPublicChannel("test", "원본 설명");

        ChannelUpdateRequest updateRequest = ChannelUpdateRequest.builder()
                .newName("updated")
                .newDescription("수정된 설명")
                .build();

        // when
        // then
        mockMvc.perform(patch("/api/channels/{channelId}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated"))
                .andExpect(jsonPath("$.description").value("수정된 설명"));

        mockMvc.perform(get("/api/channels/{channelId}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated"))
                .andExpect(jsonPath("$.description").value("수정된 설명"));

        Channel updatedChannel = channelRepository.findById(created.getId()).orElseThrow();
        assertThat(updatedChannel.getName()).isEqualTo("updated");
        assertThat(updatedChannel.getDescription()).isEqualTo("수정된 설명");
    }

    @Test
    @DisplayName("2.2 비공개 채널 수정 시도 실패")
    void updatePrivateChannel_failure() throws Exception {
        // given
        User user1 = createTestUser("user1", "user1@example.com");
        User user2 = createTestUser("user2", "user2@example.com");
        ChannelResponse created = createPrivateChannel(List.of(user1.getId(), user2.getId()));

        ChannelUpdateRequest updateRequest = ChannelUpdateRequest.builder()
                .newName("test")
                .build();

        // then
        mockMvc.perform(patch("/api/channels/{channelId}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED.name()));
    }

    @Test
    @DisplayName("2.3 존재하지 않는 채널 수정 실패")
    void updateNonExistentChannel_failure() throws Exception {
        // given
        UUID nonExistentChannelId = UUID.randomUUID();
        ChannelUpdateRequest updateRequest = ChannelUpdateRequest.builder()
                .newName("test")
                .build();

        // when
        // then
        mockMvc.perform(patch("/api/channels/{channelId}", nonExistentChannelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.CHANNEL_NOT_FOUND.name()));
    }

    @Test
    @DisplayName("3.1 공개 채널 생성 후 삭제 성공")
    void createPublicChannelAndDelete_success() throws Exception {
        // given
        ChannelResponse created = createPublicChannel("to-delete", "삭제될 채널");

        // when - 채널 삭제
        mockMvc.perform(delete("/api/channels/{channelId}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channelId").value(created.getId().toString()))
                .andExpect(jsonPath("$.success").value(true));

        // then
        mockMvc.perform(get("/api/channels/{channelId}", created.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.CHANNEL_NOT_FOUND.name()));

        assertThat(channelRepository.findById(created.getId())).isEmpty();
    }

    @Test
    @DisplayName("3.2 비공개 채널 생성 후 삭제 성공")
    void createPrivateChannelAndDelete_success() throws Exception {
        // given
        User user1 = createTestUser("user1", "user1@example.com");
        User user2 = createTestUser("user2", "user2@example.com");
        ChannelResponse created = createPrivateChannel(List.of(user1.getId(), user2.getId()));

        // when
        mockMvc.perform(delete("/api/channels/{channelId}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // then
        mockMvc.perform(get("/api/channels/{channelId}", created.getId()))
                .andExpect(status().isNotFound());

        assertThat(channelRepository.findById(created.getId())).isEmpty();
    }

    @Test
    @DisplayName("3.3 존재하지 않는 채널 삭제 실패")
    void deleteNonExistentChannel_failure() throws Exception {
        // given
        UUID nonExistentChannelId = UUID.randomUUID();

        // when
        // then
        mockMvc.perform(delete("/api/channels/{channelId}", nonExistentChannelId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.CHANNEL_NOT_FOUND.name()));
    }

    @Test
    @DisplayName("4.1 다양한 채널들 생성 후 사용자별 채널 목록 조회")
    void createMultipleChannelsAndGetUserChannels_comprehensive() throws Exception {
        // given
        User user1 = createTestUser("user1", "user1@example.com");
        User user2 = createTestUser("user2", "user2@example.com");
        User user3 = createTestUser("user3", "user3@example.com");

        ChannelResponse publicChannel1 = createPublicChannel("general", "일반 채널");
        ChannelResponse publicChannel2 = createPublicChannel("random", "잡담 채널");

        ChannelResponse privateChannel1 = createPrivateChannel(List.of(user1.getId(), user2.getId()));

        ChannelResponse privateChannel2 = createPrivateChannel(List.of(user2.getId(), user3.getId()));

        // when
        // then
        mockMvc.perform(get("/api/channels")
                        .param("userId", user1.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        List<ChannelResponse> user1Channels = channelService.findChannelsByUserId(user1.getId());
        assertThat(user1Channels).hasSize(3);

        List<UUID> user1ChannelIds = user1Channels.stream().map(ChannelResponse::getId).toList();
        assertThat(user1ChannelIds).contains(publicChannel1.getId(), publicChannel2.getId(), privateChannel1.getId());
        assertThat(user1ChannelIds).doesNotContain(privateChannel2.getId());

        mockMvc.perform(get("/api/channels")
                        .param("userId", user2.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));

        List<ChannelResponse> user2Channels = channelService.findChannelsByUserId(user2.getId());
        assertThat(user2Channels).hasSize(4);

        List<UUID> user2ChannelIds = user2Channels.stream().map(ChannelResponse::getId).toList();
        assertThat(user2ChannelIds).contains(
                publicChannel1.getId(), publicChannel2.getId(),
                privateChannel1.getId(), privateChannel2.getId()
        );

        mockMvc.perform(get("/api/channels")
                        .param("userId", user3.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        List<ChannelResponse> user3Channels = channelService.findChannelsByUserId(user3.getId());
        assertThat(user3Channels).hasSize(3);

        List<UUID> user3ChannelIds = user3Channels.stream().map(ChannelResponse::getId).toList();
        assertThat(user3ChannelIds).contains(publicChannel1.getId(), publicChannel2.getId(), privateChannel2.getId());
        assertThat(user3ChannelIds).doesNotContain(privateChannel1.getId());
    }

    @Test
    @DisplayName("4.2 존재하지 않는 사용자의 채널 목록 조회")
    void getUserChannelsForNonExistentUser_success() throws Exception {
        // given
        UUID nonExistentUserId = UUID.randomUUID();

        // when
        // then
        mockMvc.perform(get("/api/channels")
                        .param("userId", nonExistentUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
