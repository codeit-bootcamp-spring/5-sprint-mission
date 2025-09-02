package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.StorageProperties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(StorageProperties props) {
        this.root = Paths.get(props.local().rootPath());
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new UncheckedIOException("로컬 스토리지 디렉토리 생성 실패: " + root, e);
        }
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        try {
            Files.write(resolvePath(id), bytes,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return id;
        } catch (IOException e) {
            throw new UncheckedIOException("파일 저장 실패: " + id, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        try {
            return Files.newInputStream(resolvePath(id), StandardOpenOption.READ);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 읽기 실패: " + id, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto bcd) {
        UUID id = bcd.id();
        Path filePath = resolvePath(id);

        try {
            byte[] data = Files.readAllBytes(filePath);
            Resource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(bcd.contentType()))
                .contentLength(bcd.size())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + bcd.fileName() + "\"")
                .body(resource);

        } catch (IOException e) {
            throw new UncheckedIOException("파일 다운로드 실패: " + id, e);
        }
    }

    public void delete(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new UncheckedIOException("파일 삭제 실패: " + id, e);
        }
    }
}
