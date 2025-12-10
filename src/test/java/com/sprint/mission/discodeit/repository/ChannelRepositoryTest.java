package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  private Channel channel;

  @BeforeEach
  void setUp() {
    channel = Channel.builder()
                     .name("Test Channel")
                     .type(ChannelType.PUBLIC)
                     .description("Test Description")
                     .build();
    channelRepository.save(channel);
  }

  @Test
  void findByType_success() {
    List<Channel> channels = channelRepository.findByType(ChannelType.PUBLIC);
    assertThat(channels).isNotEmpty();
    assertThat(channels.get(0)
                       .getName()).isEqualTo("Test Channel");
  }

  @Test
  void findByType_notFound() {
    List<Channel> channels = channelRepository.findByType(ChannelType.PRIVATE);
    assertThat(channels).isEmpty();
  }

  @Test
  void findById_success() {
    Optional<Channel> found = channelRepository.findById(channel.getId());
    assertThat(found).isPresent();
    assertThat(found.get()
                    .getName()).isEqualTo("Test Channel");
  }

  @Test
  void findById_notFound() {
    Optional<Channel> found = channelRepository.findById(UUID.randomUUID());
    assertThat(found).isEmpty();
  }
}
