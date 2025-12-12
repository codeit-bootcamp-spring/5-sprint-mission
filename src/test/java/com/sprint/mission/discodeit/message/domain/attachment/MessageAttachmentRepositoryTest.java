package com.sprint.mission.discodeit.message.domain.attachment;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.global.config.JpaConfig;
import com.sprint.mission.discodeit.message.domain.Message;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("MessageAttachmentRepository 슬라이스 테스트")
@ActiveProfiles("test")
class MessageAttachmentRepositoryTest {

    @Autowired
    private MessageAttachmentRepository messageAttachmentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private Message message1;
    private Message message2;
    private BinaryContent attachment1;
    private BinaryContent attachment2;
    private BinaryContent attachment3;

    @BeforeEach
    void setUp() {
        User author = userRepository.save(
            new User("testuser", "test@test.com", "password1234", null));
        Channel channel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "general", "General channel"));
        message1 = messageRepository.save(new Message("message1", channel, author));
        message2 = messageRepository.save(new Message("message2", channel, author));

        attachment1 = binaryContentRepository.save(
            new BinaryContent("file1.txt", 100L, "text/plain"));
        attachment2 = binaryContentRepository.save(
            new BinaryContent("file2.txt", 200L, "text/plain"));
        attachment3 = binaryContentRepository.save(
            new BinaryContent("file3.txt", 300L, "text/plain"));
    }

    @Nested
    @DisplayName("findAllWithAttachmentByMessageIdOrderByOrderIndexAsc")
    class FindAllWithAttachmentByMessageIdOrderByOrderIndexAsc {

        @Test
        @DisplayName("orderIndex 오름차순 정렬")
        void returnsAttachmentsOrderedByOrderIndex() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment3, 2));
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment2, 1));

            entityManager.flush();
            entityManager.clear();

            // when
            List<MessageAttachment> result = messageAttachmentRepository
                .findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(message1.getId());

            // then
            assertThat(result).hasSize(3);
            assertThat(result).extracting(ma -> ma.getAttachment().getId())
                .containsExactly(attachment1.getId(), attachment2.getId(), attachment3.getId());
        }

        @Test
        @DisplayName("attachment 페치 조인 (N+1 방지)")
        void fetchesAttachment() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));

            entityManager.flush();
            entityManager.clear();

            // when
            List<MessageAttachment> result = messageAttachmentRepository
                .findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(message1.getId());

            // then
            PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
            assertThat(util.isLoaded(result.get(0).getAttachment()))
                .as("EntityGraph로 Attachment가 로딩되어야 함")
                .isTrue();
        }

        @Test
        @DisplayName("첨부파일 없으면 빈 리스트 반환")
        void returnsEmptyList_whenNoAttachments() {
            // when
            List<MessageAttachment> result = messageAttachmentRepository
                .findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(message1.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("다른 메시지 첨부파일 제외")
        void excludesOtherMessageAttachments() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment2, 0));

            // when
            List<MessageAttachment> result = messageAttachmentRepository
                .findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(message1.getId());

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAttachment().getId()).isEqualTo(attachment1.getId());
        }
    }

    @Nested
    @DisplayName("findAllWithAttachmentByMessageIdInOrderByOrderIndexAsc")
    class FindAllWithAttachmentByMessageIdInOrderByOrderIndexAsc {

        @Test
        @DisplayName("여러 메시지의 첨부파일 조회")
        void returnsAttachmentsForMultipleMessages() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment2, 1));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment3, 0));

            entityManager.flush();
            entityManager.clear();

            // when
            List<MessageAttachment> result = messageAttachmentRepository
                .findAllWithAttachmentByMessageIdInOrderByOrderIndexAsc(
                    List.of(message1.getId(), message2.getId()));

            // then
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("빈 메시지 ID 목록이면 빈 리스트 반환")
        void returnsEmptyList_whenEmptyMessageIdList() {
            // when
            List<MessageAttachment> result = messageAttachmentRepository
                .findAllWithAttachmentByMessageIdInOrderByOrderIndexAsc(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("attachment 페치 조인 (N+1 방지)")
        void fetchesAttachment() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment2, 0));

            entityManager.flush();
            entityManager.clear();

            // when
            List<MessageAttachment> result = messageAttachmentRepository
                .findAllWithAttachmentByMessageIdInOrderByOrderIndexAsc(
                    List.of(message1.getId(), message2.getId()));

            // then
            PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
            for (MessageAttachment ma : result) {
                assertThat(util.isLoaded(ma.getAttachment()))
                    .as("EntityGraph로 Attachment가 로딩되어야 함")
                    .isTrue();
            }
        }
    }

    @Nested
    @DisplayName("findAttachmentIdSetByMessageIdIn")
    class FindIdSetByMessageIdIn {

        @Test
        @DisplayName("주어진 메시지 ID들의 첨부파일 ID 반환")
        void returnsAttachmentIds() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment2, 1));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment3, 0));

            // when
            Set<UUID> result = messageAttachmentRepository
                .findAttachmentIdSetByMessageIdIn(List.of(message1.getId(), message2.getId()));

            // then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyInAnyOrder(
                attachment1.getId(), attachment2.getId(), attachment3.getId());
        }

        @Test
        @DisplayName("첨부파일 없으면 빈 Set 반환")
        void returnsEmptySet_whenNoAttachments() {
            // when
            Set<UUID> result = messageAttachmentRepository
                .findAttachmentIdSetByMessageIdIn(List.of(message1.getId()));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 메시지 ID 목록이면 빈 Set 반환")
        void returnsEmptySet_whenEmptyMessageIdList() {
            // when
            Set<UUID> result = messageAttachmentRepository.findAttachmentIdSetByMessageIdIn(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAttachmentIdSetByMessageId")
    class FindAttachmentIdSetByMessageId {

        @Test
        @DisplayName("단일 메시지의 첨부파일 ID 반환")
        void returnsAttachmentIds() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment2, 1));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment3, 0));

            // when
            Set<UUID> result = messageAttachmentRepository.findAttachmentIdSetByMessageId(message1.getId());

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder(attachment1.getId(), attachment2.getId());
        }

        @Test
        @DisplayName("첨부파일 없으면 빈 Set 반환")
        void returnsEmptySet_whenNoAttachments() {
            // when
            Set<UUID> result = messageAttachmentRepository.findAttachmentIdSetByMessageId(message1.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("다른 메시지의 첨부파일 제외")
        void excludesOtherMessageAttachments() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment2, 0));

            // when
            Set<UUID> result = messageAttachmentRepository.findAttachmentIdSetByMessageId(message1.getId());

            // then
            assertThat(result).hasSize(1);
            assertThat(result).containsExactly(attachment1.getId());
        }
    }

    @Nested
    @DisplayName("deleteAllByMessageIdIn")
    class DeleteAllByMessageIdIn {

        @Test
        @DisplayName("삭제된 개수 반환 및 영속성 컨텍스트 자동 초기화(clear) 검증")
        void deletesAttachments() {
            // given
            MessageAttachment ma1 = messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment2, 1));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment3, 0));

            // when
            int deletedCount = messageAttachmentRepository
                .deleteAllByMessageIdIn(List.of(message1.getId()));

            // then
            assertThat(deletedCount).isEqualTo(2);

            List<MessageAttachment> remaining = messageAttachmentRepository
                .findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(message2.getId());
            assertThat(remaining).hasSize(1);

            Optional<MessageAttachment> deletedMa = messageAttachmentRepository.findById(ma1.getId());
            assertThat(deletedMa).isEmpty();
        }

        @Test
        @DisplayName("삭제할 첨부파일 없으면 0 반환")
        void returnsZero_whenNoAttachments() {
            // when
            int deletedCount = messageAttachmentRepository
                .deleteAllByMessageIdIn(List.of(message1.getId()));

            // then
            assertThat(deletedCount).isZero();
        }

        @Test
        @DisplayName("빈 메시지 ID 목록이면 0 반환")
        void returnsZero_whenEmptyMessageIdList() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));

            // when
            int deletedCount = messageAttachmentRepository.deleteAllByMessageIdIn(List.of());

            // then
            assertThat(deletedCount).isZero();
        }
    }

    @Nested
    @DisplayName("deleteAllByAttachmentIdIn")
    class DeleteAllByAttachmentIdIn {

        @Test
        @DisplayName("첨부파일 ID로 삭제 및 삭제된 개수 반환")
        void deletesAttachmentsByAttachmentId() {
            // given
            MessageAttachment ma1 = messageAttachmentRepository.save(
                new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment2, 1));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment3, 0));

            // when
            int deletedCount = messageAttachmentRepository
                .deleteAllByAttachmentIdIn(Set.of(attachment1.getId(), attachment2.getId()));

            // then
            assertThat(deletedCount).isEqualTo(2);

            Optional<MessageAttachment> deletedMa = messageAttachmentRepository.findById(ma1.getId());
            assertThat(deletedMa).isEmpty();

            List<MessageAttachment> remaining = messageAttachmentRepository
                .findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(message2.getId());
            assertThat(remaining).hasSize(1);
            assertThat(remaining.get(0).getAttachment().getId()).isEqualTo(attachment3.getId());
        }

        @Test
        @DisplayName("삭제할 첨부파일 없으면 0 반환")
        void returnsZero_whenNoMatchingAttachments() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));

            // when
            int deletedCount = messageAttachmentRepository
                .deleteAllByAttachmentIdIn(Set.of(UUID.randomUUID()));

            // then
            assertThat(deletedCount).isZero();
        }

        @Test
        @DisplayName("빈 첨부파일 ID 목록이면 0 반환")
        void returnsZero_whenEmptyAttachmentIdList() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));

            // when
            int deletedCount = messageAttachmentRepository.deleteAllByAttachmentIdIn(Set.of());

            // then
            assertThat(deletedCount).isZero();
        }

        @Test
        @DisplayName("여러 메시지에 걸친 동일 첨부파일도 삭제")
        void deletesAcrossMultipleMessages() {
            // given - 같은 첨부파일을 여러 메시지에서 사용 (실제로는 드문 케이스지만 테스트)
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment2, 1));

            // when
            int deletedCount = messageAttachmentRepository
                .deleteAllByAttachmentIdIn(Set.of(attachment1.getId()));

            // then
            assertThat(deletedCount).isEqualTo(2);

            Set<UUID> remainingIds = messageAttachmentRepository
                .findAttachmentIdSetByMessageIdIn(List.of(message1.getId(), message2.getId()));
            assertThat(remainingIds).containsExactly(attachment2.getId());
        }
    }
}
