package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private EntityManager em;

  private User createUser(String name) {
    return userRepository.save(new User(
        name,
        name + "@test.com",
        "12341234",
        null
    ));
  }

  private Channel createChannel(ChannelType type, String name) {
    return channelRepository.save(new Channel(
        type,
        name,
        "desc"
    ));
  }

  private Message createMessage(User author, Channel channel, String content) {
    return messageRepository.save(new Message(
        content,
        channel,
        author,
        List.of()
    ));
  }

  @Test
  void findAllByChannelIdOrderByCreatedAtDescIdDesc() {
    User author = createUser("test");
    Channel channel = createChannel(ChannelType.PUBLIC, "public");

    createMessage(author, channel, "test1");
    createMessage(author, channel, "test2");
    createMessage(author, channel, "test3");

    Pageable pageable = PageRequest.of(0, 10);
    Slice<Message> slice = messageRepository
        .findAllByChannelIdOrderByCreatedAtDescIdDesc(channel.getId(), pageable);

    assertThat(slice).hasSize(3);
    assertThat(slice.getContent().get(0).getContent()).isEqualTo("test3");
  }

  @Test
  void findAllByChannelIdOrderByCreatedAtDescIdDescChannelNotFound() {
    User author = createUser("test");
    Channel channel = createChannel(ChannelType.PUBLIC, "public");

    createMessage(author, channel, "test1");
    createMessage(author, channel, "test2");
    createMessage(author, channel, "test3");

    Pageable pageable = PageRequest.of(0, 10);
    Slice<Message> slice = messageRepository
        .findAllByChannelIdOrderByCreatedAtDescIdDesc(UUID.randomUUID(), pageable);

    assertThat(slice).isEmpty();
  }

  @Test
  @DisplayName("findNextPage - 기본 커서 페이징 흐름 검증 (createdAt desc, id desc)")
  void findNextPage_basicPaging() {
    // given
    User user = createUser("u1");
    Channel ch = createChannel(ChannelType.PUBLIC, "ch1");

    Message m1 = createMessage(user, ch, "m1"); // 가장 오래된 것
    Message m2 = createMessage(user, ch, "m2");
    Message m3 = createMessage(user, ch, "m3");
    Message m4 = createMessage(user, ch, "m4");
    Message m5 = createMessage(user, ch, "m5"); // 가장 최신

    em.flush();
    em.clear();

    // when: 첫 페이지(최신순, 2개)
    Pageable p2 = PageRequest.of(0, 2);
    Slice<Message> first = messageRepository
        .findAllByChannelIdOrderByCreatedAtDescIdDesc(ch.getId(), p2);

    // then
    assertThat(first).hasSize(2);
    List<Message> content1 = first.getContent();
    assertThat(content1.get(0).getContent()).isEqualTo("m5");
    assertThat(content1.get(1).getContent()).isEqualTo("m4");

    // given: 커서 = 첫 페이지의 마지막 요소(m4)
    Message cursor = content1.get(1);

    // when: 커서 이후 다음 페이지(2개)
    Slice<Message> second = messageRepository.findNextPage(
        ch.getId(), cursor.getCreatedAt(), cursor.getId(), p2);

    // then
    assertThat(second).hasSize(2);
    List<Message> content2 = second.getContent();
    assertThat(content2.get(0).getContent()).isEqualTo("m3");
    assertThat(content2.get(1).getContent()).isEqualTo("m2");

    // when: 마지막 페이지(남은 1개)
    Message cursor2 = content2.get(1);
    Slice<Message> third = messageRepository.findNextPage(
        ch.getId(), cursor2.getCreatedAt(), cursor2.getId(), p2);

    // then
    assertThat(third).hasSize(1);
    assertThat(third.getContent().get(0).getContent()).isEqualTo("m1");
    assertThat(third.isLast()).isTrue();
  }

  @Test
  @DisplayName("findNextPage - 존재하지 않는 채널 ID로 조회 시 빈 결과 반환")
  void findNextPage_invalidChannelId() {
    // given
    User user = createUser("u1");
    Channel ch = createChannel(ChannelType.PUBLIC, "ch1");

    Message m1 = createMessage(user, ch, "m1");
    Message m2 = createMessage(user, ch, "m2");
    em.flush();
    em.clear();

    Pageable p2 = PageRequest.of(0, 2);

    // when: 잘못된 채널 ID로 조회
    UUID wrongChannelId = UUID.randomUUID();
    Slice<Message> result = messageRepository.findNextPage(
        wrongChannelId, m1.getCreatedAt(), m1.getId(), p2);

    // then: 결과가 비어 있어야 함
    assertThat(result).isEmpty();
    assertThat(result.isLast()).isTrue();
  }

  @Test
  @DisplayName("findTopBy... - 가장 최신 메시지 1건 반환 (createdAt desc, id desc)")
  void findTop_basic() {
    // given
    User u = createUser("u1");
    Channel ch = createChannel(ChannelType.PUBLIC, "ch1");

    Message m1 = createMessage(u, ch, "m1"); // 오래된
    Message m2 = createMessage(u, ch, "m2");
    Message m3 = createMessage(u, ch, "m3"); // 가장 최신

    em.flush();
    em.clear();

    // when
    Optional<Message> opt = messageRepository.findTopByChannelIdOrderByCreatedAtDescIdDesc(
        ch.getId());

    // then
    assertThat(opt).isPresent();
    assertThat(opt.get().getContent()).isEqualTo("m3");
  }

  @Test
  @DisplayName("findTopBy... - @EntityGraph로 channel 즉시 로딩 확인")
  void findTop_entityGraphLoadsChannel() {
    // given
    User u = createUser("u1");
    Channel ch = createChannel(ChannelType.PUBLIC, "ch1");
    createMessage(u, ch, "hello");
    em.flush();
    em.clear();

    // when
    Message msg = messageRepository
        .findTopByChannelIdOrderByCreatedAtDescIdDesc(ch.getId())
        .orElseThrow();

    // then: channel이 로딩되어 있어야 함
    PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
    assertThat(util.isLoaded(msg.getChannel())).isTrue();
    // 또는
    assertThat(Hibernate.isInitialized(msg.getChannel())).isTrue();

    // 접근 시 LazyInitializationException 없어야 함
    assertThat(msg.getChannel().getName()).isEqualTo("ch1");
  }

}

