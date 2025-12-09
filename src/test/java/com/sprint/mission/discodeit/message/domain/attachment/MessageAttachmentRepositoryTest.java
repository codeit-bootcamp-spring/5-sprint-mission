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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("MessageAttachmentRepository Slice Test")
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
        @DisplayName("orderIndex ascending order")
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
            assertThat(result.get(0).getOrderIndex()).isZero();
            assertThat(result.get(1).getOrderIndex()).isEqualTo(1);
            assertThat(result.get(2).getOrderIndex()).isEqualTo(2);
        }

        @Test
        @DisplayName("attachment fetch join (N+1 prevention)")
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
                .as("Attachment should be loaded via EntityGraph")
                .isTrue();
        }

        @Test
        @DisplayName("returns empty list when no attachments")
        void returnsEmptyList_whenNoAttachments() {
            // when
            List<MessageAttachment> result = messageAttachmentRepository
                .findAllWithAttachmentByMessageIdOrderByOrderIndexAsc(message1.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("excludes other message attachments")
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
    @DisplayName("findAllWithAttachmentByMessageInOrderByOrderIndexAsc")
    class FindAllWithAttachmentByMessageInOrderByOrderIndexAsc {

        @Test
        @DisplayName("returns attachments for multiple messages")
        void returnsAttachmentsForMultipleMessages() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment2, 1));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment3, 0));

            entityManager.flush();
            entityManager.clear();

            // when
            List<MessageAttachment> result = messageAttachmentRepository
                .findAllWithAttachmentByMessageInOrderByOrderIndexAsc(List.of(message1, message2));

            // then
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("returns empty list when empty message list")
        void returnsEmptyList_whenEmptyMessageList() {
            // when
            List<MessageAttachment> result = messageAttachmentRepository
                .findAllWithAttachmentByMessageInOrderByOrderIndexAsc(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("attachment fetch join (N+1 prevention)")
        void fetchesAttachment() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
            messageAttachmentRepository.save(new MessageAttachment(message2, attachment2, 0));

            entityManager.flush();
            entityManager.clear();

            // when
            List<MessageAttachment> result = messageAttachmentRepository
                .findAllWithAttachmentByMessageInOrderByOrderIndexAsc(List.of(message1, message2));

            // then
            PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
            for (MessageAttachment ma : result) {
                assertThat(util.isLoaded(ma.getAttachment()))
                    .as("Attachment should be loaded via EntityGraph")
                    .isTrue();
            }
        }
    }

    @Nested
    @DisplayName("findAttachmentIdSetByMessageIdIn")
    class FindIdSetByMessageIdIn {

        @Test
        @DisplayName("returns attachment IDs for given message IDs")
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
        @DisplayName("returns empty set when no attachments")
        void returnsEmptySet_whenNoAttachments() {
            // when
            Set<UUID> result = messageAttachmentRepository
                .findAttachmentIdSetByMessageIdIn(List.of(message1.getId()));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty set when empty message ID list")
        void returnsEmptySet_whenEmptyMessageIdList() {
            // when
            Set<UUID> result = messageAttachmentRepository.findAttachmentIdSetByMessageIdIn(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteAllByMessageIdIn")
    class DeleteAllByMessageIdIn {

        @Test
        @DisplayName("deletes all attachments for given message IDs")
        void deletesAttachments() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
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
        }

        @Test
        @DisplayName("returns 0 when no attachments to delete")
        void returnsZero_whenNoAttachments() {
            // when
            int deletedCount = messageAttachmentRepository
                .deleteAllByMessageIdIn(List.of(message1.getId()));

            // then
            assertThat(deletedCount).isZero();
        }

        @Test
        @DisplayName("returns 0 when empty message ID list")
        void returnsZero_whenEmptyMessageIdList() {
            // given
            messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));

            // when
            int deletedCount = messageAttachmentRepository.deleteAllByMessageIdIn(List.of());

            // then
            assertThat(deletedCount).isZero();
        }
    }
}
