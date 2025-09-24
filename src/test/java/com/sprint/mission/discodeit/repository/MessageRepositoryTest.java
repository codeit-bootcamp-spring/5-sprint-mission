package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;

    private User testUser;
    private Channel testChannel;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User(
                "testUser", "password", "testNick", "test@example.com", null
        ));
        testChannel = channelRepository.save(new Channel("testChannel", "Test Channel Description"));
    }

    @Test
    @DisplayName("채널 ID로 메시지 조회")
    void findByChannelId_success() {
        Message message = messageRepository.save(new Message(testUser, testChannel, "hello world"));

        List<Message> result = messageRepository.findByChannelId(testChannel.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("hello world");
        assertThat(result.get(0).getAuthor().getUsername()).isEqualTo("testUser");
        assertThat(result.get(0).getChannel().getId()).isEqualTo(testChannel.getId());
    }

    @Test
    @DisplayName("채널에 메시지가 없으면 빈 리스트 반환")
    void findByChannelId_empty() {
        List<Message> result = messageRepository.findByChannelId(testChannel.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("작성자 ID와 채널 ID로 메시지 조회")
    void findByAuthorIdAndChannelId_success() {
        messageRepository.save(new Message(testUser, testChannel, "첫 번째 메시지"));
        messageRepository.save(new Message(testUser, testChannel, "두 번째 메시지"));

        User otherUser = userRepository.save(new User(
                "otherUser", "password", "otherNick", "other@example.com", null
        ));
        messageRepository.save(new Message(otherUser, testChannel, "다른 사용자 메시지"));

        List<Message> result = messageRepository.findByAuthorIdAndChannelId(testUser.getId(), testChannel.getId());

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(message -> message.getAuthor().getId().equals(testUser.getId()));
        assertThat(result).allMatch(message -> message.getChannel().getId().equals(testChannel.getId()));
    }

    @Test
    @DisplayName("작성자와 채널에 해당하는 메시지가 없으면 빈 리스트 반환")
    void findByAuthorIdAndChannelId_empty() {
        User otherUser = userRepository.save(new User(
                "otherUser", "password", "otherNick", "other@example.com", null
        ));

        List<Message> result = messageRepository.findByAuthorIdAndChannelId(otherUser.getId(), testChannel.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("커서 기반 메시지 조회 (createdAt 이전만)")
    void findByChannelIdWithCursor_success() throws InterruptedException {
        Message oldMessage = messageRepository.save(new Message(testUser, testChannel, "이전 메시지"));

        Thread.sleep(10);

        Message newMessage = messageRepository.save(new Message(testUser, testChannel, "최신 메시지"));

        Slice<Message> result = messageRepository.findByChannelIdWithCursor(
                testChannel.getId(), newMessage.getCreatedAt(), PageRequest.of(0, 10)
        );

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("이전 메시지");
        assertThat(result.getContent().get(0).getId()).isEqualTo(oldMessage.getId());
    }

    @Test
    @DisplayName("커서보다 이전 메시지가 없으면 빈 결과")
    void findByChannelIdWithCursor_empty() {
        Message message = messageRepository.save(new Message(testUser, testChannel, "유일한 메시지"));

        Slice<Message> result = messageRepository.findByChannelIdWithCursor(
                testChannel.getId(), message.getCreatedAt(), PageRequest.of(0, 10)
        );

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널의 최신 메시지 시간 조회")
    void findLatestMessageTimeByChannelId_success() throws InterruptedException {
        messageRepository.save(new Message(testUser, testChannel, "첫 번째 메시지"));

        Thread.sleep(10);

        Message latestMessage = messageRepository.save(new Message(testUser, testChannel, "최신 메시지"));

        Optional<Instant> result = messageRepository.findLatestMessageTimeByChannelId(testChannel.getId());

        assertThat(result).isPresent();
        assertThat(result.get().toEpochMilli())
                .isEqualTo(latestMessage.getCreatedAt().toEpochMilli());
    }

    @Test
    @DisplayName("채널에 메시지가 없으면 빈 Optional 반환")
    void findLatestMessageTimeByChannelId_empty() {
        Optional<Instant> result = messageRepository.findLatestMessageTimeByChannelId(testChannel.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("특정 채널의 최신 메시지 시간만 조회")
    void findLatestMessageTimeByChannelId_multipleChannels() throws InterruptedException {
        Channel otherChannel = channelRepository.save(new Channel("otherChannel", "Other Channel"));

        messageRepository.save(new Message(testUser, otherChannel, "다른 채널 메시지"));

        Thread.sleep(10);

        Message targetMessage = messageRepository.save(new Message(testUser, testChannel, "대상 채널 메시지"));

        Optional<Instant> result = messageRepository.findLatestMessageTimeByChannelId(testChannel.getId());

        assertThat(result).isPresent();
        assertThat(result.get().toEpochMilli())
                .isEqualTo(targetMessage.getCreatedAt().toEpochMilli());
    }
}