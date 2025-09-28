// src/test/java/com/sprint/mission/discodeit/repository/MessageRepositoryTest.java
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class MessageRepositoryTest extends RepositorySliceTestBase {

  @Autowired MessageRepository messageRepository;
  @Autowired ChannelRepository channelRepository;
  @Autowired UserRepository userRepository;

  @Test
  void findAllByChannelIdWithAuthor_sliceDescByCreatedAt() {
    var ch = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "d"));
    var u = userRepository.save(new User("neo","n@x.io","pw", null));

    messageRepository.save(new Message("c1", ch, u, null));
    messageRepository.save(new Message("c2", ch, u, null));

    var pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"));
    var slice = messageRepository.findAllByChannelIdWithAuthor(ch.getId(), Instant.now(), pageable);

    assertThat(slice.getContent()).hasSize(1);
    assertThat(slice.hasNext()).isTrue();
  }

  @Test
  void findAllByChannelIdWithAuthor_empty_whenNoMessage() {
    var pageable = PageRequest.of(0, 20);
    var slice = messageRepository.findAllByChannelIdWithAuthor(
        UUID.randomUUID(), Instant.now(), pageable);
    assertThat(slice.getContent()).isEmpty();
  }
}
