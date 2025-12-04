package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.security.userdetails.WithMockDiscodeitUser;
import com.sprint.mission.discodeit.domain.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.repository.ChannelRepository;
import com.sprint.mission.discodeit.domain.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.MOCK_USER_ID;
import static com.sprint.mission.discodeit.support.TestFixtures.MOCK_USER_INSERT_SQL;
import static com.sprint.mission.discodeit.support.TestFixtures.createPublicChannel;
import static com.sprint.mission.discodeit.support.TestFixtures.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReadStatusApiIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

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
    @WithMockDiscodeitUser
    @DisplayName("읽음 상태 조회 - 성공: 사용자의 모든 읽음 상태를 조회")
    void findAllByUserId_Success() throws Exception {
        // given - 채널, 읽음 상태 생성
        Channel channel1 = createPublicChannel("Channel1");
        Channel channel2 = createPublicChannel("Channel2");
        channelRepository.saveAll(List.of(channel1, channel2));

        Instant lastReadAt1 = Instant.now().minusSeconds(3600);
        Instant lastReadAt2 = Instant.now().minusSeconds(1800);

        ReadStatus readStatus1 = new ReadStatus(mockUser, channel1, lastReadAt1, false);
        ReadStatus readStatus2 = new ReadStatus(mockUser, channel2, lastReadAt2, false);
        readStatusRepository.saveAll(List.of(readStatus1, readStatus2));

        // when & then
        mockMvc.perform(get("/api/readStatuses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userId").value(mockUser.getId().toString()))
            .andExpect(jsonPath("$[1].userId").value(mockUser.getId().toString()));
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("읽음 상태 조회 - 성공: 읽음 상태가 없으면 빈 배열 반환")
    void findAllByUserId_EmptyList() throws Exception {
        // when & then
        mockMvc.perform(get("/api/readStatuses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("읽음 상태 수정 - 성공: lastReadAt을 업데이트하고 데이터베이스에 반영됨")
    void updateReadStatus_Success() throws Exception {
        // given - 읽음 상태 생성
        Channel channel = createPublicChannel("Channel");
        channelRepository.save(channel);

        Instant oldLastReadAt = Instant.now().minusSeconds(7200);
        ReadStatus readStatus = new ReadStatus(mockUser, channel, oldLastReadAt, false);
        readStatusRepository.save(readStatus);

        // 새로운 읽음 시간
        Instant newLastReadAt = Instant.now();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt, false);

        // when
        mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lastReadAt").exists());

        // then - 데이터베이스에 실제로 수정되었는지 확인
        Optional<ReadStatus> updated = readStatusRepository.findById(readStatus.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getLastReadAt()).isAfter(oldLastReadAt);
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("읽음 상태 수정 - 실패: 존재하지 않는 읽음 상태 수정 시도")
    void updateReadStatus_NotFound_Fails() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now(), false);

        // when & then
        mockMvc.perform(patch("/api/readStatuses/{readStatusId}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("READ_STATUS_NOT_FOUND"));
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("읽음 상태 업데이트 - 성공: 여러 사용자가 같은 채널의 읽음 상태를 독립적으로 관리")
    void updateReadStatus_MultipleUsers_Success() throws Exception {
        // given - 다른 사용자 생성
        User user2 = createUser("user2");
        userRepository.save(user2);

        Channel channel = createPublicChannel("SharedChannel");
        channelRepository.save(channel);

        Instant time1 = Instant.now().minusSeconds(1000);
        Instant time2 = Instant.now().minusSeconds(2000);

        ReadStatus readStatus1 = new ReadStatus(mockUser, channel, time1, false);
        ReadStatus readStatus2 = new ReadStatus(user2, channel, time2, false);
        readStatusRepository.saveAll(List.of(readStatus1, readStatus2));

        // mockUser의 읽음 시간 업데이트
        Instant newTime1 = Instant.now();
        ReadStatusUpdateRequest request1 = new ReadStatusUpdateRequest(newTime1, false);

        // when
        mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatus1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
                .with(csrf()))
            .andExpect(status().isOk());

        // then - mockUser의 읽음 상태만 변경되고 user2는 영향받지 않음
        Optional<ReadStatus> updated1 = readStatusRepository.findById(readStatus1.getId());
        Optional<ReadStatus> updated2 = readStatusRepository.findById(readStatus2.getId());

        assertThat(updated1).isPresent();
        assertThat(updated1.get().getLastReadAt()).isAfter(time1);

        assertThat(updated2).isPresent();
        assertThat(updated2.get().getLastReadAt()).isEqualTo(time2);
    }

    @Test
    @WithMockDiscodeitUser
    @DisplayName("읽음 상태 조회 - 성공: 채널별로 읽음 상태가 올바르게 구분됨")
    void findAllByUserId_MultipleChannels_Success() throws Exception {
        // given - 여러 채널에 참여
        Channel channel1 = createPublicChannel("Channel1");
        Channel channel2 = createPublicChannel("Channel2");
        Channel channel3 = createPublicChannel("Channel3");
        channelRepository.saveAll(List.of(channel1, channel2, channel3));

        ReadStatus readStatus1 = new ReadStatus(mockUser, channel1, Instant.now(), false);
        ReadStatus readStatus2 = new ReadStatus(mockUser, channel2, Instant.now(), false);
        ReadStatus readStatus3 = new ReadStatus(mockUser, channel3, Instant.now(), false);
        readStatusRepository.saveAll(List.of(readStatus1, readStatus2, readStatus3));

        // when & then
        mockMvc.perform(get("/api/readStatuses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].channelId").exists())
            .andExpect(jsonPath("$[1].channelId").exists())
            .andExpect(jsonPath("$[2].channelId").exists());
    }
}
