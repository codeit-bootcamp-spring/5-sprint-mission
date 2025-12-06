package com.sprint.mission.discodeit.binarycontent.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.binarycontent.presentation.dto.BinaryContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("BinaryContentService 단위 테스트")
class BinaryContentServiceTest {

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentMapper binaryContentMapper;

    @InjectMocks
    private BinaryContentService binaryContentService;

    private static final UUID TEST_BINARY_CONTENT_ID = UUID.randomUUID();
    private static final String TEST_FILE_NAME = "test-file.png";
    private static final long TEST_SIZE = 1024L;
    private static final String TEST_CONTENT_TYPE = "image/png";

    @Nested
    @DisplayName("findAllById 메서드")
    class FindAllById {

        @Test
        @DisplayName("여러 ID로 조회 시 해당 BinaryContent 목록 반환")
        void findAllById_withValidIds_returnsBinaryContentList() {
            // given
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            List<UUID> ids = List.of(id1, id2);

            BinaryContent content1 = createBinaryContent(id1, "file1.png");
            BinaryContent content2 = createBinaryContent(id2, "file2.png");
            List<BinaryContent> contents = List.of(content1, content2);

            BinaryContentDto dto1 = createDto(id1, "file1.png");
            BinaryContentDto dto2 = createDto(id2, "file2.png");

            given(binaryContentRepository.findAllById(ids)).willReturn(contents);
            given(binaryContentMapper.toDto(content1)).willReturn(dto1);
            given(binaryContentMapper.toDto(content2)).willReturn(dto2);

            // when
            List<BinaryContentDto> result = binaryContentService.findAllById(ids);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(dto1, dto2);
            then(binaryContentRepository).should().findAllById(ids);
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 목록 반환")
        void findAllById_withEmptyIds_returnsEmptyList() {
            // given
            List<UUID> emptyIds = Collections.emptyList();

            given(binaryContentRepository.findAllById(emptyIds)).willReturn(Collections.emptyList());

            // when
            List<BinaryContentDto> result = binaryContentService.findAllById(emptyIds);

            // then
            assertThat(result).isEmpty();
            then(binaryContentRepository).should().findAllById(emptyIds);
        }

        @Test
        @DisplayName("존재하지 않는 ID 포함 시 존재하는 것만 반환")
        void findAllById_withSomeNonExistingIds_returnsOnlyExisting() {
            // given
            UUID existingId = UUID.randomUUID();
            UUID nonExistingId = UUID.randomUUID();
            List<UUID> ids = List.of(existingId, nonExistingId);

            BinaryContent content = createBinaryContent(existingId, "file.png");
            BinaryContentDto dto = createDto(existingId, "file.png");

            given(binaryContentRepository.findAllById(ids)).willReturn(List.of(content));
            given(binaryContentMapper.toDto(content)).willReturn(dto);

            // when
            List<BinaryContentDto> result = binaryContentService.findAllById(ids);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(existingId);
        }
    }

    @Nested
    @DisplayName("find 메서드")
    class Find {

        private BinaryContent testContent;
        private BinaryContentDto testDto;

        @BeforeEach
        void setUp() {
            testContent = createBinaryContent(TEST_BINARY_CONTENT_ID, TEST_FILE_NAME);
            testDto = createDto(TEST_BINARY_CONTENT_ID, TEST_FILE_NAME);
        }

