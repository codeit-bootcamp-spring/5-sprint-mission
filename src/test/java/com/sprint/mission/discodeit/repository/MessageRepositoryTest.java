package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  private Channel channel;
  private User user;
  private Message message1;
  private Message message2;

  @BeforeEach
  void setUp() {
    user = userRepository.save(User.builder()
                                   .username("testuser")
                                   .email("test@example.com")
                                   .password("password")
                                   .build());

    channel = channelRepository.save(Channel.builder()
                                            .name("general")
                                            .type(ChannelType.PUBLIC)
                                            .build());

    message1 = messageRepository.save(Message.builder()
                                             .channel(channel)
                                             .author(user)
                                             .content("Hello World")
                                             .build());

    message2 = messageRepository.save(Message.builder()
                                             .channel(channel)
                                             .author(user)
                                             .content("Second message")
                                             .build());
  }

  @Test
  void testFindById() {
    Optional<Message> found = messageRepository.findById(message1.getId());
    assertThat(found).isPresent();
    assertThat(found.get()
                    .getContent()).isEqualTo("Hello World");
    assertThat(found.get()
                    .getAuthor()).isNotNull();
    assertThat(found.get()
                    .getChannel()).isNotNull();
  }

  @Test
  void testFindByChannelIdOrderByCreatedAtDesc() {
    List<Message> messages = messageRepository.findByChannelIdOrderByCreatedAtDesc(channel.getId(),
        PageRequest.of(0, 10));
    assertThat(messages).hasSize(2);
    assertThat(messages.get(0)
                       .getContent()).isEqualTo("Second message");
    assertThat(messages.get(1)
                       .getContent()).isEqualTo("Hello World");
  }

  @Test
  void testFindByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc() {
    List<Message> messages = messageRepository.findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(
        channel.getId(), Instant.now(), PageRequest.of(0, 10));
    assertThat(messages).hasSize(2);
  }

  @Test
  void testCountByChannelId() {
    Long count = messageRepository.countByChannelId(channel.getId());
    assertThat(count).isEqualTo(2);
  }
}
