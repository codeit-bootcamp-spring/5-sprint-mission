package com.sprint.mission.discodeit.repository;

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

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@org.springframework.test.context.ActiveProfiles("test")
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
    private Channel publicChannel1;
    private Channel publicChannel2;
    private Channel privateChannel1;
    private Channel privateChannel2;

    @BeforeEach
    void setUp() {
        // given - 사용자 생성
        user1 = new User("user1", "user1@example.com", "encoded1", null);
        user2 = new User("user2", "user2@example.com", "encoded2", null);
        user3 = new User("user3", "user3@example.com", "encoded3", null);
        userRepository.saveAll(List.of(user1, user2, user3));

        // given - 공개 채널 생성
        publicChannel1 = new Channel(ChannelType.PUBLIC, "General", "General discussion");
        publicChannel2 = new Channel(ChannelType.PUBLIC, "Random", "Random chat");
        channelRepository.saveAll(List.of(publicChannel1, publicChannel2));

        // given - 비공개 채널 생성 (user1 - user2)
        privateChannel1 = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(privateChannel1);
        readStatusRepository.saveAll(List.of(
            new ReadStatus(user1, privateChannel1, Instant.now()),
            new ReadStatus(user2, privateChannel1, Instant.now())
        ));

        // given - 비공개 채널 생성 (user1 - user3)
        privateChannel2 = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(privateChannel2);
        readStatusRepository.saveAll(List.of(
            new ReadStatus(user1, privateChannel2, Instant.now()),
            new ReadStatus(user3, privateChannel2, Instant.now())
        ));
    }

    @Test
    @DisplayName("findAllByUserId - 사용자의 모든 채널 조회 성공 (공개 채널 + 참여중인 비공개 채널)")
    void findAllByUserId_Success() {
        // when
        List<Channel> channels = channelRepository.findAllByUserId(user1.getId());

        // then
        // user1은 공개 채널 2개 + 비공개 채널 2개 = 총 4개
        assertThat(channels).hasSize(4);
        assertThat(channels).contains(publicChannel1, publicChannel2, privateChannel1, privateChannel2);
    }

    @Test
    @DisplayName("findAllByUserId - 사용자의 채널 조회 시 PRIVATE 채널이 먼저 정렬됨")
    void findAllByUserId_OrderedByTypePrivateFirst() {
        // when
        List<Channel> channels = channelRepository.findAllByUserId(user1.getId());

        // then
        assertThat(channels).hasSize(4);
        // 처음 2개는 PRIVATE 타입이어야 함
        assertThat(channels.get(0).getType()).isEqualTo(ChannelType.PRIVATE);
        assertThat(channels.get(1).getType()).isEqualTo(ChannelType.PRIVATE);
        // 나머지는 PUBLIC 타입
        assertThat(channels.get(2).getType()).isEqualTo(ChannelType.PUBLIC);
        assertThat(channels.get(3).getType()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("findAllByUserId - 비공개 채널에 참여하지 않은 사용자는 해당 채널을 볼 수 없음")
    void findAllByUserId_DoesNotIncludeNonParticipatingPrivateChannels() {
        // given - user2는 privateChannel2에 참여하지 않음

        // when
        List<Channel> channels = channelRepository.findAllByUserId(user2.getId());

        // then
        // user2는 공개 채널 2개 + privateChannel1만 = 총 3개
        assertThat(channels).hasSize(3);
        assertThat(channels).contains(publicChannel1, publicChannel2, privateChannel1);
        assertThat(channels).doesNotContain(privateChannel2);
    }

    @Test
    @DisplayName("findAllByUserId - 어떤 채널에도 참여하지 않은 사용자는 공개 채널만 조회")
    void findAllByUserId_OnlyPublicChannelsForNonParticipant() {
        // given - 새로운 사용자 생성 (어떤 비공개 채널에도 참여하지 않음)
        User newUser = new User("newuser", "new@example.com", "encoded", null);
        userRepository.save(newUser);

        // when
        List<Channel> channels = channelRepository.findAllByUserId(newUser.getId());

        // then
        // 공개 채널 2개만
        assertThat(channels).hasSize(2);
        assertThat(channels).containsExactlyInAnyOrder(publicChannel1, publicChannel2);
    }

    @Test
    @DisplayName("existsBetweenUsers - 두 사용자 간 2인 비공개 채널이 존재하면 true 반환")
    void existsBetweenUsers_Exists_ReturnsTrue() {
        // when
        boolean exists = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsBetweenUsers - 두 사용자 간 2인 비공개 채널이 존재하지 않으면 false 반환")
    void existsBetweenUsers_NotExists_ReturnsFalse() {
        // when
        boolean exists = channelRepository.existsBetweenUsers(user2.getId(), user3.getId());

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsBetweenUsers - 3인 이상 채널은 중복 체크에서 제외됨")
    void existsBetweenUsers_ThreePersonChannel_ReturnsFalse() {
        // given - 3인 비공개 채널 생성
        Channel threePersonChannel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(threePersonChannel);
        readStatusRepository.saveAll(List.of(
            new ReadStatus(user1, threePersonChannel, Instant.now()),
            new ReadStatus(user2, threePersonChannel, Instant.now()),
            new ReadStatus(user3, threePersonChannel, Instant.now())
        ));

        // when - user1과 user2는 이미 privateChannel1에 함께 있지만, 3인 채널은 카운트 안됨
        boolean exists = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());

        // then - privateChannel1 (2인)이 존재하므로 true
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsBetweenUsers - 순서를 바꿔도 같은 결과 반환")
    void existsBetweenUsers_OrderIndependent() {
        // when
        boolean exists1 = channelRepository.existsBetweenUsers(user1.getId(), user2.getId());
        boolean exists2 = channelRepository.existsBetweenUsers(user2.getId(), user1.getId());

        // then
        assertThat(exists1).isEqualTo(exists2);
        assertThat(exists1).isTrue();
    }

    @Test
    @DisplayName("save - 채널 생성 시 JPA Audit 필드가 자동 설정됨")
    void save_AuditFieldsAutoSet() {
        // given
        Channel newChannel = new Channel(ChannelType.PUBLIC, "New Channel", "Description");

        // when
        Channel savedChannel = channelRepository.save(newChannel);

        // then
        assertThat(savedChannel.getId()).isNotNull();
        assertThat(savedChannel.getCreatedAt()).isNotNull();
        assertThat(savedChannel.getUpdatedAt()).isNotNull();
    }
}
