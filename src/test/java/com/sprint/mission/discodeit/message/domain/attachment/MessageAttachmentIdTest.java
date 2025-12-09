package com.sprint.mission.discodeit.message.domain.attachment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MessageAttachmentId 단위 테스트")
class MessageAttachmentIdTest {

    @Nested
    @DisplayName("생성자")
    @SuppressWarnings("DataFlowIssue")
    class ConstructorTest {

        @Test
        @DisplayName("유효한 UUID로 MessageAttachmentId 생성 성공")
        void constructor_withValidUuids_createsMessageAttachmentId() {
            // given
            UUID messageId = UUID.randomUUID();
            UUID attachmentId = UUID.randomUUID();

            // when
            MessageAttachmentId id = new MessageAttachmentId(messageId, attachmentId);

            // then
            assertThat(id.getMessageId()).isEqualTo(messageId);
            assertThat(id.getAttachmentId()).isEqualTo(attachmentId);
        }

        @Test
        @DisplayName("messageId가 null이면 예외 발생")
        void constructor_withNullMessageId_throwsException() {
            // given
            UUID attachmentId = UUID.randomUUID();

            // when & then
            assertThatThrownBy(() -> new MessageAttachmentId(null, attachmentId))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("attachmentId가 null이면 예외 발생")
        void constructor_withNullAttachmentId_throwsException() {
            // given
            UUID messageId = UUID.randomUUID();

            // when & then
            assertThatThrownBy(() -> new MessageAttachmentId(messageId, null))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("equals와 hashCode")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 messageId와 attachmentId면 동등")
        void equals_withSameIds_returnsTrue() {
            // given
            UUID messageId = UUID.randomUUID();
            UUID attachmentId = UUID.randomUUID();

            MessageAttachmentId id1 = new MessageAttachmentId(messageId, attachmentId);
            MessageAttachmentId id2 = new MessageAttachmentId(messageId, attachmentId);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 messageId면 동등하지 않음")
        void equals_withDifferentMessageId_returnsFalse() {
            // given
            UUID attachmentId = UUID.randomUUID();

            MessageAttachmentId id1 = new MessageAttachmentId(UUID.randomUUID(), attachmentId);
            MessageAttachmentId id2 = new MessageAttachmentId(UUID.randomUUID(), attachmentId);

            // then
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("다른 attachmentId면 동등하지 않음")
        void equals_withDifferentAttachmentId_returnsFalse() {
            // given
            UUID messageId = UUID.randomUUID();

            MessageAttachmentId id1 = new MessageAttachmentId(messageId, UUID.randomUUID());
            MessageAttachmentId id2 = new MessageAttachmentId(messageId, UUID.randomUUID());

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
