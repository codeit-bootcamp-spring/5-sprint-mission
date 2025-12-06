package com.sprint.mission.discodeit.domain.repository;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.presentation.dto.ChannelLastMessageAtDto;
import com.sprint.mission.discodeit.global.config.JpaConfig;
import com.sprint.mission.discodeit.message.domain.Message;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("MessageRepository 슬라이스 테스트")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User author;
    private Channel channel;

    @BeforeEach
    void setUp() {
        author = userRepository.save(new User("testuser", "test@example.com", "password1234", null));
        channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "General channel"));

        messageRepository.save(new Message("First message", channel, author));
        entityManager.flush();

        messageRepository.save(new Message("Second message", channel, author));
        entityManager.flush();

        messageRepository.save(new Message("Third message", channel, author));
        entityManager.flush();
    }

    @Nested
    @DisplayName("findByChannelId (List)")
    class FindByChannelIdList {

        @Test
        @DisplayName("채널 ID로 메시지 목록을 조회한다")
        void findByChannelId_returnsMessages() {
            // when
            List<Message> messages = messageRepository.findByChannelId(channel.getId());

            // then
            assertThat(messages).hasSize(3);
        }
    }

    @Nested
    @DisplayName("findByChannelId (Page)")
    class FindByChannelIdPage {

        @Test
        @DisplayName("채널 ID로 페이징된 메시지를 조회한다")
        void findByChannelId_returnsPagedMessages() {
            // given
            PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

            // when
            Page<Message> messagePage = messageRepository.findByChannelId(channel.getId(), pageRequest);

            // then
            assertThat(messagePage.getContent()).hasSize(2);
            assertThat(messagePage.getTotalElements()).isEqualTo(3);
            assertThat(messagePage.getTotalPages()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("findByChannelIdAndCreatedAtBefore")
    class FindByChannelIdAndCreatedAtBefore {

        @Test
        @DisplayName("특정 시간 이전의 메시지를 조회한다")
        void findByChannelIdAndCreatedAtBefore_returnsMessagesBeforeTime() {
            // given
            Instant beforeTime = Instant.now().plus(1, ChronoUnit.HOURS);
            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

            // when
            Page<Message> messagePage = messageRepository.findByChannelIdAndCreatedAtBefore(
                channel.getId(), beforeTime, pageRequest);

            // then
            assertThat(messagePage.getContent()).hasSize(3);
        }

        @Test
        @DisplayName("특정 시간 이전의 메시지가 없으면 빈 페이지를 반환한다")
        void findByChannelIdAndCreatedAtBefore_withOldTime_returnsEmptyPage() {
            // given
            Instant beforeTime = Instant.now().minus(1, ChronoUnit.HOURS);
            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<Message> messagePage = messageRepository.findByChannelIdAndCreatedAtBefore(
                channel.getId(), beforeTime, pageRequest);

            // then
            assertThat(messagePage.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findFirstByChannelOrderByCreatedAtDesc")
    class FindFirstByChannelOrderByCreatedAtDesc {

        @Test
        @DisplayName("채널의 가장 최근 메시지를 조회한다")
        void findFirstByChannelOrderByCreatedAtDesc_returnsLatestMessage() {
            // when
            Message latestMessage = messageRepository.findFirstByChannelOrderByCreatedAtDesc(channel);

            // then
            assertThat(latestMessage).isNotNull();
            assertThat(latestMessage.getContent()).isEqualTo("Third message");
        }
    }

    @Nested
    @DisplayName("findLastMessageAtByChannels")
    class FindLastMessageAtByChannels {

        @Test
        @DisplayName("채널별 마지막 메시지 시간을 조회한다")
        void findLastMessageAtByChannels_returnsLastMessageTimes() {
            // given
            Channel channel2 = channelRepository.save(new Channel(ChannelType.PUBLIC, "random", "Random channel"));
            messageRepository.save(new Message("Channel 2 message", channel2, author));
            entityManager.flush();

            // when
            List<ChannelLastMessageAtDto> results = messageRepository.findLastMessageAtByChannels(
                List.of(channel, channel2));

            // then
            assertThat(results).hasSize(2);
            assertThat(results).extracting(ChannelLastMessageAtDto::channelId)
                .containsExactlyInAnyOrder(channel.getId(), channel2.getId());
        }

        @Test
        @DisplayName("메시지가 없는 채널은 결과에 포함되지 않는다")
        void findLastMessageAtByChannels_withNoMessages_returnsEmpty() {
            // given
            Channel emptyChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "empty", "Empty channel"));

            // when
            List<ChannelLastMessageAtDto> results = messageRepository.findLastMessageAtByChannels(
                List.of(emptyChannel));

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("nullifyAuthorByUserId")
    class NullifyAuthorByUserId {

        @Test
        @DisplayName("사용자 ID로 메시지 작성자를 null로 설정한다")
        void nullifyAuthorByUserId_setsAuthorToNull() {
            // when
            int updatedCount = messageRepository.nullifyAuthorByUserId(author.getId());
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(updatedCount).isEqualTo(3);

            List<Message> messages = messageRepository.findByChannelId(channel.getId());
            assertThat(messages).allMatch(m -> m.getAuthor() == null);
        }
    }
}
