package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BinaryContentServiceTest {

    @Mock
    private BinaryContentRepository binaryContentRepository;

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

        given(binaryContentRepository.findAllById(ids)).willReturn(binaryContents);
        given(binaryContentMapper.toDtoList(binaryContents)).willReturn(expectedDtos);

        // when
        List<BinaryContentDto> result = binaryContentService.findAllByIdIn(ids);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(dto1, dto2);

        then(binaryContentRepository).should().findAllById(ids);
        then(binaryContentMapper).should().toDtoList(binaryContents);
    }

    @Test
    @DisplayName("findAllByIdIn - 빈 컬렉션으로 조회 시 빈 리스트 반환")
    void findAllByIdIn_EmptyCollection_ReturnsEmptyList() {
        // given
        Collection<UUID> emptyIds = List.of();

        given(binaryContentRepository.findAllById(emptyIds)).willReturn(List.of());
        given(binaryContentMapper.toDtoList(List.of())).willReturn(List.of());

        // when
        List<BinaryContentDto> result = binaryContentService.findAllByIdIn(emptyIds);

        // then
        assertThat(result).isEmpty();

        then(binaryContentRepository).should().findAllById(emptyIds);
    }

    @Test
    @DisplayName("getBinaryContent - 파일 조회 성공")
    void find_Success() {
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
        BinaryContentDto result = binaryContentService.find(binaryContentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(binaryContentId);
        assertThat(result.fileName()).isEqualTo("test.pdf");

        then(binaryContentRepository).should().findById(binaryContentId);
        then(binaryContentMapper).should().toDto(binaryContent);
    }

    @Test
    @DisplayName("getBinaryContent - 존재하지 않는 파일 조회 시 BinaryContentNotFoundException 발생")
    void findNotFoundException() {
        // given
        UUID binaryContentId = UUID.randomUUID();

        given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> binaryContentService.find(binaryContentId))
            .isInstanceOf(BinaryContentNotFoundException.class);

        then(binaryContentRepository).should().findById(binaryContentId);
        then(binaryContentMapper).shouldHaveNoInteractions();
    }

}
