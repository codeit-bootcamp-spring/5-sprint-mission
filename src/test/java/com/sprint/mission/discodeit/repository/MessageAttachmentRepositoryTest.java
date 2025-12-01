package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.sprint.mission.discodeit.support.TestFixtures.createBinaryContent;
import static com.sprint.mission.discodeit.support.TestFixtures.createMessage;
import static com.sprint.mission.discodeit.support.TestFixtures.createPublicChannel;
import static com.sprint.mission.discodeit.support.TestFixtures.createUser;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
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

    private User author;
    private Channel channel;
    private Message message1;
    private Message message2;
    private BinaryContent file1;
    private BinaryContent file2;
    private BinaryContent file3;

    @BeforeEach
    void setUp() {
        // given - 사용자 및 채널 생성
        author = createUser("author", "author@example.com");
        userRepository.save(author);

        channel = createPublicChannel("Test Channel");
        channelRepository.save(channel);

        // given - 메시지 생성
        message1 = createMessage("Message with attachments", channel, author);
        message2 = createMessage("Another message", channel, author);
        messageRepository.saveAll(List.of(message1, message2));

        // given - 파일 생성
        file1 = createBinaryContent("image1.png", 1024L, "image/png");
        file2 = createBinaryContent("document.pdf", 2048L, "application/pdf");
        file3 = createBinaryContent("image2.jpg", 1536L, "image/jpeg");
        binaryContentRepository.saveAll(List.of(file1, file2, file3));

        // given - 메시지 첨부파일 생성
        MessageAttachment attachment1 = new MessageAttachment(message1, file1, 0);
        MessageAttachment attachment2 = new MessageAttachment(message1, file2, 1);
        MessageAttachment attachment3 = new MessageAttachment(message2, file3, 0);
        messageAttachmentRepository.saveAll(List.of(attachment1, attachment2, attachment3));
    }

    @Test
    @DisplayName("findByMessageIdOrderByOrderIndexAsc - 메시지의 첨부파일을 순서대로 조회 성공")
    void findByMessageIdOrderByOrderIndexAsc_OrderByOrderIndexAsc_Success() {
        // when
        List<BinaryContent> attachments =
            messageAttachmentRepository.findByMessageIdOrderByOrderIndexAsc(message1.getId()).stream()
                .map(MessageAttachment::getAttachment).toList();

        // then
        assertThat(attachments).hasSize(2);
        // orderIndex 순서대로 정렬되어 반환되어야 함
        assertThat(attachments.get(0).getFileName()).isEqualTo("image1.png");
        assertThat(attachments.get(1).getFileName()).isEqualTo("document.pdf");
    }

    @Test
    @DisplayName("findByMessageIdOrderByOrderIndexAsc - 첨부파일이 없는 메시지는 빈 리스트 반환")
    void findByMessageIdOrderByOrderIndexAsc_NoAttachmentsOrderByOrderIndexAsc() {
        // given
        Message messageWithoutAttachments = createMessage("No attachments", channel, author);
        messageRepository.save(messageWithoutAttachments);

        // when
        List<BinaryContent> attachments =
            messageAttachmentRepository.findByMessageIdOrderByOrderIndexAsc(messageWithoutAttachments.getId()).stream()
                .map(MessageAttachment::getAttachment).toList();

        // then
        assertThat(attachments).isEmpty();
    }

    @Test
    @DisplayName("findByMessageIdOrderByOrderIndexAsc - orderIndex에 따라 정렬됨")
    void findByMessageIdOrderByOrderIndexAsc_OrderedByIndexOrderByOrderIndexAsc() {
        // when
        List<BinaryContent> attachments =
            messageAttachmentRepository.findByMessageIdOrderByOrderIndexAsc(message1.getId()).stream()
                .map(MessageAttachment::getAttachment).toList();

        // then
        assertThat(attachments).hasSize(2);
        // orderIndex 0, 1 순서
        assertThat(attachments.get(0)).isEqualTo(file1);
        assertThat(attachments.get(1)).isEqualTo(file2);
    }

    @Test
    @DisplayName("findByMessageInOrderByOrderIndexAsc - 여러 메시지의 모든 첨부파일 조회 성공")
    void findByMessageIn_OrderByOrderIndexAsc_Success() {
        // when
        List<MessageAttachment> attachments = messageAttachmentRepository.findByMessageInOrderByOrderIndexAsc(
            List.of(message1, message2)
        );

        // then
        assertThat(attachments).hasSize(3);

        // MessageAttachment의 복합키로 비교
        assertThat(attachments).extracting(ma -> ma.getMessage().getId())
            .containsExactlyInAnyOrder(message1.getId(), message1.getId(), message2.getId());
        assertThat(attachments).extracting(ma -> ma.getAttachment().getId())
            .containsExactlyInAnyOrder(file1.getId(), file2.getId(), file3.getId());

        // EntityGraph로 attachment가 함께 로드되었는지 확인
        attachments.forEach(ma -> {
            assertThat(ma.getAttachment()).isNotNull();
            assertThat(ma.getAttachment().getFileName()).isNotNull();
        });
    }

    @Test
    @DisplayName("findByMessageInOrderByOrderIndexAsc - 빈 컬렉션으로 조회 시 빈 리스트 반환")
    void findByMessageIn_OrderByOrderIndexAsc_EmptyCollection() {
        // when
        List<MessageAttachment> attachments = messageAttachmentRepository.findByMessageInOrderByOrderIndexAsc(
            List.of()
        );

        // then
        assertThat(attachments).isEmpty();
    }

    @Test
    @DisplayName("findByMessageInOrderByOrderIndexAsc - 첨부파일이 없는 메시지는 제외됨")
    void findByMessageIn_OrderByOrderIndexAsc_NoAttachments() {
        // given
        Message messageWithoutAttachments = createMessage("No attachments", channel, author);
        messageRepository.save(messageWithoutAttachments);

        // when
        List<MessageAttachment> attachments = messageAttachmentRepository.findByMessageInOrderByOrderIndexAsc(
            List.of(message1, messageWithoutAttachments)
        );

        // then
        assertThat(attachments).hasSize(2);
        assertThat(attachments).extracting(ma -> ma.getMessage().getId())
            .containsOnly(message1.getId());
    }

    @Test
    @DisplayName("save - MessageAttachment 생성 성공")
    void save_Success() {
        // given
        Message newMessage = createMessage("New message", channel, author);
        messageRepository.save(newMessage);

        BinaryContent newFile = createBinaryContent("newfile.txt", 100L, "text/plain");
        binaryContentRepository.save(newFile);

        MessageAttachment newAttachment = new MessageAttachment(newMessage, newFile, 0);

        // when
        MessageAttachment savedAttachment = messageAttachmentRepository.save(newAttachment);

        // then
        assertThat(savedAttachment).isNotNull();
        assertThat(savedAttachment.getMessage()).isEqualTo(newMessage);
        assertThat(savedAttachment.getAttachment()).isEqualTo(newFile);
        assertThat(savedAttachment.getOrderIndex()).isEqualTo(0);
    }

    @Test
    @DisplayName("save - 여러 첨부파일을 순서대로 저장")
    void save_MultipleAttachmentsWithOrder() {
        // given
        Message newMessage = createMessage("New message", channel, author);
        messageRepository.save(newMessage);

        BinaryContent file1 = createBinaryContent("file1.txt", 100L, "text/plain");
        BinaryContent file2 = createBinaryContent("file2.txt", 200L, "text/plain");
        binaryContentRepository.saveAll(List.of(file1, file2));

        MessageAttachment attachment1 = new MessageAttachment(newMessage, file1, 0);
        MessageAttachment attachment2 = new MessageAttachment(newMessage, file2, 1);

        // when
        messageAttachmentRepository.saveAll(List.of(attachment1, attachment2));

        // then
        List<BinaryContent> attachments =
            messageAttachmentRepository.findByMessageIdOrderByOrderIndexAsc(newMessage.getId()).stream()
                .map(MessageAttachment::getAttachment).toList();

        assertThat(attachments).hasSize(2);
        assertThat(attachments.get(0)).isEqualTo(file1);
        assertThat(attachments.get(1)).isEqualTo(file2);
    }
}
