package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

class ReadStatusRepositoryTest extends RepositorySliceTestBase {

  @Autowired ReadStatusRepository readStatusRepository;
  @Autowired UserRepository userRepository;
  @Autowired ChannelRepository channelRepository;

  @Test
  void existsByUserIdAndChannelId_true_afterSave() {
    var u = userRepository.save(new User("neo","n@x.io","pw", null));
    var ch = channelRepository.save(new Channel(ChannelType.PUBLIC, "g", "d"));

    readStatusRepository.save(new ReadStatus(u, ch, Instant.now()));

    assertThat(readStatusRepository.existsByUserIdAndChannelId(u.getId(), ch.getId())).isTrue();
  }

  @Test
  void findAllByUserId_returnsSubscriptions() {
    var u = userRepository.save(new User("trinity","t@x.io","pw", null));
    var ch1 = channelRepository.save(new Channel(ChannelType.PUBLIC, "g1", "d1"));
    var ch2 = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));

    readStatusRepository.save(new ReadStatus(u, ch1, Instant.now()));
    readStatusRepository.save(new ReadStatus(u, ch2, Instant.now()));

    var list = readStatusRepository.findAllByUserId(u.getId());
    assertThat(list).hasSize(2);
    assertThat(list).extracting(ReadStatus::getChannel).extracting(Channel::getId)
        .containsExactlyInAnyOrder(ch1.getId(), ch2.getId());
  }

  @Test
  void deleteAllByChannelId_removesRows() {
    var u = userRepository.save(new User("morpheus","m@x.io","pw", null));
    var ch = channelRepository.save(new Channel(ChannelType.PUBLIC, "g", "d"));

    readStatusRepository.save(new ReadStatus(u, ch, Instant.now()));
    assertThat(readStatusRepository.findAllByUserId(u.getId())).hasSize(1);

    readStatusRepository.deleteAllByChannelId(ch.getId());
    assertThat(readStatusRepository.findAllByUserId(u.getId())).isEmpty();
  }
}
