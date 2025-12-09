package com.sprint.mission.discodeit.message.domain;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.global.config.JpaConfig;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("MessageRepository 슬라이스 테스트")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User author1;
    private User author2;
    private Channel channel1;
    private Channel channel2;

    @BeforeEach
    void setUp() {
        author1 = userRepository.save(
            new User("author1", "author1@test.com", "password1234", null));
        author2 = userRepository.save(
            new User("author2", "author2@test.com", "password1234", null));

        channel1 = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "general", "General channel"));
        channel2 = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "random", "Random channel"));
    }

    @Nested
    @DisplayName("findPagedWithAuthorAndProfileByChannelId")
    class FindPagedWithAuthorAndProfileByChannelId {

        @Test
        @DisplayName("최신순 정렬 및 페이지 크기 제한 동작 확인")
        void returnsSortedMessagesWithPageLimit() throws InterruptedException {
            // given
            for (int i = 1; i <= 5; i++) {
                messageRepository.save(new Message("msg" + i, channel1, author1));
                Thread.sleep(10);
            }

            // 최신순으로 3개만 요청
            PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

            // when
            Page<Message> result = messageRepository.findPagedWithAuthorAndProfileByChannelId(
                channel1.getId(), pageRequest);

            // then
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getContent().get(0).getContent()).isEqualTo("msg5");
            assertThat(result.hasNext()).isTrue();
        }

        @Test
        @DisplayName("메시지 없으면 빈 페이지 반환")
        void returnsEmptyPage_whenNoMessages() {
            // given
            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<Message> result = messageRepository.findPagedWithAuthorAndProfileByChannelId(
                channel1.getId(), pageRequest);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("author와 profile 함께 조회 (N+1 방지)")
        void fetchesAuthorAndProfile() {
            // given
            Channel channel = channelRepository.save(
                new Channel(ChannelType.PUBLIC, "general", null));
            User author = userRepository.save(
                new User("author", "email", "pw", null));
            PageRequest pageRequest = PageRequest.of(0, 10);
            messageRepository.save(new Message("content", channel, author));

            entityManager.flush();
            entityManager.clear();

            // when
            Page<Message> result = messageRepository.findPagedWithAuthorAndProfileByChannelId(
                channel.getId(),
                PageRequest.of(0, 10)
            );

            Message message = result.getContent().get(0);
            PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

            // then
            assertThat(util.isLoaded(message.getAuthor()))
                .as("Author는 Fetch Join 되었으므로 로딩 상태여야 함")
                .isTrue();
            assertThat(util.isLoaded(message.getAuthor().getProfile()))
                .as("Profile까지 함께 Fetch Join 되어야 함")
                .isTrue();
            assertThat(util.isLoaded(message.getChannel()))
                .as("Channel은 Fetch Join 대상이 아니므로 초기화되지 않은 프록시 상태여야 함")
                .isFalse();
        }

        @Test
        @DisplayName("다른 채널 메시지 제외")
        void ignoresMessagesFromOtherChannels() {
            // given
            // Channel 1에 메시지 저장
            messageRepository.save(new Message("msg_ch1", channel1, author1));

            // Channel 2에 메시지 저장
            messageRepository.save(new Message("msg_ch2", channel2, author1));

            // when: Channel 1 조회
            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<Message> result = messageRepository.findPagedWithAuthorAndProfileByChannelId(
                channel1.getId(), pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getContent()).isEqualTo("msg_ch1");
        }
    }

    @Nested
    @DisplayName("findPagedWithAuthorAndProfileByChannelIdAndCreatedAtBefore")
    class FindPagedWithAuthorAndProfileByChannelIdAndCreatedAtBefore {

        @Test
        @DisplayName("커서 이전 메시지 조회 성공")
        void returnsMessagesBeforeCursor() throws InterruptedException {
            // given
            Message oldMessage = messageRepository.save(new Message("old", channel1, author1));

            Thread.sleep(10);
            Message newMessage = messageRepository.save(new Message("new", channel1, author1));

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Instant cursor = newMessage.getCreatedAt();

            // when
            Page<Message> result = messageRepository.findPagedWithAuthorAndProfileByChannelIdAndCreatedAtBefore(
                channel1.getId(), cursor, pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getContent()).isEqualTo("old");
        }

        @Test
        @DisplayName("커서 이전 메시지 없으면 빈 페이지 반환")
        void returnsEmptyPage_whenNoMessagesBeforeCursor() {
            // given
            messageRepository.save(new Message("message", channel1, author1));

            PageRequest pageRequest = PageRequest.of(0, 10);
            Instant veryOldCursor = Instant.parse("2000-01-01T00:00:00Z");

            // when
            Page<Message> result = messageRepository.findPagedWithAuthorAndProfileByChannelIdAndCreatedAtBefore(
                channel1.getId(), veryOldCursor, pageRequest);

            // then
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findIdsByChannelId")
    class FindIdsByChannelId {

        @Test
        @DisplayName("채널의 모든 메시지 ID 조회 성공")
        void returnsAllMessageIds() {
            // given
            Message msg1 = messageRepository.save(new Message("message1", channel1, author1));
            Message msg2 = messageRepository.save(new Message("message2", channel1, author2));
            messageRepository.save(new Message("other channel", channel2, author1));

            // when
            Set<UUID> result = messageRepository.findIdsByChannelId(channel1.getId());

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder(msg1.getId(), msg2.getId());
        }

        @Test
        @DisplayName("메시지 없으면 빈 Set 반환")
        void returnsEmptySet_whenNoMessages() {
            // when
            Set<UUID> result = messageRepository.findIdsByChannelId(channel1.getId());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findLastCreatedAtByChannelId")
    class FindLastCreatedAtByChannelId {

        @Test
        @DisplayName("채널의 최신 메시지 생성 시간 반환")
        void returnsLastCreatedAt() throws InterruptedException {
            // given
            messageRepository.save(new Message("old", channel1, author1));
            Thread.sleep(10);
            Message latestMessage = messageRepository.save(new Message("new", channel1, author1));

            // when
            Optional<Instant> result = messageRepository.findLastCreatedAtByChannelId(channel1.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(latestMessage.getCreatedAt());
        }

        @Test
        @DisplayName("메시지 없으면 빈 Optional 반환")
        void returnsEmpty_whenNoMessages() {
            // when
            Optional<Instant> result = messageRepository.findLastCreatedAtByChannelId(channel1.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("다른 채널 메시지 제외")
        void excludesOtherChannelMessages() throws InterruptedException {
            // given
            Message channel1Message = messageRepository.save(new Message("ch1", channel1, author1));
            Thread.sleep(10);
            messageRepository.save(new Message("ch2 newer", channel2, author1));

            // when
            Optional<Instant> result = messageRepository.findLastCreatedAtByChannelId(channel1.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(channel1Message.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("findLastMessageByChannelIdIn")
    class FindLastMessageByChannelIdIn {

        @Test
        @DisplayName("여러 채널의 최신 메시지 조회 성공")
        void returnsLastMessagePerChannel() throws InterruptedException {
            // given
            messageRepository.save(new Message("ch1 old", channel1, author1));
            Thread.sleep(10);
            Message ch1Latest = messageRepository.save(new Message("ch1 new", channel1, author1));

            messageRepository.save(new Message("ch2 old", channel2, author1));
            Thread.sleep(10);
            Message ch2Latest = messageRepository.save(new Message("ch2 new", channel2, author2));

            List<UUID> channelIds = List.of(channel1.getId(), channel2.getId());

            // when
            List<Message> result = messageRepository.findLastMessageByChannelIdIn(channelIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Message::getContent)
                .containsExactlyInAnyOrder("ch1 new", "ch2 new");
        }

        @Test
        @DisplayName("메시지 없는 채널은 결과에서 제외")
        void excludesChannelsWithNoMessages() {
            // given
            messageRepository.save(new Message("ch1 msg", channel1, author1));
            List<UUID> channelIds = List.of(channel1.getId(), channel2.getId());

            // when
            List<Message> result = messageRepository.findLastMessageByChannelIdIn(channelIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getChannel().getId()).isEqualTo(channel1.getId());
        }

        @Test
        @DisplayName("채널 ID 목록 비어있으면 빈 리스트 반환")
        void returnsEmptyList_whenEmptyChannelIds() {
            // when
            List<Message> result = messageRepository.findLastMessageByChannelIdIn(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("nullifyAuthorByUserId")
    class NullifyAuthorByUserId {

        @Test
        @DisplayName("사용자의 모든 메시지 author null 설정 성공")
        void nullifiesAuthor() {
            // given
            Message msg1 = messageRepository.save(new Message("msg1", channel1, author1));
            Message msg2 = messageRepository.save(new Message("msg2", channel2, author1));
            Message otherAuthorMsg = messageRepository.save(new Message("other", channel1, author2));

            // when
            int updatedCount = messageRepository.nullifyAuthorByUserId(author1.getId());

            // then
            assertThat(updatedCount).isEqualTo(2);

            entityManager.flush();
            entityManager.clear();

            Message updated1 = messageRepository.findById(msg1.getId()).orElseThrow();
            Message updated2 = messageRepository.findById(msg2.getId()).orElseThrow();
            Message notUpdated = messageRepository.findById(otherAuthorMsg.getId()).orElseThrow();

            assertThat(updated1.getAuthor()).isNull();
            assertThat(updated2.getAuthor()).isNull();
            assertThat(notUpdated.getAuthor()).isNotNull();
        }

        @Test
        @DisplayName("해당 사용자 메시지 없으면 0 반환")
        void returnsZero_whenNoMessages() {
            // when
            int updatedCount = messageRepository.nullifyAuthorByUserId(UUID.randomUUID());

            // then
            assertThat(updatedCount).isZero();
        }
    }

    @Nested
    @DisplayName("deleteAllByChannelId")
    class DeleteAllByChannelId {

        @Test
        @DisplayName("채널의 모든 메시지 삭제 성공")
        void deletesAllMessagesInChannel() {
            // given
            messageRepository.save(new Message("msg1", channel1, author1));
            messageRepository.save(new Message("msg2", channel1, author2));
            messageRepository.save(new Message("other channel", channel2, author1));

            // when
            int deletedCount = messageRepository.deleteAllByChannelId(channel1.getId());

            // then
            assertThat(deletedCount).isEqualTo(2);
            assertThat(messageRepository.findIdsByChannelId(channel1.getId())).isEmpty();
            assertThat(messageRepository.findIdsByChannelId(channel2.getId())).hasSize(1);
        }

        @Test
        @DisplayName("메시지 없으면 0 반환")
        void returnsZero_whenNoMessages() {
            // when
            int deletedCount = messageRepository.deleteAllByChannelId(channel1.getId());

            // then
            assertThat(deletedCount).isZero();
        }
    }
}
