package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LocalBinaryContentStorage 단위 테스트")
class LocalBinaryContentStorageTest {

    private static final String TEST_CONTENT = "test content";
    private static final String ORIGINAL_CONTENT = "original content";
    private static final String NEW_CONTENT = "new content";
    private static final int LARGE_FILE_SIZE_MB = 1;
    private static final int BYTES_PER_MB = 1024 * 1024;

    private LocalBinaryContentStorage storage;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        storage = new LocalBinaryContentStorage(tempDir);
        storage.init();
    }

    private byte[] createTestContent(String content) {
        return content.getBytes();
    }

    private byte[] createLargeContent() {
        byte[] content = new byte[LARGE_FILE_SIZE_MB * BYTES_PER_MB];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }
        return content;
    }

    private BinaryContentDto createBinaryContentDto(UUID id, String fileName, long size, String contentType) {
        return new BinaryContentDto(id, fileName, size, contentType);
    }

    private void assertStreamContentEquals(InputStream stream, byte[] expected) throws IOException {
        try (stream) {
            byte[] actual = stream.readAllBytes();
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("파일 저장 성공")
    void put_Success() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        byte[] content = createTestContent(TEST_CONTENT);

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
        byte[] content = createTestContent(TEST_CONTENT);
        storage.put(id, content);

        // when
        InputStream inputStream = storage.get(id);

        // then
        assertThat(inputStream).isNotNull();
        assertStreamContentEquals(inputStream, content);
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
        byte[] content = createTestContent(TEST_CONTENT);
        storage.put(id, content);

        BinaryContentDto metaData = createBinaryContentDto(
            id,
            "test.txt",
            content.length,
            "text/plain"
        );

        // when
        ResponseEntity<Resource> response = storage.download(metaData);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().exists()).isTrue();
        try (InputStream is = response.getBody().getInputStream()) {
            byte[] readContent = is.readAllBytes();
            assertThat(readContent).isEqualTo(content);
        }
    }

    @Test
    @DisplayName("파일 리소스 조회 실패 - 존재하지 않는 파일")
    void download_FileNotFound() {
        // given
        UUID id = UUID.randomUUID();
        BinaryContentDto metaData = createBinaryContentDto(
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
        byte[] originalContent = createTestContent(ORIGINAL_CONTENT);
        byte[] newContent = createTestContent(NEW_CONTENT);

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

        byte[] content1 = createTestContent("content 1");
        byte[] content2 = createTestContent("content 2");
        byte[] content3 = createTestContent("content 3");

        // when
        storage.put(id1, content1);
        storage.put(id2, content2);
        storage.put(id3, content3);

        // then
        assertStreamContentEquals(storage.get(id1), content1);
        assertStreamContentEquals(storage.get(id2), content2);
        assertStreamContentEquals(storage.get(id3), content3);
    }

    @Test
    @DisplayName("큰 파일 저장 및 조회")
    void put_LargeFile() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        byte[] largeContent = createLargeContent();

        // when
        UUID result = storage.put(id, largeContent);

        // then
        assertThat(result).isEqualTo(id);
        assertStreamContentEquals(storage.get(id), largeContent);
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
        assertStreamContentEquals(storage.get(id), emptyContent);
    }

    @Test
    @DisplayName("파일 다운로드 - Content-Disposition 헤더 확인")
    void download_ContentDisposition() {
        // given
        UUID id = UUID.randomUUID();
        byte[] content = createTestContent(TEST_CONTENT);
        storage.put(id, content);

        BinaryContentDto metaData = createBinaryContentDto(
            id,
            "testfile.txt",
            content.length,
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

        BinaryContentDto metaData = createBinaryContentDto(
            id,
            "image.jpg",
            content.length,
            "image/jpeg"
        );

        // when
        ResponseEntity<Resource> response = storage.download(metaData);

        // then
        assertThat(response.getHeaders().getFirst("Content-Type"))
            .isEqualTo("image/jpeg");
    }
}
