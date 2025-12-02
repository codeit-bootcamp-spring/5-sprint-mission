package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.userdetails.WithMockDiscodeitUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.MOCK_USER_ID;
import static com.sprint.mission.discodeit.support.TestFixtures.MOCK_USER_INSERT_SQL;
import static com.sprint.mission.discodeit.support.TestFixtures.createPrivateChannel;
import static com.sprint.mission.discodeit.support.TestFixtures.createPublicChannel;
import static com.sprint.mission.discodeit.support.TestFixtures.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChannelApiIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User mockUser;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update(MOCK_USER_INSERT_SQL,
            MOCK_USER_ID, "testuser", "test@example.com", "encoded", "USER");
        mockUser = userRepository.findById(MOCK_USER_ID).orElseThrow();
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("공개 채널 생성 - 성공: 공개 채널을 생성하고 데이터베이스에 저장됨")
    void createPublicChannel_Success() throws Exception {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
            "General",
            "General discussion"
        );

        // when
        String responseBody = mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("PUBLIC"))
            .andExpect(jsonPath("$.name").value("General"))
            .andExpect(jsonPath("$.description").value("General discussion"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        // then - 데이터베이스에 실제로 저장되었는지 확인
        String channelId = objectMapper.readTree(responseBody).get("id").asText();
        Optional<Channel> savedChannel = channelRepository.findById(UUID.fromString(channelId));

        assertThat(savedChannel).isPresent();
        assertThat(savedChannel.get().getType()).isEqualTo(ChannelType.PUBLIC);
        assertThat(savedChannel.get().getName()).isEqualTo("General");
        assertThat(savedChannel.get().getDescription()).isEqualTo("General discussion");
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("비공개 채널 생성 - 성공: 2인 비공개 채널을 생성하고 ReadStatus가 생성됨")
    void createPrivateChannel_Success() throws Exception {
        // given - 사용자 2명 생성
        User user1 = createUser("user1");
        User user2 = createUser("user2");
        userRepository.saveAll(List.of(user1, user2));

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
            Set.of(user1.getId(), user2.getId())
        );

        // when
        String responseBody = mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("PRIVATE"))
            .andExpect(jsonPath("$.name").doesNotExist())
            .andReturn()
            .getResponse()
            .getContentAsString();

        // then - 데이터베이스 검증
        String channelId = objectMapper.readTree(responseBody).get("id").asText();
        Optional<Channel> savedChannel = channelRepository.findById(UUID.fromString(channelId));

        assertThat(savedChannel).isPresent();
        assertThat(savedChannel.get().getType()).isEqualTo(ChannelType.PRIVATE);

        // ReadStatus가 2개 생성되었는지 확인
        List<User> participants = readStatusRepository.findUsersByChannel(savedChannel.get());
        assertThat(participants).hasSize(2);
        assertThat(participants).extracting(User::getId)
            .containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("비공개 채널 생성 - 실패: 이미 존재하는 2인 채널 중복 생성 시도")
    void createPrivateChannel_Duplicate_Fails() throws Exception {
        // given - 사용자 2명과 기존 채널 생성
        User user1 = createUser("user1");
        User user2 = createUser("user2");
        userRepository.saveAll(List.of(user1, user2));

        Channel existingChannel = createPrivateChannel();
        channelRepository.save(existingChannel);
        readStatusRepository.saveAll(List.of(
            new ReadStatus(user1, existingChannel, Instant.now(), true),
            new ReadStatus(user2, existingChannel, Instant.now(), true)
        ));

        // 중복 생성 시도
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
            Set.of(user1.getId(), user2.getId())
        );

        // when & then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_PRIVATE_CHANNEL"));
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("채널 생성 - 실패: 유효하지 않은 데이터로 생성 시도")
    void createChannel_InvalidData_Fails() throws Exception {
        // given - PUBLIC 채널인데 name이 빈 문자열
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
            "",
            "Description"
        );

        // when & then
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("사용자의 채널 목록 조회 - 성공: 공개 채널과 참여중인 비공개 채널이 조회됨")
    void findChannelsByUserId_Success() throws Exception {
        // given
        // 채널 생성
        Channel publicChannel = createPublicChannel("Public");
        Channel privateChannel = createPrivateChannel();
        channelRepository.saveAll(List.of(publicChannel, privateChannel));

        // 비공개 채널에 mockUser 추가
        User otherUser = createUser("other");
        userRepository.save(otherUser);
        readStatusRepository.saveAll(List.of(
            new ReadStatus(mockUser, privateChannel, Instant.now(), true),
            new ReadStatus(otherUser, privateChannel, Instant.now(), true)
        ));

        // when & then
        mockMvc.perform(get("/api/channels"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].type").exists())
            .andExpect(jsonPath("$[1].type").exists());
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("채널 목록 조회 - 성공: 사용자의 모든 공개 채널 조회")
    void findAllPublicChannels_Success() throws Exception {
        // given - 공개 채널 2개 생성
        Channel channel1 = createPublicChannel("Channel1");
        Channel channel2 = createPublicChannel("Channel2");
        channelRepository.saveAll(List.of(channel1, channel2));

        // when & then
        mockMvc.perform(get("/api/channels"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("채널 수정 - 성공: 공개 채널 정보를 수정하고 데이터베이스에 반영됨")
    void updateChannel_Success() throws Exception {
        // given - 공개 채널 생성
        Channel channel = createPublicChannel("OldName", "OldDesc");
        channelRepository.save(channel);

        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
            "NewName",
            "NewDesc"
        );

        // when
        mockMvc.perform(patch("/api/channels/{channelId}", channel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("NewName"))
            .andExpect(jsonPath("$.description").value("NewDesc"));

        // then - 데이터베이스에 실제로 수정되었는지 확인
        Optional<Channel> updatedChannel = channelRepository.findById(channel.getId());
        assertThat(updatedChannel).isPresent();
        assertThat(updatedChannel.get().getName()).isEqualTo("NewName");
        assertThat(updatedChannel.get().getDescription()).isEqualTo("NewDesc");
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("채널 수정 - 실패: 존재하지 않는 채널 수정 시도")
    void updateChannel_NotFound_Fails() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("NewName", "NewDesc");

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("채널 삭제 - 성공: 채널과 관련된 ReadStatus가 모두 삭제됨")
    void deleteChannel_Success() throws Exception {
        // given - 채널과 ReadStatus 생성
        User user = createUser("user");
        userRepository.save(user);

        Channel channel = createPublicChannel("ToDelete");
        channelRepository.save(channel);

        ReadStatus readStatus = new ReadStatus(user, channel, Instant.now(), false);
        readStatusRepository.save(readStatus);

        UUID channelId = channel.getId();

        // when
        mockMvc.perform(delete("/api/channels/{channelId}", channelId)
                .with(csrf()))
            .andExpect(status().isNoContent());

        // then - 채널과 ReadStatus가 모두 삭제되었는지 확인
        assertThat(channelRepository.findById(channelId)).isEmpty();
        assertThat(readStatusRepository.findAllByChannelIn(List.of(channel))).isEmpty();
    }

    @Test
    @WithMockUser(roles = "CHANNEL_MANAGER")
    @DisplayName("채널 삭제 - 실패: 존재하지 않는 채널 삭제 시도")
    void deleteChannel_NotFound_Fails() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", nonExistentId)
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }
}
