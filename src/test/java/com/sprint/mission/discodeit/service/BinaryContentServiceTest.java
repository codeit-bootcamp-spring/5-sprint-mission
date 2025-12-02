package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

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

        BinaryContentDto dto = new BinaryContentDto(
            id1, "file.png", 1024L, "image/png", BinaryContentStatus.SUCCESS);

        given(binaryContentRepository.findAllById(ids)).willReturn(binaryContents);
        given(binaryContentMapper.toDto(any(BinaryContent.class))).willReturn(dto);

        // when
        List<BinaryContentDto> result = binaryContentService.findAllByIdIn(ids);

        // then
        assertThat(result).hasSize(2);

        then(binaryContentRepository).should().findAllById(ids);
        then(binaryContentMapper).should(times(2)).toDto(any(BinaryContent.class));
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
            "application/pdf",
            BinaryContentStatus.SUCCESS
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

    @Test
    @DisplayName("updateStatus - 상태 업데이트 성공")
    void updateStatus_Success() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        BinaryContent binaryContent = new BinaryContent("test.png", 1024L, "image/png");
        BinaryContentDto expectedDto = new BinaryContentDto(
            binaryContentId, "test.png", 1024L, "image/png", BinaryContentStatus.SUCCESS
        );

        given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.of(binaryContent));
        given(binaryContentMapper.toDto(binaryContent)).willReturn(expectedDto);

        // when
        BinaryContentDto result = binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);

        // then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(BinaryContentStatus.SUCCESS);

        then(binaryContentRepository).should().findById(binaryContentId);
        then(binaryContentMapper).should().toDto(binaryContent);
    }

    @Test
    @DisplayName("updateStatus - 존재하지 않는 파일 상태 업데이트 시 BinaryContentNotFoundException 발생")
    void updateStatus_NotFound() {
        // given
        UUID binaryContentId = UUID.randomUUID();

        given(binaryContentRepository.findById(binaryContentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL))
            .isInstanceOf(BinaryContentNotFoundException.class);

        then(binaryContentRepository).should().findById(binaryContentId);
        then(binaryContentMapper).shouldHaveNoInteractions();
    }
}
