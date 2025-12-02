package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.createBinaryContentWithId;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BinaryContentMapper 단위 테스트")
class BinaryContentMapperTest {

    private final BinaryContentMapper binaryContentMapper = new BinaryContentMapper();

    @Test
    @DisplayName("BinaryContent를 DTO로 변환한다")
    void toDto_Success() {
        // given
        UUID contentId = UUID.randomUUID();
        BinaryContent content = createBinaryContentWithId(contentId, "test.png", 1024L, "image/png");

        // when
        BinaryContentDto result = binaryContentMapper.toDto(content);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(contentId);
        assertThat(result.fileName()).isEqualTo("test.png");
        assertThat(result.size()).isEqualTo(1024L);
        assertThat(result.contentType()).isEqualTo("image/png");
    }

    @Test
    @DisplayName("null BinaryContent를 변환하면 null을 반환한다")
    void toDto_NullEntity() {
        // when
        BinaryContentDto result = binaryContentMapper.toDto(null);

        // then
        assertThat(result).isNull();
    }
}
