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

import static com.sprint.mission.discodeit.support.StorageTestFixtures.NEW_CONTENT;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.ORIGINAL_CONTENT;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.TEST_CONTENT_ENGLISH;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.createBinaryContentDto;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.createLargeContent;
import static com.sprint.mission.discodeit.support.StorageTestFixtures.createTestContent;
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

    @Test
    @DisplayName("파일 저장 성공")
    void put_Success() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        byte[] content = createTestContent(TEST_CONTENT_ENGLISH);

        // when
        UUID result = storage.put(id, content);

        // then
        assertThat(result).isEqualTo(id);
        Path filePath = tempDir.resolve(id.toString());
        assertThat(Files.exists(filePath)).isTrue();
        assertThat(Files.readAllBytes(filePath)).isEqualTo(content);
    }

    @Test
    @DisplayName("파일 리소스 조회 성공")
    void download_Success() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        byte[] content = createTestContent(TEST_CONTENT_ENGLISH);
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
        assertThat(Files.readAllBytes(tempDir.resolve(id1.toString()))).isEqualTo(content1);
        assertThat(Files.readAllBytes(tempDir.resolve(id2.toString()))).isEqualTo(content2);
        assertThat(Files.readAllBytes(tempDir.resolve(id3.toString()))).isEqualTo(content3);
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
        assertThat(Files.readAllBytes(tempDir.resolve(id.toString()))).isEqualTo(largeContent);
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
        assertThat(Files.readAllBytes(tempDir.resolve(id.toString()))).isEqualTo(emptyContent);
    }

    @Test
    @DisplayName("파일 다운로드 - Content-Disposition 헤더 확인")
    void download_ContentDisposition() {
        // given
        UUID id = UUID.randomUUID();
        byte[] content = createTestContent(TEST_CONTENT_ENGLISH);
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

    @Test
    @DisplayName("초기화 실패 - 부모 경로가 파일인 경우 디렉토리 생성 실패")
    void init_FailsWhenParentIsFile() throws IOException {
        // given
        Path filePath = tempDir.resolve("existing-file");
        Files.createFile(filePath);

        // 파일 내부에 디렉토리를 생성하려고 시도
        Path invalidPath = filePath.resolve("sub-dir");
        LocalBinaryContentStorage newStorage = new LocalBinaryContentStorage(invalidPath);

        // when & then
        assertThatThrownBy(newStorage::init)
            .isInstanceOf(UncheckedIOException.class)
            .hasMessageContaining("로컬 스토리지 디렉토리 생성 실패");
    }

    @Test
    @DisplayName("파일 저장 실패 - 존재하지 않는 디렉토리")
    void put_FailsWhenDirectoryDoesNotExist() {
        // given
        Path nonExistentDir = tempDir.resolve("non-existent-dir");
        LocalBinaryContentStorage newStorage = new LocalBinaryContentStorage(nonExistentDir);
        // init()을 호출하지 않아서 디렉토리가 존재하지 않음

        UUID id = UUID.randomUUID();
        byte[] content = createTestContent(TEST_CONTENT_ENGLISH);

        // when & then
        assertThatThrownBy(() -> newStorage.put(id, content))
            .isInstanceOf(UncheckedIOException.class)
            .hasMessageContaining("파일 저장 실패");
    }
}
