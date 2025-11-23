package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.FileDownloadResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BinaryContentServiceTest {

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private BinaryContentMapper binaryContentMapper;

    @InjectMocks
    private BinaryContentService binaryContentService;

    @Test
    @DisplayName("findAllByIdIn - 여러 파일 조회 성공")
    void findAllByIdIn_Success() {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Collection<UUID> ids = Set.of(id1, id2);

        BinaryContent bc1 = new BinaryContent("file1.png", 1024L, "image/png");
        BinaryContent bc2 = new BinaryContent("file2.jpg", 2048L, "image/jpeg");
        List<BinaryContent> binaryContents = List.of(bc1, bc2);

        BinaryContentDto dto1 = new BinaryContentDto(id1, "file1.png", 1024L, "image/png");
        BinaryContentDto dto2 = new BinaryContentDto(id2, "file2.jpg", 2048L, "image/jpeg");
        List<BinaryContentDto> expectedDtos = List.of(dto1, dto2);

        given(binaryContentRepository.findAllByIdIn(ids)).willReturn(binaryContents);
        given(binaryContentMapper.toDtoList(binaryContents)).willReturn(expectedDtos);

        // when
        List<BinaryContentDto> result = binaryContentService.findAllByIdIn(ids);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(dto1, dto2);

        then(binaryContentRepository).should().findAllByIdIn(ids);
        then(binaryContentMapper).should().toDtoList(binaryContents);
    }

    @Test
    @DisplayName("findAllByIdIn - 빈 컬렉션으로 조회 시 빈 리스트 반환")
    void findAllByIdIn_EmptyCollection_ReturnsEmptyList() {
        // given
        Collection<UUID> emptyIds = List.of();

        given(binaryContentRepository.findAllByIdIn(emptyIds)).willReturn(List.of());
        given(binaryContentMapper.toDtoList(List.of())).willReturn(List.of());

        // when
        List<BinaryContentDto> result = binaryContentService.findAllByIdIn(emptyIds);

        // then
        assertThat(result).isEmpty();

        then(binaryContentRepository).should().findAllByIdIn(emptyIds);
    }

    @Test
    @DisplayName("getBinaryContent - 파일 조회 성공")
    void getBinaryContent_Success() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        BinaryContent binaryContent = new BinaryContent("test.pdf", 5120L, "application/pdf");
        BinaryContentDto expectedDto = new BinaryContentDto(
            binaryContentId,
            "test.pdf",
            5120L,
            "application/pdf"
        );

        given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.of(binaryContent));
        given(binaryContentMapper.toDto(binaryContent)).willReturn(expectedDto);

        // when
        BinaryContentDto result = binaryContentService.getBinaryContent(binaryContentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(binaryContentId);
        assertThat(result.fileName()).isEqualTo("test.pdf");

        then(binaryContentRepository).should().findById(binaryContentId);
        then(binaryContentMapper).should().toDto(binaryContent);
    }

    @Test
    @DisplayName("getBinaryContent - 존재하지 않는 파일 조회 시 BinaryContentNotFoundException 발생")
    void getBinaryContent_NotFound_ThrowsBinaryContentNotFoundException() {
        // given
        UUID binaryContentId = UUID.randomUUID();

        given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> binaryContentService.getBinaryContent(binaryContentId))
            .isInstanceOf(BinaryContentNotFoundException.class);

        then(binaryContentRepository).should().findById(binaryContentId);
        then(binaryContentMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("download - 파일 다운로드 성공")
    void download_Success() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        BinaryContent binaryContent = new BinaryContent("document.docx", 10240L, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        Resource resource = mock(Resource.class);

        given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.of(binaryContent));
        given(binaryContentStorage.getResource(binaryContentId)).willReturn(resource);

        // when
        FileDownloadResponse result = binaryContentService.download(binaryContentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.resource()).isEqualTo(resource);
        assertThat(result.fileName()).isEqualTo("document.docx");
        assertThat(result.contentType()).isEqualTo("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        assertThat(result.size()).isEqualTo(10240L);

        then(binaryContentRepository).should().findById(binaryContentId);
        then(binaryContentStorage).should().getResource(binaryContentId);
    }

    @Test
    @DisplayName("download - 존재하지 않는 파일 다운로드 시 BinaryContentNotFoundException 발생")
    void download_NotFound_ThrowsBinaryContentNotFoundException() {
        // given
        UUID binaryContentId = UUID.randomUUID();

        given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> binaryContentService.download(binaryContentId))
            .isInstanceOf(BinaryContentNotFoundException.class);

        then(binaryContentRepository).should().findById(binaryContentId);
        then(binaryContentStorage).shouldHaveNoInteractions();
    }
}
