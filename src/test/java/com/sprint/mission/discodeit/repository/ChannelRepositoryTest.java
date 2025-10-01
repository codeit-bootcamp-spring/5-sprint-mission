package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TestEntityManager em;

    private Channel createChannel(ChannelType channelType, String name) {
        Channel channel = new Channel(channelType, name, "test");
        return channelRepository.save(channel);
    }

    @Test
    @DisplayName("ID 목록 없이 조회 했을 때 public 리스트 반환")
    void findAllByTypeOrIdIn_withNullIds_returnsOnlyPublic() {
        // given
        Channel publicChannel = createChannel(ChannelType.PUBLIC, "공개 채널");
        Channel privateChannel1 = createChannel(ChannelType.PRIVATE, "비공개 채널1");
        Channel privateChannel2 = createChannel(ChannelType.PRIVATE, "비공개 채널2");
        em.flush();
        em.clear();

        // when
        List<Channel> channelList = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, null);

        // then
        assertThat(channelList).hasSize(1);
    }

    @Test
    @DisplayName("ID 목록과 조회 했을 때 public + 해당되는 private 리스트 반환")
    void findAllByTypeOrIdIn_withPrivateIds_returnsWithPrivate() {
        // given
        Channel publicChannel = createChannel(ChannelType.PUBLIC, "공개 채널");
        Channel privateChannel1 = createChannel(ChannelType.PRIVATE, "비공개 채널1");
        Channel privateChannel2 = createChannel(ChannelType.PRIVATE, "비공개 채널2");
        em.flush();
        em.clear();

        // when
        List<Channel> channelList = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC,
            List.of(privateChannel1.getId(), privateChannel2.getId()));

        // then
        assertThat(channelList).hasSize(3);
    }
}