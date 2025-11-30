package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.TestFixtures;
import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentUploadException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BinaryContentServiceTest {

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private BinaryContentMapper binaryContentMapper;

    @Mock
    private MultipartFile multipartFile;

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

    @Test
    @DisplayName("create - 바이너리 콘텐츠 생성 성공")
    void create_Success() throws IOException {
        // given
        byte[] fileBytes = "test-file-data".getBytes();
        UUID binaryContentId = UUID.randomUUID();

        given(multipartFile.getOriginalFilename()).willReturn("test.png");
        given(multipartFile.getSize()).willReturn(1024L);
        given(multipartFile.getContentType()).willReturn("image/png");
        given(multipartFile.getBytes()).willReturn(fileBytes);

        BinaryContent savedBinaryContent = TestFixtures.createBinaryContent(
            binaryContentId, "test.png", 1024L, "image/png"
        );

        BinaryContentDto expectedDto = new BinaryContentDto(
            binaryContentId, "test.png", 1024L, "image/png"
        );

        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedBinaryContent);
        given(binaryContentMapper.toDto(savedBinaryContent)).willReturn(expectedDto);

        // when
        BinaryContentDto result = binaryContentService.create(multipartFile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(binaryContentId);
        assertThat(result.fileName()).isEqualTo("test.png");
        assertThat(result.size()).isEqualTo(1024L);
        assertThat(result.contentType()).isEqualTo("image/png");

        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(eventPublisher).should().publishEvent(any(BinaryContentCreatedEvent.class));
        then(binaryContentMapper).should().toDto(savedBinaryContent);
    }

    @Test
    @DisplayName("create - 파일 읽기 실패 시 BinaryContentUploadException 발생")
    void create_IOException_ThrowsBinaryContentUploadException() throws IOException {
        // given
        given(multipartFile.getOriginalFilename()).willReturn("test.png");
        given(multipartFile.getSize()).willReturn(1024L);
        given(multipartFile.getContentType()).willReturn("image/png");
        given(multipartFile.getBytes()).willThrow(new IOException("Read error"));

        BinaryContent savedBinaryContent = new BinaryContent("test.png", 1024L, "image/png");
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedBinaryContent);

        // when & then
        assertThatThrownBy(() -> binaryContentService.create(multipartFile))
            .isInstanceOf(BinaryContentUploadException.class)
            .hasCauseInstanceOf(IOException.class);

        then(binaryContentRepository).should().save(any(BinaryContent.class));
        then(eventPublisher).should(never()).publishEvent(any(BinaryContentCreatedEvent.class));
    }
}