        @Test
        @DisplayName("유효한 ID로 조회 시 BinaryContentDto 반환")
        void find_withValidId_returnsBinaryContentDto() {
            // given
            given(binaryContentRepository.findById(TEST_BINARY_CONTENT_ID))
                .willReturn(Optional.of(testContent));
            given(binaryContentMapper.toDto(testContent)).willReturn(testDto);

            // when
            BinaryContentDto result = binaryContentService.find(TEST_BINARY_CONTENT_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(TEST_BINARY_CONTENT_ID);
            assertThat(result.fileName()).isEqualTo(TEST_FILE_NAME);
            assertThat(result.size()).isEqualTo(TEST_SIZE);
            assertThat(result.contentType()).isEqualTo(TEST_CONTENT_TYPE);
            then(binaryContentRepository).should().findById(TEST_BINARY_CONTENT_ID);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 BinaryContentNotFoundException 발생")
        void find_withNonExistingId_throwsBinaryContentNotFoundException() {
            // given
            UUID nonExistingId = UUID.randomUUID();

            given(binaryContentRepository.findById(nonExistingId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> binaryContentService.find(nonExistingId))
                .isInstanceOf(BinaryContentNotFoundException.class);

            then(binaryContentMapper).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("updateStatus 메서드")
    class UpdateStatus {

        private BinaryContent testContent;

        @BeforeEach
        void setUp() {
            testContent = createBinaryContent(TEST_BINARY_CONTENT_ID, TEST_FILE_NAME);
        }

        @Test
        @DisplayName("PROCESSING에서 SUCCESS로 상태 변경 성공")
        void updateStatus_fromProcessingToSuccess_updatesSuccessfully() {
            // given
            BinaryContentDto updatedDto = new BinaryContentDto(
                TEST_BINARY_CONTENT_ID,
                TEST_FILE_NAME,
                TEST_SIZE,
                TEST_CONTENT_TYPE,
                BinaryContentStatus.SUCCESS
            );

            given(binaryContentRepository.findById(TEST_BINARY_CONTENT_ID))
                .willReturn(Optional.of(testContent));
            given(binaryContentMapper.toDto(testContent)).willReturn(updatedDto);

            // when
            BinaryContentDto result = binaryContentService.updateStatus(
                TEST_BINARY_CONTENT_ID,
                BinaryContentStatus.SUCCESS
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo(BinaryContentStatus.SUCCESS);
            assertThat(testContent.getStatus()).isEqualTo(BinaryContentStatus.SUCCESS);
        }

        @Test
        @DisplayName("PROCESSING에서 FAIL로 상태 변경 성공")
        void updateStatus_fromProcessingToFail_updatesSuccessfully() {
            // given
            BinaryContentDto updatedDto = new BinaryContentDto(
                TEST_BINARY_CONTENT_ID,
                TEST_FILE_NAME,
                TEST_SIZE,
                TEST_CONTENT_TYPE,
                BinaryContentStatus.FAIL
            );

            given(binaryContentRepository.findById(TEST_BINARY_CONTENT_ID))
                .willReturn(Optional.of(testContent));
            given(binaryContentMapper.toDto(testContent)).willReturn(updatedDto);

            // when
            BinaryContentDto result = binaryContentService.updateStatus(
                TEST_BINARY_CONTENT_ID,
                BinaryContentStatus.FAIL
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo(BinaryContentStatus.FAIL);
            assertThat(testContent.getStatus()).isEqualTo(BinaryContentStatus.FAIL);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 상태 변경 시 BinaryContentNotFoundException 발생")
        void updateStatus_withNonExistingId_throwsBinaryContentNotFoundException() {
            // given
            UUID nonExistingId = UUID.randomUUID();

            given(binaryContentRepository.findById(nonExistingId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> binaryContentService.updateStatus(
                nonExistingId,
                BinaryContentStatus.SUCCESS
            ))
                .isInstanceOf(BinaryContentNotFoundException.class);

            then(binaryContentMapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("null 상태로 변경 시 기존 상태 유지")
        void updateStatus_withNullStatus_keepsCurrentStatus() {
            // given
            BinaryContentDto unchangedDto = createDto(TEST_BINARY_CONTENT_ID, TEST_FILE_NAME);

            given(binaryContentRepository.findById(TEST_BINARY_CONTENT_ID))
                .willReturn(Optional.of(testContent));
            given(binaryContentMapper.toDto(testContent)).willReturn(unchangedDto);

            // when
            BinaryContentDto result = binaryContentService.updateStatus(
                TEST_BINARY_CONTENT_ID,
                null
            );

            // then
            assertThat(result).isNotNull();
            assertThat(testContent.getStatus()).isEqualTo(BinaryContentStatus.PROCESSING);
        }

        @Test
        @DisplayName("동일한 상태로 변경 시도 시 성공")
        void updateStatus_toSameStatus_updatesSuccessfully() {
            // given
            BinaryContentDto dto = createDto(TEST_BINARY_CONTENT_ID, TEST_FILE_NAME);

            given(binaryContentRepository.findById(TEST_BINARY_CONTENT_ID))
                .willReturn(Optional.of(testContent));
            given(binaryContentMapper.toDto(testContent)).willReturn(dto);

            // when
            BinaryContentDto result = binaryContentService.updateStatus(
                TEST_BINARY_CONTENT_ID,
                BinaryContentStatus.PROCESSING
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo(BinaryContentStatus.PROCESSING);
        }
    }

    private BinaryContent createBinaryContent(UUID id, String fileName) {
        BinaryContent content = new BinaryContent(fileName, TEST_SIZE, TEST_CONTENT_TYPE);
        ReflectionTestUtils.setField(content, "id", id);
        return content;
    }

    private BinaryContentDto createDto(UUID id, String fileName) {
        return new BinaryContentDto(
            id,
            fileName,
            TEST_SIZE,
            TEST_CONTENT_TYPE,
            BinaryContentStatus.PROCESSING
        );
    }
}
