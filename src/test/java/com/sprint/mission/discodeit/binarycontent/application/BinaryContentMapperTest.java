package com.sprint.mission.discodeit.binarycontent.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BinaryContentMapper 단위 테스트")
class BinaryContentMapperTest {

    private final BinaryContentMapper mapper = new BinaryContentMapper();

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TEST_FILE_NAME = "test-file.png";
    private static final long TEST_SIZE = 1024L;
    private static final String TEST_CONTENT_TYPE = "image/png";

    @Test
    @DisplayName("BinaryContent를 BinaryContentDto로 변환 성공")
    void toDto_withValidEntity_returnsDto() {
        // given
        BinaryContent entity = new BinaryContent(TEST_FILE_NAME, TEST_SIZE, TEST_CONTENT_TYPE);
        ReflectionTestUtils.setField(entity, "id", TEST_ID);

        // when
        BinaryContentDto result = mapper.toDto(entity);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_ID);
        assertThat(result.fileName()).isEqualTo(TEST_FILE_NAME);
        assertThat(result.size()).isEqualTo(TEST_SIZE);
        assertThat(result.contentType()).isEqualTo(TEST_CONTENT_TYPE);
        assertThat(result.status()).isEqualTo(BinaryContentStatus.PROCESSING);
    }

    @Test
    @DisplayName("null 입력 시 null 반환")
    void toDto_withNull_returnsNull() {
        // when
        BinaryContentDto result = mapper.toDto(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("SUCCESS 상태의 BinaryContent 변환 성공")
    void toDto_withSuccessStatus_returnsDto() {
        // given
        BinaryContent entity = new BinaryContent(TEST_FILE_NAME, TEST_SIZE, TEST_CONTENT_TYPE);
        ReflectionTestUtils.setField(entity, "id", TEST_ID);
        entity.updateStatus(BinaryContentStatus.SUCCESS);

        // when
        BinaryContentDto result = mapper.toDto(entity);

        // then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(BinaryContentStatus.SUCCESS);
    }

    @Test
    @DisplayName("FAIL 상태의 BinaryContent 변환 성공")
    void toDto_withFailStatus_returnsDto() {
        // given
        BinaryContent entity = new BinaryContent(TEST_FILE_NAME, TEST_SIZE, TEST_CONTENT_TYPE);
        ReflectionTestUtils.setField(entity, "id", TEST_ID);
        entity.updateStatus(BinaryContentStatus.FAIL);

        // when
        BinaryContentDto result = mapper.toDto(entity);

        // then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(BinaryContentStatus.FAIL);
    }
}
