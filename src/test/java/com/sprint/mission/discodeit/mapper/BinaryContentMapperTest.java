package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BinaryContentMapper 단위 테스트")
class BinaryContentMapperTest {

    private final BinaryContentMapper binaryContentMapper = new BinaryContentMapper();

    private BinaryContent createBinaryContentWithId(UUID id, String fileName, long size, String contentType) {
        BinaryContent content = new BinaryContent(fileName, size, contentType);
        ReflectionTestUtils.setField(content, "id", id);
        return content;
    }

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

    @Test
    @DisplayName("BinaryContent 리스트를 DTO 리스트로 변환한다")
    void toDtoList_Success() {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        BinaryContent content1 = createBinaryContentWithId(id1, "file1.pdf", 2048L, "application/pdf");
        BinaryContent content2 = createBinaryContentWithId(id2, "file2.jpg", 4096L, "image/jpeg");
        List<BinaryContent> contents = List.of(content1, content2);

        // when
        List<BinaryContentDto> result = binaryContentMapper.toDtoList(contents);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(id1);
        assertThat(result.get(0).fileName()).isEqualTo("file1.pdf");
        assertThat(result.get(1).id()).isEqualTo(id2);
        assertThat(result.get(1).fileName()).isEqualTo("file2.jpg");
    }

    @Test
    @DisplayName("null 리스트를 변환하면 빈 리스트를 반환한다")
    void toDtoList_NullList() {
        // when
        List<BinaryContentDto> result = binaryContentMapper.toDtoList(null);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("빈 리스트를 변환하면 빈 리스트를 반환한다")
    void toDtoList_EmptyList() {
        // when
        List<BinaryContentDto> result = binaryContentMapper.toDtoList(List.of());

        // then
        assertThat(result).isEmpty();
    }
}
