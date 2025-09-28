package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@EnableJpaAuditing
class ChannelRepositoryTest {

  @Autowired private ChannelRepository channelRepository;
  @Autowired private EntityManager em;

  private void createChannel(ChannelType type, String name) {
    channelRepository.save(new Channel(type, name, "test description"));
  }

  @Test
  @DisplayName("id 일치 또는 타입 일치 채널 탐색 테스트")
  void findAllByTypeOrIdIn_returnsMatchedByIdsOrType() {
    // given
    createChannel(ChannelType.PUBLIC, "public1");
    createChannel(ChannelType.PUBLIC, "public2");
    createChannel(ChannelType.PRIVATE, "private1");
    createChannel(ChannelType.PRIVATE, "private2");
    em.flush();
    em.clear();

    // when: 먼저 PRIVATE 2개 조회해 id 수집
    List<Channel> privates =
        channelRepository.findAllByTypeOrIdIn(ChannelType.PRIVATE, List.of());
    assertThat(privates).hasSize(2);

    List<UUID> ids = List.of(privates.get(0).getId(), privates.get(1).getId());

    // then: id(2개) + type(PUBLIC 2개) = 4개
    List<Channel> result =
        channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, ids);
    List<String> names = result.stream().map(Channel::getName).toList();

    assertThat(result).hasSize(4);
    assertThat(names).containsExactlyInAnyOrder("public1", "public2", "private1", "private2");
  }

  @Test
  @DisplayName("id 일치 또는 타입 일치 채널 탐색(public 채널만)")
  void findAllByTypeOrIdIn_publicOnly() {
    // given
    createChannel(ChannelType.PUBLIC, "public1");
    createChannel(ChannelType.PUBLIC, "public2");
    createChannel(ChannelType.PRIVATE, "private1");
    createChannel(ChannelType.PRIVATE, "private2");
    em.flush();
    em.clear();

    // when: 존재하지 않는 id 1개 + type PUBLIC
    List<Channel> result =
        channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(UUID.randomUUID()));
    List<String> names = result.stream().map(Channel::getName).toList();

    // then
    assertThat(result).hasSize(2);
    assertThat(names).containsExactlyInAnyOrder("public1", "public2");
  }
}