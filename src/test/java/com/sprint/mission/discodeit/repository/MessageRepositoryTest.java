package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")  // application-test.yaml 사용
@EnableJpaAuditing       // Audit 필드 (createdAt, updatedAt) 자동 반영
class MessageRepositoryTest {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserStatusRepository userStatusRepository;

    @Test
    @DisplayName("채널 ID와 작성 시각 기준으로 메시지 조회 성공")
    void findAllByChannelIdWithAuthorSuccess() {
        // given
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "test"));
        User user = userRepository.save(new User("mike", "mike@test.com", "1234", null));
        userStatusRepository.save(new UserStatus(user, Instant.now()));

        Message message1 = new Message("hi1", channel, user, List.of());
        Message message2 = new Message("hi2", channel, user, List.of());
        messageRepository.saveAll(List.of(message1, message2));

        // when
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(
                channel.getId(), Instant.now().plusSeconds(1), PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("채널 마지막 메시지 시각 조회 성공")
    void findLastMessageAtByChannelIdSuccess() {
        // given
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "test"));
        User user = userRepository.save(new User("mike", "mike@test.com", "1234", null));

        Message message = new Message("hello", channel, user, List.of());
        messageRepository.save(message);

        // when
        Optional<Instant> lastMessageAt = messageRepository.findLastMessageAtByChannelId(channel.getId());

        // then
        assertThat(lastMessageAt).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 채널에서 마지막 메시지 조회 실패")
    void findLastMessageAtByChannelIdFail() {
        // when
        Optional<Instant> lastMessageAt = messageRepository.findLastMessageAtByChannelId(UUID.randomUUID());

        // then
        assertThat(lastMessageAt).isEmpty();
    }
}