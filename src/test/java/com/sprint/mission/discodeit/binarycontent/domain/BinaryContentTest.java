package com.sprint.mission.discodeit.binarycontent.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BinaryContent 단위 테스트")
class BinaryContentTest {

    @Nested
    @DisplayName("생성자")
    class ConstructorTest {

        @Test
        @DisplayName("유효한 값으로 BinaryContent 생성 성공")
        void constructor_withValidValues_createsBinaryContent() {
            // when
            BinaryContent content = new BinaryContent("test.txt", 1024L, "text/plain");

            // then
            assertThat(content.getFileName()).isEqualTo("test.txt");
            assertThat(content.getSize()).isEqualTo(1024L);
            assertThat(content.getContentType()).isEqualTo("text/plain");
            assertThat(content.getStatus()).isEqualTo(BinaryContentStatus.PROCESSING);
        }

        @Test
        @DisplayName("size가 0이어도 생성 성공")
        void constructor_withZeroSize_createsBinaryContent() {
            // when
            BinaryContent content = new BinaryContent("empty.txt", 0L, "text/plain");

            // then
            assertThat(content.getSize()).isZero();
        }

        @Test
        @DisplayName("fileName이 blank이면 예외 발생")
        void constructor_withBlankFileName_throwsException() {
            assertThatThrownBy(() -> new BinaryContent("", 100L, "text/plain"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("fileName이 null이면 예외 발생")
        void constructor_withNullFileName_throwsException() {
            assertThatThrownBy(() -> new BinaryContent(null, 100L, "text/plain"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("contentType이 null이면 예외 발생")
        void constructor_withNullContentType_throwsException() {
            assertThatThrownBy(() -> new BinaryContent("test.txt", 100L, null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("contentType이 최대 길이 초과하면 예외 발생")
        void constructor_withTooLongContentType_throwsException() {
            // given
            String longContentType = "a".repeat(101);

            // when & then
            assertThatThrownBy(() -> new BinaryContent("test.txt", 100L, longContentType))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("contentType이 정확히 최대 길이면 생성 성공")
        void constructor_withMaxLengthContentType_createsBinaryContent() {
            // given
            String maxContentType = "a".repeat(100);

            // when
            BinaryContent content = new BinaryContent("test.txt", 100L, maxContentType);

            // then
            assertThat(content.getContentType()).hasSize(100);
        }
    }

    @Nested
    @DisplayName("updateStatus 메서드")
    class UpdateStatusTest {

        @Test
        @DisplayName("상태를 SUCCESS로 변경 성공")
        void updateStatus_toSuccess_updatesStatus() {
            // given
            BinaryContent content = new BinaryContent("test.txt", 100L, "text/plain");

            // when
            content.updateStatus(BinaryContentStatus.SUCCESS);

            // then
            assertThat(content.getStatus()).isEqualTo(BinaryContentStatus.SUCCESS);
        }

        @Test
        @DisplayName("상태를 FAIL로 변경 성공")
        void updateStatus_toFail_updatesStatus() {
            // given
            BinaryContent content = new BinaryContent("test.txt", 100L, "text/plain");

            // when
            content.updateStatus(BinaryContentStatus.FAIL);

            // then
            assertThat(content.getStatus()).isEqualTo(BinaryContentStatus.FAIL);
        }

        @Test
        @DisplayName("newStatus가 null이면 상태 유지")
        void updateStatus_withNull_keepsOriginalStatus() {
            // given
            BinaryContent content = new BinaryContent("test.txt", 100L, "text/plain");

            // when
            content.updateStatus(null);

            // then
            assertThat(content.getStatus()).isEqualTo(BinaryContentStatus.PROCESSING);
        }

        @Test
        @DisplayName("updateStatus는 자기 자신을 반환 (fluent API)")
        void updateStatus_returnsItself() {
            // given
            BinaryContent content = new BinaryContent("test.txt", 100L, "text/plain");

            // when
            BinaryContent result = content.updateStatus(BinaryContentStatus.SUCCESS);

            // then
            assertThat(result).isSameAs(content);
        }
    }
}
