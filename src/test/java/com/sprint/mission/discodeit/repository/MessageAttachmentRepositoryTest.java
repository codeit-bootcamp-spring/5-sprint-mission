package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@org.springframework.test.context.ActiveProfiles("test")
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
        author = new User("author", "author@example.com", "encoded", null);
        userRepository.save(author);

        channel = new Channel(ChannelType.PUBLIC, "Test Channel", null);
        channelRepository.save(channel);

        // given - 메시지 생성
        message1 = new Message("Message with attachments", channel, author);
        message2 = new Message("Another message", channel, author);
        messageRepository.saveAll(List.of(message1, message2));

        // given - 파일 생성
        file1 = new BinaryContent("image1.png", 1024L, "image/png");
        file2 = new BinaryContent("document.pdf", 2048L, "application/pdf");
        file3 = new BinaryContent("image2.jpg", 1536L, "image/jpeg");
        binaryContentRepository.saveAll(List.of(file1, file2, file3));

        // given - 메시지 첨부파일 생성
        MessageAttachment attachment1 = new MessageAttachment(message1, file1, 0);
        MessageAttachment attachment2 = new MessageAttachment(message1, file2, 1);
        MessageAttachment attachment3 = new MessageAttachment(message2, file3, 0);
        messageAttachmentRepository.saveAll(List.of(attachment1, attachment2, attachment3));
    }

    @Test
    @DisplayName("findAttachmentsByMessageId - 메시지의 첨부파일을 순서대로 조회 성공")
    void findAttachmentsByMessageId_Success() {
        // when
        List<BinaryContent> attachments = messageAttachmentRepository.findAttachmentsByMessageId(
            message1.getId()
        );

        // then
        assertThat(attachments).hasSize(2);
        // orderIndex 순서대로 정렬되어 반환되어야 함
        assertThat(attachments.get(0).getFileName()).isEqualTo("image1.png");
        assertThat(attachments.get(1).getFileName()).isEqualTo("document.pdf");
    }

    @Test
    @DisplayName("findAttachmentsByMessageId - 첨부파일이 없는 메시지는 빈 리스트 반환")
    void findAttachmentsByMessageId_NoAttachments() {
        // given
        Message messageWithoutAttachments = new Message("No attachments", channel, author);
        messageRepository.save(messageWithoutAttachments);

        // when
        List<BinaryContent> attachments = messageAttachmentRepository.findAttachmentsByMessageId(
            messageWithoutAttachments.getId()
        );

        // then
        assertThat(attachments).isEmpty();
    }

    @Test
    @DisplayName("findAttachmentsByMessageId - orderIndex에 따라 정렬됨")
    void findAttachmentsByMessageId_OrderedByIndex() {
        // when
        List<BinaryContent> attachments = messageAttachmentRepository.findAttachmentsByMessageId(
            message1.getId()
        );

        // then
        assertThat(attachments).hasSize(2);
        // orderIndex 0, 1 순서
        assertThat(attachments.get(0)).isEqualTo(file1);
        assertThat(attachments.get(1)).isEqualTo(file2);
    }

    @Test
    @DisplayName("findAllByMessageIn - 여러 메시지의 모든 첨부파일 조회 성공")
    void findAllByMessageIn_Success() {
        // when
        List<MessageAttachment> attachments = messageAttachmentRepository.findAllByMessageIn(
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
    @DisplayName("findAllByMessageIn - 빈 컬렉션으로 조회 시 빈 리스트 반환")
    void findAllByMessageIn_EmptyCollection() {
        // when
        List<MessageAttachment> attachments = messageAttachmentRepository.findAllByMessageIn(
            List.of()
        );

        // then
        assertThat(attachments).isEmpty();
    }

    @Test
    @DisplayName("findAllByMessageIn - 첨부파일이 없는 메시지는 제외됨")
    void findAllByMessageIn_NoAttachments() {
        // given
        Message messageWithoutAttachments = new Message("No attachments", channel, author);
        messageRepository.save(messageWithoutAttachments);

        // when
        List<MessageAttachment> attachments = messageAttachmentRepository.findAllByMessageIn(
            List.of(message1, messageWithoutAttachments)
        );

        // then
        assertThat(attachments).hasSize(2);
        assertThat(attachments).extracting(ma -> ma.getMessage().getId())
            .containsOnly(message1.getId());
    }

    @Test
    @DisplayName("deleteAllByMessageId - 메시지의 모든 첨부파일 삭제 성공")
    void deleteAllByMessageId_Success() {
        // when
        messageAttachmentRepository.deleteAllByMessageId(message1.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        // 검증 - MessageAttachment 삭제 확인
        List<MessageAttachment> remainingAttachments = messageAttachmentRepository.findAll();
        assertThat(remainingAttachments).hasSize(1);
        assertThat(remainingAttachments.get(0).getMessage().getId()).isEqualTo(message2.getId());

        // 검증 - BinaryContent는 orphan cleanup scheduler에 의해 별도로 정리됨
        // MessageAttachment 삭제 시 BinaryContent는 즉시 삭제되지 않음
        List<BinaryContent> remainingFiles = binaryContentRepository.findAll();
        assertThat(remainingFiles).hasSize(3); // 모든 BinaryContent는 여전히 존재
    }

    @Test
    @DisplayName("deleteAllByMessageId - 첨부파일이 없는 메시지 삭제 시 첨부파일 개수 변화 없음")
    void deleteAllByMessageId_NoAttachments() {
        // given
        Message messageWithoutAttachments = new Message("No attachments", channel, author);
        messageRepository.save(messageWithoutAttachments);
        int initialCount = messageAttachmentRepository.findAll().size();

        // when
        messageAttachmentRepository.deleteAllByMessageId(
            messageWithoutAttachments.getId()
        );
        entityManager.flush();
        entityManager.clear();

        // then
        int finalCount = messageAttachmentRepository.findAll().size();
        assertThat(finalCount).isEqualTo(initialCount);
    }

    @Test
    @DisplayName("save - MessageAttachment 생성 성공")
    void save_Success() {
        // given
        Message newMessage = new Message("New message", channel, author);
        messageRepository.save(newMessage);

        BinaryContent newFile = new BinaryContent("newfile.txt", 100L, "text/plain");
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
        Message newMessage = new Message("New message", channel, author);
        messageRepository.save(newMessage);

        BinaryContent file1 = new BinaryContent("file1.txt", 100L, "text/plain");
        BinaryContent file2 = new BinaryContent("file2.txt", 200L, "text/plain");
        binaryContentRepository.saveAll(List.of(file1, file2));

        MessageAttachment attachment1 = new MessageAttachment(newMessage, file1, 0);
        MessageAttachment attachment2 = new MessageAttachment(newMessage, file2, 1);

        // when
        messageAttachmentRepository.saveAll(List.of(attachment1, attachment2));

        // then
        List<BinaryContent> attachments = messageAttachmentRepository.findAttachmentsByMessageId(
            newMessage.getId()
        );

        assertThat(attachments).hasSize(2);
        assertThat(attachments.get(0)).isEqualTo(file1);
        assertThat(attachments.get(1)).isEqualTo(file2);
    }
}
