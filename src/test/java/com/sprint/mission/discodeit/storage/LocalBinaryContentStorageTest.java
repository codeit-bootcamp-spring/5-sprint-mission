package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.properties.StorageProperties;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocalBinaryContentStorage 단위 테스트")
class LocalBinaryContentStorageTest {

    @Mock
    private BinaryContentRepository binaryContentRepository;

    private LocalBinaryContentStorage storage;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        StorageProperties.Local local = new StorageProperties.Local(
            tempDir.toString(),
            Duration.ofMinutes(10)
        );
        StorageProperties properties = new StorageProperties("local", local);

        storage = new LocalBinaryContentStorage(binaryContentRepository, properties);
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
    void getResource_Success() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        byte[] content = "test content".getBytes();
        storage.put(id, content);

        // when
        Resource resource = storage.getResource(id);

        // then
        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
        byte[] readContent = resource.getInputStream().readAllBytes();
        assertThat(readContent).isEqualTo(content);
    }

    @Test
    @DisplayName("파일 리소스 조회 실패 - 존재하지 않는 파일")
    void getResource_FileNotFound() {
        // given
        UUID id = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> storage.getResource(id))
            .isInstanceOf(BinaryContentNotFoundException.class);
    }

    @Test
    @DisplayName("고아 파일 정리 - 오래된 UUID 파일 삭제")
    void cleanOrphanFiles_DeletesOldOrphanFile() throws IOException {
        // given
        UUID orphanId = UUID.randomUUID();
        Path orphanFile = tempDir.resolve(orphanId.toString());
        Files.write(orphanFile, "orphan content".getBytes());

        // 파일을 11분 전에 생성된 것으로 설정
        Instant oldTime = Instant.now().minus(Duration.ofMinutes(11));
        Files.setLastModifiedTime(orphanFile, FileTime.from(oldTime));

        given(binaryContentRepository.existsById(orphanId)).willReturn(false);

        // when
        storage.cleanOrphanFiles();

        // then
        assertThat(Files.exists(orphanFile)).isFalse();
    }

    @Test
    @DisplayName("고아 파일 정리 - 최근 파일은 삭제하지 않음")
    void cleanOrphanFiles_KeepsRecentFiles() throws IOException {
        // given
        UUID recentId = UUID.randomUUID();
        Path recentFile = tempDir.resolve(recentId.toString());
        Files.write(recentFile, "recent content".getBytes());

        // when
        storage.cleanOrphanFiles();

        // then
        // 최근 파일은 orphanGrace 기간(10분) 이내이므로 검사되지 않고 보존됨
        assertThat(Files.exists(recentFile)).isTrue();
    }

    @Test
    @DisplayName("고아 파일 정리 - DB에 존재하는 파일은 삭제하지 않음")
    void cleanOrphanFiles_KeepsFilesInDatabase() throws IOException {
        // given
        UUID validId = UUID.randomUUID();
        Path validFile = tempDir.resolve(validId.toString());
        Files.write(validFile, "valid content".getBytes());

        // 파일을 11분 전에 생성된 것으로 설정
        Instant oldTime = Instant.now().minus(Duration.ofMinutes(11));
        Files.setLastModifiedTime(validFile, FileTime.from(oldTime));

        given(binaryContentRepository.existsById(validId)).willReturn(true);

        // when
        storage.cleanOrphanFiles();

        // then
        assertThat(Files.exists(validFile)).isTrue();
    }

    @Test
    @DisplayName("고아 파일 정리 - 잘못된 형식의 파일명 삭제")
    void cleanOrphanFiles_DeletesInvalidFilenames() throws IOException {
        // given
        Path invalidFile = tempDir.resolve("invalid-filename.txt");
        Files.write(invalidFile, "invalid content".getBytes());

        // 파일을 11분 전에 생성된 것으로 설정
        Instant oldTime = Instant.now().minus(Duration.ofMinutes(11));
        Files.setLastModifiedTime(invalidFile, FileTime.from(oldTime));

        // when
        storage.cleanOrphanFiles();

        // then
        assertThat(Files.exists(invalidFile)).isFalse();
    }

    @Test
    @DisplayName("고아 파일 정리 - 스토리지 디렉토리가 없으면 경고 로그만 출력")
    void cleanOrphanFiles_HandlesNonExistentDirectory() throws IOException {
        // given
        Path nonExistentDir = tempDir.resolve("non-existent");
        StorageProperties.Local local = new StorageProperties.Local(
            nonExistentDir.toString(),
            Duration.ofMinutes(10)
        );
        StorageProperties properties = new StorageProperties("local", local);
        LocalBinaryContentStorage storageWithInvalidDir = new LocalBinaryContentStorage(
            binaryContentRepository, properties
        );
        storageWithInvalidDir.init();

        // 디렉토리 삭제
        Files.delete(nonExistentDir);

        // when & then (예외가 발생하지 않아야 함)
        storageWithInvalidDir.cleanOrphanFiles();
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
        StorageProperties.Local local = new StorageProperties.Local(
            newDir.toString(),
            Duration.ofMinutes(10)
        );
        StorageProperties properties = new StorageProperties("local", local);

        // when
        LocalBinaryContentStorage newStorage = new LocalBinaryContentStorage(binaryContentRepository, properties);
        newStorage.init();

        // then
        assertThat(Files.exists(newDir)).isTrue();
        assertThat(Files.isDirectory(newDir)).isTrue();
    }

    @Test
    @DisplayName("orphanGrace가 null일 때 기본값 10분 사용")
    void constructor_DefaultOrphanGrace() {
        // given
        StorageProperties.Local local = new StorageProperties.Local(
            tempDir.toString(),
            null
        );
        StorageProperties properties = new StorageProperties("local", local);

        // when
        LocalBinaryContentStorage newStorage = new LocalBinaryContentStorage(binaryContentRepository, properties);

        // then - 예외가 발생하지 않아야 함
        assertThat(newStorage).isNotNull();
    }

    @Test
    @DisplayName("고아 파일 정리 중 개별 파일 처리 실패해도 계속 진행")
    void cleanOrphanFiles_ContinuesOnError() throws IOException {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Path file1 = tempDir.resolve(id1.toString());
        Path file2 = tempDir.resolve(id2.toString());

        Files.write(file1, "content1".getBytes());
        Files.write(file2, "content2".getBytes());

        Instant oldTime = Instant.now().minus(Duration.ofMinutes(11));
        Files.setLastModifiedTime(file1, FileTime.from(oldTime));
        Files.setLastModifiedTime(file2, FileTime.from(oldTime));

        given(binaryContentRepository.existsById(id1)).willReturn(false);
        given(binaryContentRepository.existsById(id2)).willReturn(false);

        // when
        storage.cleanOrphanFiles();

        // then - 예외 없이 완료되어야 함
        assertThat(Files.exists(file1)).isFalse();
        assertThat(Files.exists(file2)).isFalse();
    }
}
