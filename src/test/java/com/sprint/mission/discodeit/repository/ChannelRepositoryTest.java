package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class ChannelRepositoryTest {

    @Autowired
    ChannelRepository channelRepository;

    @Test
    @DisplayName("PUBLIC 타입 채널 조회 성공")
    void findAllByTypeSuccess() {
        Channel pub1 = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "test1"));
        Channel pub2 = channelRepository.save(new Channel(ChannelType.PUBLIC, "random", "test2"));
        Channel priv = channelRepository.save(new Channel(ChannelType.PRIVATE, "secret", "test3"));

        List<Channel> results = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of());

        assertThat(results).contains(pub1, pub2);
        assertThat(results).doesNotContain(priv);
    }

    @Test
    @DisplayName("ID 리스트로 특정 채널 조회 성공")
    void findAllByIdInSuccess() {
        Channel pub1 = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "test1"));
        Channel priv = channelRepository.save(new Channel(ChannelType.PRIVATE, "secret", "test2"));

        List<Channel> results = channelRepository.findAllByTypeOrIdIn(
                ChannelType.PRIVATE,
                List.of(pub1.getId())
        );

        // PRIVATE 타입 채널 + pub1(id 매칭) 포함
        assertThat(results).contains(priv, pub1);
    }

    @Test
    @DisplayName("조건에 맞는 채널이 없으면 빈 리스트 반환")
    void findAllByTypeOrIdInFail() {
        Channel pub = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));

        List<Channel> results = channelRepository.findAllByTypeOrIdIn(
                ChannelType.PRIVATE,
                List.of(UUID.randomUUID()) // 존재하지 않는 ID
        );

        assertThat(results).isEmpty();
    }
}