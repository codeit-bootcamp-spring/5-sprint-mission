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

@DisplayName("Message 단위 테스트")
class MessageTest {

    private Channel channel;
    private User author;

    @BeforeEach
    void setUp() {
        channel = new Channel(ChannelType.PUBLIC, "general", "General channel");
        author = new User("testuser", "test@test.com", "password1234", null);
    }

    @Nested
    @DisplayName("생성자")
    class ConstructorTest {

        @Test
        @DisplayName("유효한 값으로 Message 생성 성공")
        void constructor_withValidValues_createsMessage() {
            // when
            Message message = new Message("Hello, World!", channel, author);

            // then
            assertThat(message.getContent()).isEqualTo("Hello, World!");
            assertThat(message.getChannel()).isEqualTo(channel);
            assertThat(message.getAuthor()).isEqualTo(author);
        }

        @Test
        @DisplayName("null content로 Message 생성 성공")
        void constructor_withNullContent_createsMessage() {
            // when
            Message message = new Message(null, channel, author);

            // then
            assertThat(message.getContent()).isNull();
            assertThat(message.getChannel()).isEqualTo(channel);
            assertThat(message.getAuthor()).isEqualTo(author);
        }

        @Test
        @DisplayName("빈 content로 Message 생성 성공")
        void constructor_withEmptyContent_createsMessage() {
            // when
            Message message = new Message("", channel, author);

            // then
            assertThat(message.getContent()).isEmpty();
        }

        @Test
        @DisplayName("channel이 null이면 예외 발생")
        void constructor_withNullChannel_throwsException() {
            assertThatThrownBy(() -> new Message("content", null, author))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("author가 null이면 예외 발생")
        void constructor_withNullAuthor_throwsException() {
            assertThatThrownBy(() -> new Message("content", channel, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("content가 최대 길이 초과하면 예외 발생")
        void constructor_withTooLongContent_throwsException() {
            // given
            String longContent = "a".repeat(Message.CONTENT_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> new Message(longContent, channel, author))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("content가 최대 길이면 Message 생성 성공")
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
    @DisplayName("update 메서드")
    class UpdateTest {

        @Test
        @DisplayName("content 변경 성공")
        void update_withNewContent_updatesContent() {
            // given
            Message message = new Message("original", channel, author);

            // when
            message.update("updated content");

            // then
            assertThat(message.getContent()).isEqualTo("updated content");
        }

        @Test
        @DisplayName("newContent가 null이면 content 유지")
        void update_withNullContent_keepsOriginalContent() {
            // given
            Message message = new Message("original", channel, author);

            // when
            message.update(null);

            // then
            assertThat(message.getContent()).isEqualTo("original");
        }

        @Test
        @DisplayName("content를 빈 문자열로 변경")
        void update_withEmptyContent_updatesContentToEmpty() {
            // given
            Message message = new Message("original", channel, author);

            // when
            message.update("");

            // then
            assertThat(message.getContent()).isEmpty();
        }

        @Test
        @DisplayName("자기 자신을 반환 (fluent API)")
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
