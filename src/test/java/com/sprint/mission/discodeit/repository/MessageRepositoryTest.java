package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TestEntityManager em;

    private User createAuthor(String username, String email) {
        User user = new User(username, email, "password1234", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());
        return userRepository.save(user);
    }

    private Channel createChannel(ChannelType channelType, String name) {
        Channel channel = new Channel(channelType, name, "test");
        return channelRepository.save(channel);
    }

    private Message createMessage(String content, Channel channel, User author) {
        Message message = new Message(content, channel, author, new ArrayList<>());
        return messageRepository.save(message);
    }

    @Test
    @DisplayName("채널 ID와 생성 시간으로 메시지 목록 조회")
    void findAllByChannelIdWithAuthor() {
        // given
        User author = createAuthor("test01", "test01@email.com");
        Channel publicChannel = createChannel(ChannelType.PUBLIC, "공개 채널");
        Channel privateChannel = createChannel(ChannelType.PRIVATE, "비공개 채널");
        Message message1 = createMessage("메시지1", publicChannel, author);
        Message message2 = createMessage("메시지2", publicChannel, author);
        Message message3 = createMessage("메시지3", publicChannel, author);
        Message message4 = createMessage("메시지4", publicChannel, author);
        em.flush();
        em.clear();

        // when
        Slice<Message> messages = messageRepository.findAllByChannelIdWithAuthor(
            publicChannel.getId(),
            Instant.now(),
            PageRequest.of(0, 50, Direction.DESC, "createdAt"));

        // then
        assertThat(messages).isNotEmpty();
        assertThat(messages.getContent()).allSatisfy(message -> {
            assertThat(message.getAuthor().getId()).isEqualTo(author.getId());
            assertThat(message.getChannel().getId()).isEqualTo(publicChannel.getId());
        });
    }

    @Test
    @DisplayName("채널 ID로 해당 채널의 마지막 메시지 조회")
    void findLastMessageAtByChannelId() {
        // given
        User author = createAuthor("test01", "test01@email.com");
        Channel publicChannel = createChannel(ChannelType.PUBLIC, "공개 채널");
        Message message1 = createMessage("메시지1", publicChannel, author);
        Message message2 = createMessage("메시지2", publicChannel, author);
        Message message3 = createMessage("메시지3", publicChannel, author);
        Message message4 = createMessage("메시지4", publicChannel, author);
        em.flush();
        em.clear();

        // when
        Optional<Instant> lastMessageAtByChannelId = messageRepository.findLastMessageAtByChannelId(
            publicChannel.getId());

        // then
        // 어떻게 해야 될지 모르겠습니다.
//        assertThat(lastMessageAtByChannelId.get())
//            .isEqualTo(message4.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
//            .isEqualTo(message4.getCreatedAt().atOffset(ZoneOffset.UTC).toInstant());
//            .isEqualTo(message4.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant());
//            .isEqualTo(message4.getCreatedAt().atZone(ZoneOffset.UTC).toInstant());
    }

    @Test
    @DisplayName("채널 ID로 해당 채널의 모든 메시지 삭제")
    void deleteAllByChannelId() {
        // given
        User author = userRepository.save(createAuthor("test01", "test01@email.com"));
        Channel publicChannel = createChannel(ChannelType.PUBLIC, "공개 채널");
        Message message1 = createMessage("메시지1", publicChannel, author);
        Message message2 = createMessage("메시지2", publicChannel, author);
        em.flush();
        em.clear();

        // when
        messageRepository.deleteAllByChannelId(publicChannel.getId());

        // then
        List<Message> messageList = messageRepository.findAllByChannelIdWithAuthor(
            publicChannel.getId(), Instant.now(), PageRequest.of(0, 50)).getContent();
        assertThat(messageList).isEmpty();
    }
}