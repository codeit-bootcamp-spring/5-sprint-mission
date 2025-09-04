package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.StorageProperties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;
    private final BinaryContentRepository binaryContentRepository;

    public LocalBinaryContentStorage(
        StorageProperties props,
        BinaryContentRepository binaryContentRepository
    ) {
        this.root = Paths.get(props.local().rootPath());
        this.binaryContentRepository = binaryContentRepository;
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

    // 용량이 크면 OutOfMemoryError 발생 가능하지만 추후 원격 스토리지 직접 업로드로 변경
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
            Resource resource = new ByteArrayResource(Files.readAllBytes(filePath));

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

    @Scheduled(fixedDelay = 300_000)
    @Transactional(readOnly = true)
    public void cleanOrphanFiles() {
        if (!Files.isDirectory(root)) {
            log.warn("스토리지 디렉토리가 없습니다: {}", root);
            return;
        }

        Set<UUID> ids = binaryContentRepository.findAllIds();

        List<Path> orphans;
        try (Stream<Path> stream = Files.list(root)) {
            orphans = stream
                .filter(Files::isRegularFile)
                .filter(p -> {
                    String name = p.getFileName().toString();
                    try {
                        UUID id = UUID.fromString(name);
                        return !ids.contains(id);
                    } catch (IllegalArgumentException ignore) {
                        return true;
                    }
                })
                .toList();
        } catch (IOException e) {
            log.error("스토리지 디렉토리 탐색 실패: {}", root, e);
            return;
        }

        int deleted = 0;
        for (Path file : orphans) {
            try {
                Files.deleteIfExists(file);
                deleted++;
                log.info("고아 파일 삭제: {}", file.getFileName());
            } catch (IOException e) {
                log.error("고아 파일 삭제 실패: {}", file.getFileName(), e);
            }
        }
        log.info("고아 파일 정리 완료. 삭제 {}건.", deleted);
    }
}
