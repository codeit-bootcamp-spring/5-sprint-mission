package com.sprint.mission.discodeit.channel.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Channel 단위 테스트")
class ChannelTest {

    @Nested
    @DisplayName("생성자")
    class ConstructorTest {

        @Test
        @DisplayName("PUBLIC 채널 생성 성공")
        void constructor_withPublicType_createsChannel() {
            // when
            Channel channel = new Channel(ChannelType.PUBLIC, "general", "General channel");

            // then
            assertThat(channel.getType()).isEqualTo(ChannelType.PUBLIC);
            assertThat(channel.getName()).isEqualTo("general");
            assertThat(channel.getDescription()).isEqualTo("General channel");
        }

        @Test
        @DisplayName("PUBLIC 채널은 description이 null이어도 생성 성공")
        void constructor_withPublicTypeAndNullDescription_createsChannel() {
            // when
            Channel channel = new Channel(ChannelType.PUBLIC, "general", null);

            // then
            assertThat(channel.getType()).isEqualTo(ChannelType.PUBLIC);
            assertThat(channel.getName()).isEqualTo("general");
            assertThat(channel.getDescription()).isNull();
        }

        @Test
        @DisplayName("PRIVATE 채널 생성 성공")
        void constructor_withPrivateType_createsChannel() {
            // when
            Channel channel = new Channel(ChannelType.PRIVATE, null, null);

            // then
            assertThat(channel.getType()).isEqualTo(ChannelType.PRIVATE);
            assertThat(channel.getName()).isNull();
            assertThat(channel.getDescription()).isNull();
        }

        @Test
        @DisplayName("type이 null이면 예외 발생")
        @SuppressWarnings("DataFlowIssue")
        void constructor_withNullType_throwsException() {
            assertThatThrownBy(() -> new Channel(null, "name", "desc"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("PUBLIC 채널에서 name이 blank이면 예외 발생")
        void constructor_withPublicTypeAndBlankName_throwsException() {
            assertThatThrownBy(() -> new Channel(ChannelType.PUBLIC, "", "desc"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("PUBLIC 채널에서 name이 null이면 예외 발생")
        void constructor_withPublicTypeAndNullName_throwsException() {
            assertThatThrownBy(() -> new Channel(ChannelType.PUBLIC, null, "desc"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("name이 최대 길이 초과하면 예외 발생")
        void constructor_withTooLongName_throwsException() {
            // given
            String longName = "a".repeat(Channel.NAME_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> new Channel(ChannelType.PUBLIC, longName, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("description이 최대 길이 초과하면 예외 발생")
        void constructor_withTooLongDescription_throwsException() {
            // given
            String longDescription = "a".repeat(Channel.DESCRIPTION_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> new Channel(ChannelType.PUBLIC, "name", longDescription))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("update 메서드")
    class UpdateTest {

        @Test
        @DisplayName("name과 description 모두 변경 성공")
        void update_withNewNameAndDescription_updatesChannel() {
            // given
            Channel channel = new Channel(ChannelType.PUBLIC, "old-name", "old-desc");

            // when
            channel.update("new-name", "new-desc");

            // then
            assertThat(channel.getName()).isEqualTo("new-name");
            assertThat(channel.getDescription()).isEqualTo("new-desc");
        }

        @Test
        @DisplayName("newName이 blank이면 name 유지")
        void update_withBlankName_keepsOriginalName() {
            // given
            Channel channel = new Channel(ChannelType.PUBLIC, "original", "desc");

            // when
            channel.update("", "new-desc");

            // then
            assertThat(channel.getName()).isEqualTo("original");
            assertThat(channel.getDescription()).isEqualTo("new-desc");
        }

        @Test
        @DisplayName("newName이 null이면 name 유지")
        void update_withNullName_keepsOriginalName() {
            // given
            Channel channel = new Channel(ChannelType.PUBLIC, "original", "desc");

            // when
            channel.update(null, "new-desc");

            // then
            assertThat(channel.getName()).isEqualTo("original");
            assertThat(channel.getDescription()).isEqualTo("new-desc");
        }

        @Test
        @DisplayName("newDescription이 null이면 description 유지")
        void update_withNullDescription_keepsOriginalDescription() {
            // given
            Channel channel = new Channel(ChannelType.PUBLIC, "name", "original-desc");

            // when
            channel.update("new-name", null);

            // then
            assertThat(channel.getName()).isEqualTo("new-name");
            assertThat(channel.getDescription()).isEqualTo("original-desc");
        }

        @Test
        @DisplayName("newDescription이 빈 문자열이면 description을 빈 문자열로 변경")
        void update_withEmptyDescription_updatesDescriptionToEmpty() {
            // given
            Channel channel = new Channel(ChannelType.PUBLIC, "name", "original-desc");

            // when
            channel.update("name", "");

            // then
            assertThat(channel.getDescription()).isEmpty();
        }

        @Test
        @DisplayName("newName이 최대 길이 초과하면 예외 발생")
        void update_withTooLongName_throwsException() {
            // given
            Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");
            String longName = "a".repeat(Channel.NAME_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> channel.update(longName, "desc"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("newDescription이 최대 길이 초과하면 예외 발생")
        void update_withTooLongDescription_throwsException() {
            // given
            Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");
            String longDescription = "a".repeat(Channel.DESCRIPTION_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> channel.update("name", longDescription))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("update는 자기 자신을 반환 (fluent API)")
        void update_returnsItself() {
            // given
            Channel channel = new Channel(ChannelType.PUBLIC, "name", "desc");

            // when
            Channel result = channel.update("new-name", "new-desc");

            // then
            assertThat(result).isSameAs(channel);
        }
    }
}
