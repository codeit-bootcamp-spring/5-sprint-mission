package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.channel.data.ChannelLastMessageAtDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Autowired
    private EntityManager entityManager;

    private User author;
    private Channel channel1;
    private Channel channel2;
    private Message message1;
    private Message message2;
    private Message message3;

    @BeforeEach
    void setUp() throws InterruptedException {
        // given - 사용자 생성
        author = new User("testauthor", "author@example.com", "encoded", null);
        userRepository.save(author);

        // given - 채널 생성
        channel1 = new Channel(ChannelType.PUBLIC, "Channel 1", null);
        channel2 = new Channel(ChannelType.PUBLIC, "Channel 2", null);
        channelRepository.saveAll(List.of(channel1, channel2));

        // given - 메시지 생성 (시간 순서를 보장하기 위해 약간의 지연 추가)
        message1 = new Message("Message 1", channel1, author);
        messageRepository.save(message1);
        Thread.sleep(10);

        message2 = new Message("Message 2", channel1, author);
        messageRepository.save(message2);
        Thread.sleep(10);

        message3 = new Message("Message 3", channel2, author);
        messageRepository.save(message3);
    }

    @Test
    @DisplayName("findPageWithoutCursorByChannelId - 커서 없이 페이징 조회 성공")
    void findPageWithoutCursorByChannelId_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<Message> page = messageRepository.findPageWithoutCursorByChannelId(
            channel1.getId(),
            pageable
        );

        // then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Message::getContent)
            .containsExactly("Message 2", "Message 1"); // 최신순 정렬

        // EntityGraph로 author, profile이 함께 로드되었는지 확인
        page.getContent().forEach(message -> assertThat(message.getAuthor()).isNotNull());
    }

    @Test
    @DisplayName("findPageWithoutCursorByChannelId - 존재하지 않는 채널 조회 시 빈 페이지 반환")
    void findPageWithoutCursorByChannelId_NotFound() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Message> page = messageRepository.findPageWithoutCursorByChannelId(
            UUID.randomUUID(),
            pageable
        );

        // then
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("findPageWithoutCursorByChannelId - 페이지 크기에 맞게 조회")
    void findPageWithoutCursorByChannelId_WithPageSize() {
        // given
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<Message> page = messageRepository.findPageWithoutCursorByChannelId(
            channel1.getId(),
            pageable
        );

        // then
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().get(0).getContent()).isEqualTo("Message 2");
    }

    @Test
    @DisplayName("findPageByChannelId - 커서 기반 페이징 조회 성공")
    void findPageByChannelId_Success() {
        // given
        Instant cursor = message2.getCreatedAt();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<Message> page = messageRepository.findPageByChannelId(
            channel1.getId(),
            cursor,
            pageable
        );

        // then
        // cursor(message2 생성시간) 이전 메시지만 조회되어야 함
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getContent()).isEqualTo("Message 1");

        // EntityGraph로 author, profile이 함께 로드되었는지 확인
        page.getContent().forEach(message -> assertThat(message.getAuthor()).isNotNull());
    }

    @Test
    @DisplayName("findPageByChannelId - 커서 이전 메시지가 없으면 빈 페이지 반환")
    void findPageByChannelId_NoPreviousMessages() {
        // given - 가장 오래된 메시지의 시간을 커서로 사용
        Instant cursor = message1.getCreatedAt();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Message> page = messageRepository.findPageByChannelId(
            channel1.getId(),
            cursor,
            pageable
        );

        // then
        assertThat(page.getContent()).isEmpty();
    }

    @Test
    @DisplayName("findLastMessageAtByChannels - 여러 채널의 마지막 메시지 시간 조회 성공")
    void findLastMessageAtByChannels_Success() {
        // when
        List<ChannelLastMessageAtDto> results = messageRepository.findLastMessageAtByChannels(
            List.of(channel1, channel2)
        );

        // then
        assertThat(results).hasSize(2);

        ChannelLastMessageAtDto channel1Result = results.stream()
            .filter(dto -> dto.channelId().equals(channel1.getId()))
            .findFirst()
            .orElseThrow();
        assertThat(channel1Result.lastMessageAt()).isEqualTo(message2.getCreatedAt());

        ChannelLastMessageAtDto channel2Result = results.stream()
            .filter(dto -> dto.channelId().equals(channel2.getId()))
            .findFirst()
            .orElseThrow();
        assertThat(channel2Result.lastMessageAt()).isEqualTo(message3.getCreatedAt());
    }

    @Test
    @DisplayName("findLastMessageAtByChannels - 메시지가 없는 채널은 결과에 포함되지 않음")
    void findLastMessageAtByChannels_NoMessages() {
        // given
        Channel emptyChannel = new Channel(ChannelType.PUBLIC, "Empty Channel", null);
        channelRepository.save(emptyChannel);

        // when
        List<ChannelLastMessageAtDto> results = messageRepository.findLastMessageAtByChannels(
            List.of(channel1, emptyChannel)
        );

        // then
        assertThat(results).hasSize(1); // emptyChannel은 제외됨
        assertThat(results.get(0).channelId()).isEqualTo(channel1.getId());
    }

    @Test
    @DisplayName("findLastMessageAtByChannelId - 채널의 마지막 메시지 시간 조회 성공")
    void findLastMessageAtByChannelId_Success() {
        // when
        Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channel1.getId());

        // then
        assertThat(lastMessageAt).isEqualTo(message2.getCreatedAt());
    }

    @Test
    @DisplayName("findLastMessageAtByChannelId - 메시지가 없는 채널은 null 반환")
    void findLastMessageAtByChannelId_NoMessages() {
        // given
        Channel emptyChannel = new Channel(ChannelType.PUBLIC, "Empty Channel", null);
        channelRepository.save(emptyChannel);

        // when
        Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(emptyChannel.getId());

        // then
        assertThat(lastMessageAt).isNull();
    }

    @Test
    @DisplayName("nullifyAuthorByUser - 사용자의 모든 메시지 작성자를 null로 변경")
    void nullifyAuthorByUser_Success() {
        // when
        int updatedCount = messageRepository.nullifyAuthorByUser(author);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(updatedCount).isEqualTo(3);

        // 검증
        Message updatedMessage1 = messageRepository.findById(message1.getId()).orElseThrow();
        Message updatedMessage2 = messageRepository.findById(message2.getId()).orElseThrow();
        Message updatedMessage3 = messageRepository.findById(message3.getId()).orElseThrow();

        assertThat(updatedMessage1.getAuthor()).isNull();
        assertThat(updatedMessage2.getAuthor()).isNull();
        assertThat(updatedMessage3.getAuthor()).isNull();
    }

    @Test
    @DisplayName("nullifyAuthorByUser - 메시지가 없는 사용자는 0 반환")
    void nullifyAuthorByUser_NoMessages() {
        // given
        User newUser = new User("newuser", "new@example.com", "encoded", null);
        userRepository.save(newUser);

        // when
        int updatedCount = messageRepository.nullifyAuthorByUser(newUser);

        // then
        assertThat(updatedCount).isZero();
    }

    @Test
    @DisplayName("deleteAllByChannelId - 채널의 모든 메시지와 첨부파일 삭제")
    void deleteAllByChannelId_Success() {
        // when
        messageRepository.deleteAllByChannelId(channel1.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        // 검증 - channel1의 메시지들이 삭제되었는지 확인
        List<Message> remainingMessages = messageRepository.findAll();
        assertThat(remainingMessages).hasSize(1);
        assertThat(remainingMessages.get(0).getContent()).isEqualTo("Message 3");
        assertThat(remainingMessages.get(0).getChannel().getId()).isEqualTo(channel2.getId());
    }

    @Test
    @DisplayName("deleteAllByChannelId - 메시지가 없는 채널 삭제 시 메시지 개수 변화 없음")
    void deleteAllByChannelId_NoMessages() {
        // given
        Channel emptyChannel = new Channel(ChannelType.PUBLIC, "Empty Channel", null);
        channelRepository.save(emptyChannel);
        int initialCount = messageRepository.findAll().size();

        // when
        messageRepository.deleteAllByChannelId(emptyChannel.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        int finalCount = messageRepository.findAll().size();
        assertThat(finalCount).isEqualTo(initialCount);
    }

    @Test
    @DisplayName("save - 메시지 생성 시 JPA Audit 필드가 자동 설정됨")
    void save_AuditFieldsAutoSet() {
        // given
        Message newMessage = new Message("New Message", channel1, author);

        // when
        Message savedMessage = messageRepository.save(newMessage);

        // then
        assertThat(savedMessage.getId()).isNotNull();
        assertThat(savedMessage.getCreatedAt()).isNotNull();
        assertThat(savedMessage.getUpdatedAt()).isNotNull();
    }
}
