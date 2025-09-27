package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileIOErrorException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BinaryContentServiceTest {

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    private BasicBinaryContentService binaryContentService;

    @Test
    @DisplayName("바이너리 콘텐츠 생성 성공")
    void create_success() {
        // given
        String fileName = "test.jpg";
        String contentType = "image/jpeg";
        Long size = 1024L;
        byte[] bytes = "test content".getBytes();

        UserProfileImageRequest request = UserProfileImageRequest.builder()
                .fileName(fileName)
                .contentType(contentType)
                .size(size)
                .bytes(bytes)
                .build();

        given(binaryContentRepository.save(any(BinaryContent.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(binaryContentStorage.put(any(UUID.class), any(byte[].class)))
                .willAnswer(i -> i.getArgument(0));

        // when
        BinaryContentResponse response = binaryContentService.create(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getFileName()).isEqualTo(fileName);
        assertThat(response.getContentType()).isEqualTo(contentType);
        assertThat(response.getSize()).isEqualTo(size);
        assertThat(response.isSuccess()).isTrue();

        verify(binaryContentRepository).save(any(BinaryContent.class));
        verify(binaryContentStorage).put(any(UUID.class), eq(bytes));
    }

    @Test
    @DisplayName("ID로 바이너리 콘텐츠 조회 성공")
    void getById_success() throws IOException {
        // given
        String fileName = "test.jpg";
        String contentType = "image/jpeg";
        Long size = 1024L;
        byte[] bytes = "test content".getBytes();

        BinaryContent binaryContent = new BinaryContent(fileName, contentType, size);
        UUID id = binaryContent.getId();

        InputStream inputStream = new ByteArrayInputStream(bytes);

        given(binaryContentRepository.findById(id)).willReturn(Optional.of(binaryContent));
        given(binaryContentStorage.get(id)).willReturn(inputStream);

        // when
        BinaryContentResponse response = binaryContentService.getById(id);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(binaryContent.getId());
        assertThat(response.getFileName()).isEqualTo(fileName);
        assertThat(response.getContentType()).isEqualTo(contentType);
        assertThat(response.getSize()).isEqualTo(size);
        assertThat(response.getBytes()).containsExactly(bytes);
        assertThat(response.isSuccess()).isTrue();

        verify(binaryContentRepository).findById(id);
        verify(binaryContentStorage).get(id);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 바이너리 콘텐츠 조회 실패")
    void getById_notFound_failure() {
        // given
        UUID id = UUID.randomUUID();
        given(binaryContentRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> binaryContentService.getById(id))
                .isInstanceOf(BinaryContentNotFoundException.class);

        verify(binaryContentRepository).findById(id);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 다운로드 DTO 생성 성공")
    void download_success() throws IOException {
        // given
        String fileName = "test.jpg";
        String contentType = "image/jpeg";
        Long size = 1024L;
        byte[] bytes = "test content".getBytes();

        BinaryContent binaryContent = new BinaryContent(fileName, contentType, size);
        UUID id = binaryContent.getId();

        InputStream inputStream = new ByteArrayInputStream(bytes);

        given(binaryContentRepository.findById(id)).willReturn(Optional.of(binaryContent));
        given(binaryContentStorage.get(id)).willReturn(inputStream);

        // when
        BinaryContentDTO dto = binaryContentService.download(id);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(binaryContent.getId());
        assertThat(dto.getFileName()).isEqualTo(fileName);
        assertThat(dto.getContentType()).isEqualTo(contentType);
        assertThat(dto.getSize()).isEqualTo(size);

        verify(binaryContentRepository).findById(id);
        verify(binaryContentStorage).get(id);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 다운로드 DTO 생성 실패")
    void download_notFound_failure() {
        // given
        UUID id = UUID.randomUUID();
        given(binaryContentRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> binaryContentService.download(id))
                .isInstanceOf(BinaryContentNotFoundException.class);

        verify(binaryContentRepository).findById(id);
    }

    @Test
    @DisplayName("파일 스토리지 오류로 다운로드 DTO 생성 실패")
    void download_fileIOError_failure() throws IOException {
        // given
        String fileName = "test.jpg";
        String contentType = "image/jpeg";
        Long size = 1024L;

        BinaryContent binaryContent = new BinaryContent(fileName, contentType, size);
        UUID id = binaryContent.getId();

        FileIOErrorException ex = FileIOErrorException
                .withStorage(
                        fileName,
                        new FileNotFoundException("File not found in storage: " + fileName)
                );

        given(binaryContentRepository.findById(id)).willReturn(Optional.of(binaryContent));
        given(binaryContentStorage.get(id)).willThrow(ex);

        // when
        // then
        assertThatThrownBy(() -> binaryContentService.download(id))
                .isInstanceOf(FileIOErrorException.class);

        verify(binaryContentRepository).findById(id);
        verify(binaryContentStorage).get(id);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 삭제 성공")
    void delete_success() {
        // given
        String fileName = "test.jpg";
        String contentType = "image/jpeg";
        Long size = 1024L;

        BinaryContent binaryContent = new BinaryContent(fileName, contentType, size);
        UUID id = binaryContent.getId();

        given(binaryContentRepository.findById(id)).willReturn(Optional.of(binaryContent));

        // when
        BinaryContentResponse response = binaryContentService.delete(id);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(binaryContent.getId());
        assertThat(response.getFileName()).isEqualTo(fileName);
        assertThat(response.getContentType()).isEqualTo(contentType);
        assertThat(response.getSize()).isEqualTo(size);
        assertThat(response.isSuccess()).isTrue();

        verify(binaryContentRepository).findById(id);
        verify(binaryContentRepository).deleteById(id);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 바이너리 콘텐츠 삭제 실패")
    void delete_notFound_failure() {
        // given
        UUID id = UUID.randomUUID();
        given(binaryContentRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> binaryContentService.delete(id))
                .isInstanceOf(BinaryContentNotFoundException.class);

        verify(binaryContentRepository).findById(id);
    }
}
