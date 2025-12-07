package com.sprint.mission.discodeit.message.domain;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.global.config.JpaConfig;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachment;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachmentRepository;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("MessageAttachmentRepository 슬라이스 테스트")
class MessageAttachmentRepositoryTest {

    @Autowired
    private MessageAttachmentRepository messageAttachmentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    private Message message1;
    private Message message2;

    @BeforeEach
    void setUp() {
        User author = userRepository.save(new User("testuser", "test@example.com", "password1234", null));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "General channel"));

        message1 = messageRepository.save(new Message("Message with attachments", channel, author));
        message2 = messageRepository.save(new Message("Another message", channel, author));

        BinaryContent attachment1 = binaryContentRepository.save(new BinaryContent("file1.jpg", 1024, "image/jpeg"));
        BinaryContent attachment2 = binaryContentRepository.save(new BinaryContent("file2.png", 2048, "image/png"));
        BinaryContent attachment3 = binaryContentRepository.save(
            new BinaryContent("file3.pdf", 4096, "application/pdf"));

        messageAttachmentRepository.save(new MessageAttachment(message1, attachment1, 0));
        messageAttachmentRepository.save(new MessageAttachment(message1, attachment2, 1));
        messageAttachmentRepository.save(new MessageAttachment(message2, attachment3, 0));
    }

    @Nested
    @DisplayName("findByMessageIdOrderByOrderIndexAsc")
    class FindByMessageIdOrderByOrderIndexAsc {

        @Test
        @DisplayName("메시지 ID로 첨부파일을 orderIndex 순으로 조회한다")
        void findByMessageIdOrderByOrderIndexAsc_returnsOrderedAttachments() {
            // when
            List<MessageAttachment> attachments =
                messageAttachmentRepository.findByMessageIdOrderByOrderIndexAsc(message1.getId());

            // then
            assertThat(attachments).hasSize(2);
            assertThat(attachments.get(0).getOrderIndex()).isEqualTo(0);
            assertThat(attachments.get(1).getOrderIndex()).isEqualTo(1);
            assertThat(attachments.get(0).getAttachment().getFileName()).isEqualTo("file1.jpg");
            assertThat(attachments.get(1).getAttachment().getFileName()).isEqualTo("file2.png");
        }

        @Test
        @DisplayName("첨부파일이 없는 메시지는 빈 목록을 반환한다")
        void findByMessageIdOrderByOrderIndexAsc_withNoAttachments_returnsEmptyList() {
            // given
            User author = userRepository.findAll().get(0);
            Channel channel = channelRepository.findAll().get(0);
            Message messageWithoutAttachments = messageRepository.save(new Message("No attachments", channel, author));

            // when
            List<MessageAttachment> attachments = messageAttachmentRepository
                .findByMessageIdOrderByOrderIndexAsc(messageWithoutAttachments.getId());

            // then
            assertThat(attachments).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByMessageInOrderByOrderIndexAsc")
    class FindByMessageInOrderByOrderIndexAsc {

        @Test
        @DisplayName("메시지 목록에 해당하는 첨부파일을 조회한다")
        void findByMessageInOrderByOrderIndexAsc_returnsAllAttachments() {
            // when
            List<MessageAttachment> attachments = messageAttachmentRepository.findByMessageInOrderByOrderIndexAsc(
                List.of(message1, message2));

            // then
            assertThat(attachments).hasSize(3);
        }

        @Test
        @DisplayName("첨부파일이 orderIndex 순으로 정렬된다")
        void findByMessageInOrderByOrderIndexAsc_returnsOrderedAttachments() {
            // when
            List<MessageAttachment> attachments = messageAttachmentRepository.findByMessageInOrderByOrderIndexAsc(
                List.of(message1));

            // then
            assertThat(attachments).hasSize(2);
            assertThat(attachments.get(0).getOrderIndex()).isLessThan(attachments.get(1).getOrderIndex());
        }
    }
}
