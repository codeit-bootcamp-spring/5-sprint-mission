package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.common.config.JpaConfig;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.ChannelType;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.repository.ChannelRepository;
import com.sprint.mission.discodeit.domain.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
@DisplayName("ChannelRepository 슬라이스 테스트")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    private Channel publicChannel;
    private Channel privateChannel;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User("testuser1", "test1@example.com", "password123456789012345678901234567890123456789012345678", null));
        user2 = userRepository.save(new User("testuser2", "test2@example.com", "password123456789012345678901234567890123456789012345678", null));

        publicChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "General channel"));
        privateChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));
    }

    @Nested
    @DisplayName("findAllByTypeOrIdIn")
    class FindAllByTypeOrIdIn {

        @Test
        @DisplayName("PUBLIC 타입이거나 ID 목록에 해당하는 채널을 조회한다")
        void findAllByTypeOrIdIn_returnsMatchingChannels() {
            // given
            List<UUID> privateChannelIds = List.of(privateChannel.getId());

            // when
            List<Channel> channels = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, privateChannelIds);

            // then
            assertThat(channels).hasSize(2);
            assertThat(channels).extracting(Channel::getId)
                .containsExactlyInAnyOrder(publicChannel.getId(), privateChannel.getId());
        }

        @Test
        @DisplayName("PUBLIC 타입만 조회한다")
        void findAllByTypeOrIdIn_withEmptyIds_returnsOnlyPublicChannels() {
            // when
            List<Channel> channels = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, Collections.emptyList());

            // then
            assertThat(channels).hasSize(1);
            assertThat(channels.get(0).getType()).isEqualTo(ChannelType.PUBLIC);
        }
    }

    @Nested
    @DisplayName("existsBetweenUsers")
    class ExistsBetweenUsers {

        @Test
        @DisplayName("두 사용자 간 PRIVATE 채널이 존재하면 true를 반환한다")
        void existsBetweenUsers_withExistingPrivateChannel_returnsTrue() {
            // given
            readStatusRepository.save(new ReadStatus(user1, privateChannel, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user2, privateChannel, Instant.now(), true));

            // when
            boolean exists = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("두 사용자 간 PRIVATE 채널이 존재하지 않으면 false를 반환한다")
        void existsBetweenUsers_withNoPrivateChannel_returnsFalse() {
            // when
            boolean exists = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("세 명 이상이 참여한 PRIVATE 채널은 false를 반환한다")
        void existsBetweenUsers_withMoreThanTwoUsers_returnsFalse() {
            // given
            User user3 = userRepository.save(new User("testuser3", "test3@example.com", "password123456789012345678901234567890123456789012345678", null));
            readStatusRepository.save(new ReadStatus(user1, privateChannel, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user2, privateChannel, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user3, privateChannel, Instant.now(), true));

            // when
            boolean exists = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());

            // then
            assertThat(exists).isFalse();
        }
    }
}
