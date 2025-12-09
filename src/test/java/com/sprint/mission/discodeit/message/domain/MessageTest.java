package com.sprint.mission.discodeit.message.domain;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Message Unit Test")
class MessageTest {

    private Channel channel;
    private User author;

    @BeforeEach
    void setUp() {
        channel = new Channel(ChannelType.PUBLIC, "general", "General channel");
        author = new User("testuser", "test@test.com", "password1234", null);
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTest {

        @Test
        @DisplayName("creates Message with valid values")
        void constructor_withValidValues_createsMessage() {
            // when
            Message message = new Message("Hello, World!", channel, author);

            // then
            assertThat(message.getContent()).isEqualTo("Hello, World!");
            assertThat(message.getChannel()).isEqualTo(channel);
            assertThat(message.getAuthor()).isEqualTo(author);
        }

        @Test
        @DisplayName("creates Message with null content")
        void constructor_withNullContent_createsMessage() {
            // when
            Message message = new Message(null, channel, author);

            // then
            assertThat(message.getContent()).isNull();
            assertThat(message.getChannel()).isEqualTo(channel);
            assertThat(message.getAuthor()).isEqualTo(author);
        }

        @Test
        @DisplayName("creates Message with empty content")
        void constructor_withEmptyContent_createsMessage() {
            // when
            Message message = new Message("", channel, author);

            // then
            assertThat(message.getContent()).isEmpty();
        }

        @Test
        @DisplayName("throws exception when channel is null")
        void constructor_withNullChannel_throwsException() {
            assertThatThrownBy(() -> new Message("content", null, author))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("throws exception when author is null")
        void constructor_withNullAuthor_throwsException() {
            assertThatThrownBy(() -> new Message("content", channel, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("throws exception when content exceeds max length")
        void constructor_withTooLongContent_throwsException() {
            // given
            String longContent = "a".repeat(Message.CONTENT_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> new Message(longContent, channel, author))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("creates Message with max length content")
        void constructor_withMaxLengthContent_createsMessage() {
            // given
            String maxContent = "a".repeat(Message.CONTENT_MAX_LENGTH);

            // when
            Message message = new Message(maxContent, channel, author);

            // then
            assertThat(message.getContent()).hasSize(Message.CONTENT_MAX_LENGTH);
        }
    }

    @Nested
    @DisplayName("update method")
    class UpdateTest {

        @Test
        @DisplayName("updates content successfully")
        void update_withNewContent_updatesContent() {
            // given
            Message message = new Message("original", channel, author);

            // when
            message.update("updated content");

            // then
            assertThat(message.getContent()).isEqualTo("updated content");
        }

        @Test
        @DisplayName("keeps original content when newContent is null")
        void update_withNullContent_keepsOriginalContent() {
            // given
            Message message = new Message("original", channel, author);

            // when
            message.update(null);

            // then
            assertThat(message.getContent()).isEqualTo("original");
        }

        @Test
        @DisplayName("updates content to empty string")
        void update_withEmptyContent_updatesContentToEmpty() {
            // given
            Message message = new Message("original", channel, author);

            // when
            message.update("");

            // then
            assertThat(message.getContent()).isEmpty();
        }

        @Test
        @DisplayName("returns itself (fluent API)")
        void update_returnsItself() {
            // given
            Message message = new Message("original", channel, author);

            // when
            Message result = message.update("new content");

            // then
            assertThat(result).isSameAs(message);
        }
    }
}
