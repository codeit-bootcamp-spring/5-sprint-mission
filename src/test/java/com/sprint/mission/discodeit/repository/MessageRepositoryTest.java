package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import java.time.Instant;
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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@EnableJpaAuditing
class MessageRepositoryTest {

  @Autowired private MessageRepository messageRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ChannelRepository channelRepository;
  @Autowired private EntityManager em;

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
  @DisplayName("findAllByChannelIdWithAuthor - 지정 시점 이전(createdAt < cursor) 메시지 슬라이스 + 연관 즉시 로딩 검증")
  void findAllByChannelIdWithAuthor_basicAndFetchJoins() {
    // given
    User author = createUser("u1");
    Channel ch = createChannel(ChannelType.PUBLIC, "ch1");

    createMessage(author, ch, "m1");
    createMessage(author, ch, "m2");
    createMessage(author, ch, "m3");
    em.flush();
    em.clear();

    Pageable pageable = PageRequest.of(0, 10);
    Instant cursor = Instant.now(); // 현재 시각 이전 메시지 전부 대상

    // when
    Slice<Message> slice =
        messageRepository.findAllByChannelIdWithAuthor(ch.getId(), cursor, pageable);

    // then: 개수만 검증(정렬 보장 X), 연관 엔티티가 fetch join으로 로딩됐는지 확인
    assertThat(slice.getNumberOfElements()).isEqualTo(3);

    Message any = slice.getContent().get(0);
    PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
    assertThat(util.isLoaded(any.getAuthor())).isTrue();
    // author.status / author.profile 도 fetch join 대상
    assertThat(util.isLoaded(any.getAuthor().getStatus())).isTrue();
    assertThat(util.isLoaded(any.getAuthor().getProfile())).isTrue();

    // 보조 확인(Hibernate)
    assertThat(Hibernate.isInitialized(any.getAuthor())).isTrue();
    assertThat(Hibernate.isInitialized(any.getAuthor().getStatus())).isTrue();
    assertThat(Hibernate.isInitialized(any.getAuthor().getProfile())).isTrue();
  }

  @Test
  @DisplayName("findAllByChannelIdWithAuthor - 아주 이른 커서(과거)면 빈 결과 반환")
  void findAllByChannelIdWithAuthor_emptyWhenCursorTooEarly() {
    // given
    User author = createUser("u1");
    Channel ch = createChannel(ChannelType.PUBLIC, "ch1");
    createMessage(author, ch, "m1");
    createMessage(author, ch, "m2");
    em.flush();
    em.clear();

    Pageable pageable = PageRequest.of(0, 10);
    Instant veryEarly = Instant.EPOCH; // 1970-01-01

    // when
    Slice<Message> slice =
        messageRepository.findAllByChannelIdWithAuthor(ch.getId(), veryEarly, pageable);

    // then
    assertThat(slice).isEmpty();
    assertThat(slice.isLast()).isTrue();
  }

  @Test
  @DisplayName("findLastMessageAtByChannelId - 최신 메시지 시각이 Optional로 조회된다")
  void findLastMessageAtByChannelId_present() {
    // given
    User u = createUser("u1");
    Channel ch = createChannel(ChannelType.PUBLIC, "ch1");
    createMessage(u, ch, "m1");
    createMessage(u, ch, "m2");
    em.flush();
    em.clear();

    // when
    Optional<Instant> last =
        messageRepository.findLastMessageAtByChannelId(ch.getId());

    // then
    assertThat(last).isPresent();
  }

  @Test
  @DisplayName("deleteAllByChannelId - 해당 채널 메시지만 일괄 삭제")
  void deleteAllByChannelId_bulkDelete() {
    // given
    User u = createUser("u1");
    Channel ch1 = createChannel(ChannelType.PUBLIC, "ch1");
    Channel ch2 = createChannel(ChannelType.PUBLIC, "ch2");
    createMessage(u, ch1, "c1-1");
    createMessage(u, ch1, "c1-2");
    createMessage(u, ch2, "c2-1");
    em.flush();
    em.clear();

    // when
    messageRepository.deleteAllByChannelId(ch1.getId());
    em.flush();
    em.clear();

    // then: ch1 비어있고, ch2 는 남아 있어야 한다
    Slice<Message> ch1Messages =
        messageRepository.findAllByChannelIdWithAuthor(ch1.getId(), Instant.now(), PageRequest.of(0, 10));
    Slice<Message> ch2Messages =
        messageRepository.findAllByChannelIdWithAuthor(ch2.getId(), Instant.now(), PageRequest.of(0, 10));

    assertThat(ch1Messages).isEmpty();
    assertThat(ch2Messages.getNumberOfElements()).isEqualTo(1);
  }
}

