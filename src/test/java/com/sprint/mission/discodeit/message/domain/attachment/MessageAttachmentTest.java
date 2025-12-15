package com.sprint.mission.discodeit.message.domain.attachment;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.message.domain.Message;
import com.sprint.mission.discodeit.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MessageAttachment 단위 테스트")
class MessageAttachmentTest {

    private Message message;
    private BinaryContent attachment;

    @BeforeEach
    void setUp() {
        Channel channel = new Channel(ChannelType.PUBLIC, "general", "General channel");
        User author = new User("testuser", "test@test.com", "password1234", null);
        message = new Message("test message", channel, author);
        attachment = new BinaryContent("file.txt", 1024L, "text/plain");
    }

    @Nested
    @DisplayName("생성자")
    @SuppressWarnings("DataFlowIssue")
    class ConstructorTest {

        @Test
        @DisplayName("유효한 값으로 MessageAttachment 생성 성공")
        void constructor_withValidValues_createsMessageAttachment() {
            // when
            MessageAttachment messageAttachment = new MessageAttachment(message, attachment, 0);

            // then
            assertThat(messageAttachment.getMessage()).isEqualTo(message);
            assertThat(messageAttachment.getAttachment()).isEqualTo(attachment);
            assertThat(messageAttachment.getOrderIndex()).isZero();
            assertThat(messageAttachment.getId()).isNotNull();
        }

        @Test
        @DisplayName("다른 orderIndex로 MessageAttachment 생성 성공")
        void constructor_withDifferentOrderIndex_createsMessageAttachment() {
            // when
            MessageAttachment messageAttachment = new MessageAttachment(message, attachment, 5);

            // then
            assertThat(messageAttachment.getOrderIndex()).isEqualTo(5);
        }

        @Test
        @DisplayName("message가 null이면 예외 발생")
        void constructor_withNullMessage_throwsException() {
            assertThatThrownBy(() -> new MessageAttachment(null, attachment, 0))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("attachment가 null이면 예외 발생")
        void constructor_withNullAttachment_throwsException() {
            assertThatThrownBy(() -> new MessageAttachment(message, null, 0))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("생성 시 새 MessageAttachmentId 생성")
        void constructor_createsNewMessageAttachmentId() {
            // when
            MessageAttachment messageAttachment1 = new MessageAttachment(message, attachment, 0);
            MessageAttachment messageAttachment2 = new MessageAttachment(message, attachment, 1);

            // then
            assertThat(messageAttachment1.getId()).isNotNull();
            assertThat(messageAttachment2.getId()).isNotNull();
        }
    }
}
