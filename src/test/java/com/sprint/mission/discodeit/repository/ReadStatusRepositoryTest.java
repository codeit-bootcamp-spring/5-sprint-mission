package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.readstatus.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class ReadStatusRepositoryTest {

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    private User user1;
    private User user2;
    private Channel channel1;
    private Channel channel2;
    private ReadStatus readStatus1;
    private ReadStatus readStatus2;
    private ReadStatus readStatus3;

    @BeforeEach
    void setUp() {
        // given - 사용자 생성
        user1 = new User("user1", "user1@example.com", "encoded1", null);
        user2 = new User("user2", "user2@example.com", "encoded2", null);
        userRepository.saveAll(List.of(user1, user2));

        // given - 채널 생성
        channel1 = new Channel(ChannelType.PUBLIC, "Channel 1", null);
        channel2 = new Channel(ChannelType.PUBLIC, "Channel 2", null);
        channelRepository.saveAll(List.of(channel1, channel2));

        // given - 읽음 상태 생성
        readStatus1 = new ReadStatus(user1, channel1, Instant.now());
        readStatus2 = new ReadStatus(user1, channel2, Instant.now());
        readStatus3 = new ReadStatus(user2, channel1, Instant.now());
        readStatusRepository.saveAll(List.of(readStatus1, readStatus2, readStatus3));
    }

    @Test
    @DisplayName("findAllByUserId - 사용자의 모든 읽음 상태 조회 성공")
    void findAllByUserId_Success() {
        // when
        List<ReadStatusDto> readStatuses = readStatusRepository.findAllByUserId(user1.getId());

        // then
        assertThat(readStatuses).hasSize(2);
        assertThat(readStatuses).extracting(ReadStatusDto::userId)
            .containsOnly(user1.getId());
        assertThat(readStatuses).extracting(ReadStatusDto::channelId)
            .containsExactlyInAnyOrder(channel1.getId(), channel2.getId());
    }

    @Test
    @DisplayName("findAllByUserId - 읽음 상태가 없는 사용자는 빈 리스트 반환")
    void findAllByUserId_NoReadStatus() {
        // given
        User newUser = new User("newuser", "new@example.com", "encoded", null);
        userRepository.save(newUser);

        // when
        List<ReadStatusDto> readStatuses = readStatusRepository.findAllByUserId(newUser.getId());

        // then
        assertThat(readStatuses).isEmpty();
    }

    @Test
    @DisplayName("findUsersByChannel - 채널의 모든 참여자 조회 성공")
    void findUsersByChannel_Success() {
        // when
        List<User> users = readStatusRepository.findUsersByChannel(channel1);

        // then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername)
            .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    @DisplayName("findUsersByChannel - 참여자가 없는 채널은 빈 리스트 반환")
    void findUsersByChannel_NoUsers() {
        // given
        Channel emptyChannel = new Channel(ChannelType.PUBLIC, "Empty Channel", null);
        channelRepository.save(emptyChannel);

        // when
        List<User> users = readStatusRepository.findUsersByChannel(emptyChannel);

        // then
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("findUsersByChannel - 중복된 사용자는 한 번만 반환 (DISTINCT)")
    void findUsersByChannel_DistinctUsers() {
        // when
        List<User> users = readStatusRepository.findUsersByChannel(channel1);

        // then
        assertThat(users).hasSize(2);
        // user1, user2가 각각 한 번씩만 포함됨
        assertThat(users).extracting(User::getId)
            .containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    @DisplayName("findAllByChannelIn - 여러 채널의 모든 읽음 상태 조회 성공")
    void findAllByChannelIn_Success() {
        // when
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIn(
            List.of(channel1, channel2)
        );

        // then
        assertThat(readStatuses).hasSize(3);
        assertThat(readStatuses).containsExactlyInAnyOrder(readStatus1, readStatus2, readStatus3);
    }

    @Test
    @DisplayName("findAllByChannelIn - 빈 컬렉션으로 조회 시 빈 리스트 반환")
    void findAllByChannelIn_EmptyCollection() {
        // when
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIn(List.of());

        // then
        assertThat(readStatuses).isEmpty();
    }

    @Test
    @DisplayName("findAllByChannelIn - 읽음 상태가 없는 채널은 제외됨")
    void findAllByChannelIn_NoReadStatus() {
        // given
        Channel emptyChannel = new Channel(ChannelType.PUBLIC, "Empty Channel", null);
        channelRepository.save(emptyChannel);

        // when
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIn(
            List.of(channel1, emptyChannel)
        );

        // then
        assertThat(readStatuses).hasSize(2); // channel1의 읽음 상태만 반환
        assertThat(readStatuses).extracting(rs -> rs.getChannel().getId())
            .containsOnly(channel1.getId());
    }

    @Test
    @DisplayName("deleteAllByChannelId - 채널의 모든 읽음 상태 삭제 성공")
    void deleteAllByChannelId_Success() {
        // when
        int deletedCount = readStatusRepository.deleteAllByChannelId(channel1.getId());

        // then
        assertThat(deletedCount).isEqualTo(2);

        // 검증
        List<ReadStatus> remainingStatuses = readStatusRepository.findAll();
        assertThat(remainingStatuses).hasSize(1);
        assertThat(remainingStatuses.get(0).getChannel().getId()).isEqualTo(channel2.getId());
    }

    @Test
    @DisplayName("deleteAllByChannelId - 읽음 상태가 없는 채널 삭제 시 0 반환")
    void deleteAllByChannelId_NoReadStatus() {
        // given
        Channel emptyChannel = new Channel(ChannelType.PUBLIC, "Empty Channel", null);
        channelRepository.save(emptyChannel);

        // when
        int deletedCount = readStatusRepository.deleteAllByChannelId(emptyChannel.getId());

        // then
        assertThat(deletedCount).isZero();
    }

    @Test
    @DisplayName("deleteAllByUser - 사용자의 모든 읽음 상태 삭제 성공")
    void deleteAllByUser_Success() {
        // when
        int deletedCount = readStatusRepository.deleteAllByUser(user1);

        // then
        assertThat(deletedCount).isEqualTo(2);

        // 검증
        List<ReadStatus> remainingStatuses = readStatusRepository.findAll();
        assertThat(remainingStatuses).hasSize(1);
        assertThat(remainingStatuses.get(0).getUser().getId()).isEqualTo(user2.getId());
    }

    @Test
    @DisplayName("deleteAllByUser - 읽음 상태가 없는 사용자 삭제 시 0 반환")
    void deleteAllByUser_NoReadStatus() {
        // given
        User newUser = new User("newuser", "new@example.com", "encoded", null);
        userRepository.save(newUser);

        // when
        int deletedCount = readStatusRepository.deleteAllByUser(newUser);

        // then
        assertThat(deletedCount).isZero();
    }

    @Test
    @DisplayName("save - 읽음 상태 생성 시 JPA Audit 필드가 자동 설정됨")
    void save_AuditFieldsAutoSet() {
        // given
        User newUser = new User("newuser", "new@example.com", "encoded", null);
        userRepository.save(newUser);

        ReadStatus newReadStatus = new ReadStatus(newUser, channel1, Instant.now());

        // when
        ReadStatus savedReadStatus = readStatusRepository.save(newReadStatus);

        // then
        assertThat(savedReadStatus.getId()).isNotNull();
        assertThat(savedReadStatus.getCreatedAt()).isNotNull();
    }
}
