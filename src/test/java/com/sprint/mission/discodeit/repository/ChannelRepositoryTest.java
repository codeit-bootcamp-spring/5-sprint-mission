// src/test/java/com/sprint/mission/discodeit/repository/ChannelRepositoryTest.java
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.TestJpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

class ChannelRepositoryTest extends RepositorySliceTestBase {

  @Autowired ChannelRepository channelRepository;

  @Test
  void findAllByTypeOrIdIn_returnsPublicAndPrivateMatched() {
    var pub = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));
    var prv = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));

    var result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(prv.getId()));
    assertThat(result).extracting(Channel::getId).contains(pub.getId(), prv.getId());
  }

  @Test
  void findAllByTypeOrIdIn_empty_whenNoMatch() {
    var result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(UUID.randomUUID()));
    assertThat(result).isEmpty();
  }
}
