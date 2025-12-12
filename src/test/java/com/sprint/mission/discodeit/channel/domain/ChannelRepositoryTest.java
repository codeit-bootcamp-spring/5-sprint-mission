package com.sprint.mission.discodeit.channel.domain;

import com.sprint.mission.discodeit.global.config.JpaConfig;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
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
@Import(JpaConfig.class)
@DisplayName("ChannelRepository 슬라이스 테스트")
@ActiveProfiles("test")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User("testuser1", "test1@example.com", "password1234", null));
        user2 = userRepository.save(new User("testuser2", "test2@example.com", "password1234", null));
        user3 = userRepository.save(new User("testuser3", "test3@example.com", "password1234", null));
    }

    @Nested
    @DisplayName("findAllByType")
    class FindAllByType {

        @Test
        @DisplayName("PUBLIC 타입 채널 목록 조회 성공")
        void findAllByType_withPublicType_returnsPublicChannels() {
            // given
            Channel publicChannel1 = channelRepository.save(
                new Channel(ChannelType.PUBLIC, "general", "General channel"));
            Channel publicChannel2 = channelRepository.save(
                new Channel(ChannelType.PUBLIC, "random", "Random channel"));
            channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));

            // when
            List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);

            // then
            assertThat(publicChannels).hasSize(2);
            assertThat(publicChannels).extracting(Channel::getId)
                .containsExactlyInAnyOrder(publicChannel1.getId(), publicChannel2.getId());
        }

        @Test
        @DisplayName("PRIVATE 타입 채널 목록 조회 성공")
        void findAllByType_withPrivateType_returnsPrivateChannels() {
            // given
            channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "General channel"));
            Channel privateChannel = channelRepository.save(
                new Channel(ChannelType.PRIVATE, null, null));

            // when
            List<Channel> privateChannels = channelRepository.findAllByType(ChannelType.PRIVATE);

            // then
            assertThat(privateChannels).hasSize(1);
            assertThat(privateChannels.get(0).getId()).isEqualTo(privateChannel.getId());
        }

        @Test
        @DisplayName("해당 타입 채널 없으면 빈 목록 반환")
        void findAllByType_withNoMatchingType_returnsEmptyList() {
            // given
            channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "General channel"));

            // when
            List<Channel> privateChannels = channelRepository.findAllByType(ChannelType.PRIVATE);

            // then
            assertThat(privateChannels).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsBetweenUsers")
    class ExistsBetweenUsers {

        @Test
        @DisplayName("두 사용자 간 PRIVATE 채널 존재하면 true 반환")
        void existsBetweenUsers_withExistingChannel_returnsTrue() {
            // given
            Channel privateChannel = channelRepository.save(
                new Channel(ChannelType.PRIVATE, null, null));

            readStatusRepository.save(new ReadStatus(user1, privateChannel, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user2, privateChannel, Instant.now(), true));

            // when
            boolean exists = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("두 사용자 간 PRIVATE 채널 없으면 false 반환")
        void existsBetweenUsers_withNoChannel_returnsFalse() {
            // when
            boolean exists = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("한 사용자만 참여한 채널은 false 반환")
        void existsBetweenUsers_withOnlyOneParticipant_returnsFalse() {
            // given
            Channel privateChannel = channelRepository.save(
                new Channel(ChannelType.PRIVATE, null, null));

            readStatusRepository.save(new ReadStatus(user1, privateChannel, Instant.now(), true));

            // when
            boolean exists = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("3명 이상 참여 채널은 false 반환")
        void existsBetweenUsers_withMoreThanTwoParticipants_returnsFalse() {
            // given
            Channel privateChannel = channelRepository.save(
                new Channel(ChannelType.PRIVATE, null, null));

            readStatusRepository.save(new ReadStatus(user1, privateChannel, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user2, privateChannel, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user3, privateChannel, Instant.now(), true));

            // when
            boolean exists = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("PUBLIC 채널은 두 사용자 참여해도 false 반환")
        void existsBetweenUsers_withPublicChannel_returnsFalse() {
            // given
            Channel publicChannel = channelRepository.save(
                new Channel(ChannelType.PUBLIC, "general", "General channel"));

            readStatusRepository.save(new ReadStatus(user1, publicChannel, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user2, publicChannel, Instant.now(), true));

            // when
            boolean exists = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("사용자 ID 순서 무관하게 동일 결과 반환")
        void existsBetweenUsers_withReversedOrder_returnsSameResult() {
            // given
            Channel privateChannel = channelRepository.save(
                new Channel(ChannelType.PRIVATE, null, null));

            readStatusRepository.save(new ReadStatus(user1, privateChannel, Instant.now(), true));
            readStatusRepository.save(new ReadStatus(user2, privateChannel, Instant.now(), true));

            // when
            boolean existsNormal = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());
            boolean existsReversed = channelRepository.existsBetweenUsers(user2.getId(), user1.getId());

            // then
            assertThat(existsNormal).isTrue();
            assertThat(existsReversed).isTrue();
        }
    }
}
