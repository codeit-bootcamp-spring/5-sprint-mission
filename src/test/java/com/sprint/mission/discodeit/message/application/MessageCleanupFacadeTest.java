package com.sprint.mission.discodeit.message.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.message.domain.attachment.MessageAttachmentRepository;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageCleanupFacade 단위 테스트")
class MessageCleanupFacadeTest {

    @Mock
    private MessageAttachmentRepository messageAttachmentRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @InjectMocks
    private MessageCleanupFacade messageCleanupFacade;

    @Nested
    @DisplayName("cleanup")
    class Cleanup {

        @Test
        @DisplayName("첨부파일이 있으면 MessageAttachment와 BinaryContent 삭제")
        void cleanup_withAttachments_deletesAttachmentsAndBinaryContents() {
            // given
            UUID messageId = UUID.randomUUID();
            UUID attachmentId1 = UUID.randomUUID();
            UUID attachmentId2 = UUID.randomUUID();
            Set<UUID> attachmentIds = Set.of(attachmentId1, attachmentId2);
            MessageDeletedEvent event = new MessageDeletedEvent(messageId);

            given(messageAttachmentRepository.findAttachmentIdSetByMessageId(messageId))
                .willReturn(attachmentIds);
            given(messageAttachmentRepository.deleteAllByAttachmentIdIn(attachmentIds))
                .willReturn(2);

            // when
            messageCleanupFacade.cleanup(event);

            // then
            then(messageAttachmentRepository).should().findAttachmentIdSetByMessageId(messageId);
            then(messageAttachmentRepository).should().deleteAllByAttachmentIdIn(attachmentIds);
            then(binaryContentRepository).should().deleteAllByIdInBatch(attachmentIds);
        }

        @Test
        @DisplayName("첨부파일이 없으면 삭제 작업 수행하지 않음")
        void cleanup_withNoAttachments_skipsDeleteOperations() {
            // given
            UUID messageId = UUID.randomUUID();
            MessageDeletedEvent event = new MessageDeletedEvent(messageId);

            given(messageAttachmentRepository.findAttachmentIdSetByMessageId(messageId))
                .willReturn(Set.of());

            // when
            messageCleanupFacade.cleanup(event);

            // then
            then(messageAttachmentRepository).should().findAttachmentIdSetByMessageId(messageId);
            then(messageAttachmentRepository).should(never()).deleteAllByAttachmentIdIn(Set.of());
            then(binaryContentRepository).should(never()).deleteAllByIdInBatch(Set.of());
        }

        @Test
        @DisplayName("첨부파일 조회 중 예외 발생 시 예외 전파")
        void cleanup_whenFindAttachmentsFails_throwsException() {
            // given
            UUID messageId = UUID.randomUUID();
            MessageDeletedEvent event = new MessageDeletedEvent(messageId);
            RuntimeException exception = new RuntimeException("Database error");

            given(messageAttachmentRepository.findAttachmentIdSetByMessageId(messageId))
                .willThrow(exception);

            // when & then
            assertThatThrownBy(() -> messageCleanupFacade.cleanup(event))
                .isInstanceOf(RuntimeException.class);

            then(messageAttachmentRepository).should(never()).deleteAllByAttachmentIdIn(Set.of());
            then(binaryContentRepository).should(never()).deleteAllByIdInBatch(Set.of());
        }

        @Test
        @DisplayName("MessageAttachment 삭제 중 예외 발생 시 예외 전파")
        void cleanup_whenDeleteAttachmentsFails_throwsException() {
            // given
            UUID messageId = UUID.randomUUID();
            UUID attachmentId = UUID.randomUUID();
            Set<UUID> attachmentIds = Set.of(attachmentId);
            MessageDeletedEvent event = new MessageDeletedEvent(messageId);
            RuntimeException exception = new RuntimeException("Delete failed");

            given(messageAttachmentRepository.findAttachmentIdSetByMessageId(messageId))
                .willReturn(attachmentIds);
            given(messageAttachmentRepository.deleteAllByAttachmentIdIn(attachmentIds))
                .willThrow(exception);

            // when & then
            assertThatThrownBy(() -> messageCleanupFacade.cleanup(event))
                .isInstanceOf(RuntimeException.class);

            then(binaryContentRepository).should(never()).deleteAllByIdInBatch(attachmentIds);
        }

        @Test
        @DisplayName("BinaryContent 삭제 중 예외 발생 시 예외 전파")
        void cleanup_whenDeleteBinaryContentsFails_throwsException() {
            // given
            UUID messageId = UUID.randomUUID();
            UUID attachmentId = UUID.randomUUID();
            Set<UUID> attachmentIds = Set.of(attachmentId);
            MessageDeletedEvent event = new MessageDeletedEvent(messageId);
            RuntimeException exception = new RuntimeException("BinaryContent delete failed");

            given(messageAttachmentRepository.findAttachmentIdSetByMessageId(messageId))
                .willReturn(attachmentIds);
            given(messageAttachmentRepository.deleteAllByAttachmentIdIn(attachmentIds))
                .willReturn(1);
            willThrow(exception).given(binaryContentRepository).deleteAllByIdInBatch(attachmentIds);

            // when & then
            assertThatThrownBy(() -> messageCleanupFacade.cleanup(event))
                .isInstanceOf(RuntimeException.class);
        }
    }
}
