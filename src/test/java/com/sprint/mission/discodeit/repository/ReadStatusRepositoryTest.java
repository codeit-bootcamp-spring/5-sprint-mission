package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.channel.domain.Channel;
import com.sprint.mission.discodeit.domain.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.domain.channel.domain.ChannelType;
import com.sprint.mission.discodeit.domain.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.domain.readstatus.domain.ReadStatusRepository;
import com.sprint.mission.discodeit.domain.user.domain.User;
import com.sprint.mission.discodeit.domain.user.domain.UserRepository;
import com.sprint.mission.discodeit.global.config.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
@DisplayName("ReadStatusRepository 슬라이스 테스트")
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

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User("testuser1", "test1@example.com", "password1234", null));
        user2 = userRepository.save(new User("testuser2", "test2@example.com", "password1234", null));

        channel1 = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "General channel"));
        Channel channel2 = channelRepository.save(new Channel(ChannelType.PUBLIC, "random", "Random channel"));

        readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now(), true));
        readStatusRepository.save(new ReadStatus(user1, channel2, Instant.now(), false));
    }

    @Nested
    @DisplayName("findAllByUserId")
    class FindAllByUserId {

        @Test
        @DisplayName("사용자 ID로 ReadStatus 목록을 조회한다")
        void findAllByUserId_returnsReadStatuses() {
            // when
            List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(user1.getId());

            // then
            assertThat(readStatuses).hasSize(2);
        }

        @Test
        @DisplayName("해당 사용자의 ReadStatus가 없으면 빈 목록을 반환한다")
        void findAllByUserId_withNoReadStatus_returnsEmptyList() {
            // when
            List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(user2.getId());

            // then
            assertThat(readStatuses).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByChannelIn")
    class FindAllByChannelIn {

        @Test
        @DisplayName("채널 목록에 해당하는 ReadStatus를 조회한다")
        void findAllByChannelIn_returnsReadStatuses() {
            // given
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), true));

            // when
            List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIn(List.of(channel1));

            // then
            assertThat(readStatuses).hasSize(2);
        }
    }

    @Nested
    @DisplayName("findAllByChannelId")
    class FindAllByChannelId {

        @Test
        @DisplayName("채널 ID로 ReadStatus 목록을 조회한다")
        void findAllByChannelId_returnsReadStatuses() {
            // when
            List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel1.getId());

            // then
            assertThat(readStatuses).hasSize(1);
            assertThat(readStatuses.get(0).getUser().getId()).isEqualTo(user1.getId());
        }
    }

    @Nested
    @DisplayName("deleteByChannelId")
    class DeleteByChannelId {

        @Test
        @DisplayName("채널 ID로 ReadStatus를 삭제한다")
        void deleteByChannelId_deletesReadStatuses() {
            // when
            long deletedCount = readStatusRepository.deleteByChannelId(channel1.getId());

            // then
            assertThat(deletedCount).isEqualTo(1);
            assertThat(readStatusRepository.findAllByChannelId(channel1.getId())).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteByUserId")
    class DeleteByUserId {

        @Test
        @DisplayName("사용자 ID로 ReadStatus를 삭제한다")
        void deleteByUserId_deletesReadStatuses() {
            // when
            long deletedCount = readStatusRepository.deleteByUserId(user1.getId());

            // then
            assertThat(deletedCount).isEqualTo(2);
            assertThat(readStatusRepository.findAllByUserId(user1.getId())).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByChannelIdWithNotificationEnabled")
    class FindAllByChannelIdWithNotificationEnabled {

        @Test
        @DisplayName("알림이 활성화된 ReadStatus를 조회한다 (작성자 제외)")
        void findAllByChannelIdWithNotificationEnabled_returnsEnabledReadStatuses() {
            // given
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), true));

            // when
            List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithNotificationEnabled(
                channel1.getId(), user1.getId());

            // then
            assertThat(readStatuses).hasSize(1);
            assertThat(readStatuses.get(0).getUser().getId()).isEqualTo(user2.getId());
        }

        @Test
        @DisplayName("알림이 비활성화된 ReadStatus는 조회되지 않는다")
        void findAllByChannelIdWithNotificationEnabled_excludesDisabledNotifications() {
            // given
            readStatusRepository.save(new ReadStatus(user2, channel1, Instant.now(), false));

            // when
            List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithNotificationEnabled(
                channel1.getId(), user1.getId());

            // then
            assertThat(readStatuses).isEmpty();
        }
    }
}
