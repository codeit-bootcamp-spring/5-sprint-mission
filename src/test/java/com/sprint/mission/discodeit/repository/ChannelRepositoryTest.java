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
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private EntityManager em;

  private void createChannel(ChannelType type, String name) {
    channelRepository.save(new Channel(
        type,
        name,
        "test description"
    ));
  }

  @Test
  @DisplayName("id 일치 또는 타입 일치 채널 탐색 테스트")
  void findAllByIdInOrTypeTest() {
    createChannel(ChannelType.PUBLIC, "public1");
    createChannel(ChannelType.PUBLIC, "public2");
    createChannel(ChannelType.PRIVATE, "private1");
    createChannel(ChannelType.PRIVATE, "private2");

    em.flush();
    em.clear();

    List<Channel> privates = channelRepository.findAllByIdInOrType(List.of(), ChannelType.PRIVATE);
    List<UUID> ids = List.of(privates.get(0).getId(), privates.get(1).getId());
    List<Channel> result = channelRepository.findAllByIdInOrType(ids, ChannelType.PUBLIC);
    List<String> channelNames = result.stream()
        .map(Channel::getName)
        .toList();

    assertThat(result).hasSize(4);
    assertThat(channelNames).containsExactlyInAnyOrder("public1", "public2", "private1",
        "private2");
  }

  @Test
  @DisplayName("id 일치 또는 타입 일치 채널 탐색(public 채널만)")
  void findAllByIdInOrTypeTestPublicOnly() {
    createChannel(ChannelType.PUBLIC, "public1");
    createChannel(ChannelType.PUBLIC, "public2");
    createChannel(ChannelType.PRIVATE, "private1");
    createChannel(ChannelType.PRIVATE, "private2");

    em.flush();
    em.clear();

    List<Channel> result = channelRepository.findAllByIdInOrType(List.of(UUID.randomUUID()),
        ChannelType.PUBLIC);
    List<String> channelNames = result.stream()
        .map(Channel::getName)
        .toList();

    assertThat(result).hasSize(2);
    assertThat(channelNames).containsExactlyInAnyOrder("public1", "public2");
  }

}
