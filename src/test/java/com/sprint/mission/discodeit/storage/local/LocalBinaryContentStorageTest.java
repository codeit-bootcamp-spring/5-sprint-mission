package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LocalBinaryContentStorage 단위 테스트")
class LocalBinaryContentStorageTest {

    private LocalBinaryContentStorage storage;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        storage = new LocalBinaryContentStorage(tempDir);
        storage.init();
    }

    @AfterEach
    void tearDown() throws IOException {
        // 테스트 후 정리
        if (Files.exists(tempDir)) {
            try (var paths = Files.walk(tempDir)) {
                paths.sorted(Comparator.reverseOrder()) // 역순으로 정렬하여 하위부터 삭제
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // ignore
                        }
                    });
            }
        }
    }

    @Test
    @DisplayName("파일 저장 성공")
    void put_Success() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        byte[] content = "test content".getBytes();

        // when
        UUID result = storage.put(id, content);

        // then
        assertThat(result).isEqualTo(id);
        Path filePath = tempDir.resolve(id.toString());
        assertThat(Files.exists(filePath)).isTrue();
        assertThat(Files.readAllBytes(filePath)).isEqualTo(content);
    }

    @Test
    @DisplayName("파일 읽기 성공")
    void get_Success() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        byte[] content = "test content".getBytes();
        storage.put(id, content);

        // when
        InputStream inputStream = storage.get(id);

        // then
        assertThat(inputStream).isNotNull();
        byte[] readContent = inputStream.readAllBytes();
        assertThat(readContent).isEqualTo(content);
        inputStream.close();
    }

    @Test
    @DisplayName("파일 읽기 실패 - 존재하지 않는 파일")
    void get_FileNotFound() {
        // given
        UUID id = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> storage.get(id))
            .isInstanceOf(UncheckedIOException.class)
            .hasMessageContaining("파일 읽기 실패");
    }

    @Test
    @DisplayName("파일 리소스 조회 성공")
    void download_Success() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        byte[] content = "test content".getBytes();
        storage.put(id, content);

        BinaryContentDto metaData = new BinaryContentDto(
            id,
            "test.txt",
            (long) content.length,
            "text/plain"
        );

        // when
        ResponseEntity<Resource> response = storage.download(metaData);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().exists()).isTrue();
        byte[] readContent = response.getBody().getInputStream().readAllBytes();
        assertThat(readContent).isEqualTo(content);
    }

    @Test
    @DisplayName("파일 리소스 조회 실패 - 존재하지 않는 파일")
    void download_FileNotFound() {
        // given
        UUID id = UUID.randomUUID();
        BinaryContentDto metaData = new BinaryContentDto(
            id,
            "notfound.txt",
            1024L,
            "text/plain"
        );

        // when & then
        assertThatThrownBy(() -> storage.download(metaData))
            .isInstanceOf(UncheckedIOException.class);
    }

    @Test
    @DisplayName("파일 저장 후 덮어쓰기")
    void put_Overwrite() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        byte[] originalContent = "original content".getBytes();
        byte[] newContent = "new content".getBytes();

        storage.put(id, originalContent);

        // when
        storage.put(id, newContent);

        // then
        Path filePath = tempDir.resolve(id.toString());
        assertThat(Files.readAllBytes(filePath)).isEqualTo(newContent);
    }

    @Test
    @DisplayName("초기화 시 디렉토리 생성")
    void init_CreatesDirectory() throws IOException {
        // given
        Path newDir = tempDir.resolve("new-storage");

        // when
        LocalBinaryContentStorage newStorage = new LocalBinaryContentStorage(newDir);
        newStorage.init();

        // then
        assertThat(Files.exists(newDir)).isTrue();
        assertThat(Files.isDirectory(newDir)).isTrue();
    }

    @Test
    @DisplayName("여러 파일 저장 및 조회")
    void put_MultipleFiles() throws IOException {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        byte[] content1 = "content 1".getBytes();
        byte[] content2 = "content 2".getBytes();
        byte[] content3 = "content 3".getBytes();

        // when
        storage.put(id1, content1);
        storage.put(id2, content2);
        storage.put(id3, content3);

        // then
        assertThat(storage.get(id1).readAllBytes()).isEqualTo(content1);
        assertThat(storage.get(id2).readAllBytes()).isEqualTo(content2);
        assertThat(storage.get(id3).readAllBytes()).isEqualTo(content3);
    }

    @Test
    @DisplayName("큰 파일 저장 및 조회")
    void put_LargeFile() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }

        // when
        UUID result = storage.put(id, largeContent);

        // then
        assertThat(result).isEqualTo(id);
        InputStream inputStream = storage.get(id);
        byte[] readContent = inputStream.readAllBytes();
        assertThat(readContent).isEqualTo(largeContent);
        inputStream.close();
    }

    @Test
    @DisplayName("빈 파일 저장 및 조회")
    void put_EmptyFile() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        byte[] emptyContent = new byte[0];

        // when
        UUID result = storage.put(id, emptyContent);

        // then
        assertThat(result).isEqualTo(id);
        InputStream inputStream = storage.get(id);
        byte[] readContent = inputStream.readAllBytes();
        assertThat(readContent).isEmpty();
        inputStream.close();
    }

    @Test
    @DisplayName("파일 다운로드 - Content-Disposition 헤더 확인")
    void download_ContentDisposition() {
        // given
        UUID id = UUID.randomUUID();
        byte[] content = "test content".getBytes();
        storage.put(id, content);

        BinaryContentDto metaData = new BinaryContentDto(
            id,
            "testfile.txt",
            (long) content.length,
            "text/plain"
        );

        // when
        ResponseEntity<Resource> response = storage.download(metaData);

        // then
        assertThat(response.getHeaders().getFirst("Content-Disposition"))
            .contains("attachment")
            .contains("testfile.txt");
    }

    @Test
    @DisplayName("파일 다운로드 - Content-Type 헤더 확인")
    void download_ContentType() {
        // given
        UUID id = UUID.randomUUID();
        byte[] content = new byte[]{0x12, 0x34, 0x56, 0x78};
        storage.put(id, content);

        BinaryContentDto metaData = new BinaryContentDto(
            id,
            "image.jpg",
            (long) content.length,
            "image/jpeg"
        );

        // when
        ResponseEntity<Resource> response = storage.download(metaData);

        // then
        assertThat(response.getHeaders().getFirst("Content-Type"))
            .isEqualTo("image/jpeg");
    }
}
