package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.config.properties.StorageProperties;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
@Slf4j
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    private final Path root;
    private final Duration orphanGrace;

    private final BinaryContentRepository binaryContentRepository;

    public LocalBinaryContentStorage(
        BinaryContentRepository binaryContentRepository,
        StorageProperties props
    ) {
        this.root = Paths.get(props.local().rootPath());

        this.orphanGrace = props.local().orphanGrace() != null
            ? props.local().orphanGrace()
            : Duration.ofMinutes(10);

        this.binaryContentRepository = binaryContentRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
            if (!Files.isWritable(root)) {
                throw new IOException("경로에 쓰기 권한이 없습니다: " + root);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("로컬 스토리지 디렉토리 생성 실패: " + root, e);
        }
    }

    private Path resolvePath(UUID binaryContentId) {
        return root.resolve(binaryContentId.toString());
    }

    @Override
    public UUID put(
        UUID binaryContentId,
        byte[] bytes
    ) {
        log.debug("로컬 스토리지 파일 저장 시도: id={}, size={}", binaryContentId, bytes.length);

        try {
            Files.write(
                resolvePath(binaryContentId),
                bytes,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            log.info("로컬 스토리지 파일 저장 완료: id={}", binaryContentId);

            return binaryContentId;
        } catch (IOException e) {
            log.error("로컬 스토리지 파일 저장 실패: id={}", binaryContentId, e);

            throw new UncheckedIOException("파일 저장 실패: " + binaryContentId, e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        log.debug("로컬 스토리지 파일 조회 시도: id={}", binaryContentId);

        try {
            return Files.newInputStream(resolvePath(binaryContentId), StandardOpenOption.READ);
        } catch (IOException e) {
            log.error("로컬 스토리지 파일 조회 실패: id={}", binaryContentId, e);
            throw new UncheckedIOException("파일 읽기 실패: " + binaryContentId, e);
        }
    }

    @Override
    public Resource getResource(UUID binaryContentId) {
        log.debug("로컬 스토리지 파일 리소스 조회 시도: binaryContentId={}", binaryContentId);

        Path filePath = resolvePath(binaryContentId);

        try {
            if (!Files.isRegularFile(filePath)) {
                throw new NoSuchFileException(filePath.toString());
            }

            return new InputStreamResource(Files.newInputStream(filePath, StandardOpenOption.READ));
        } catch (NoSuchFileException e) {
            log.warn("파일을 찾을 수 없습니다. binaryContentId={}", binaryContentId);

            throw new BinaryContentNotFoundException();
        } catch (IOException e) {
            log.error("로컬 스토리지 파일 읽기 실패: binaryContentId={}", binaryContentId, e);

            throw new UncheckedIOException("로컬 스토리지 파일 읽기 실패: binaryContentId={%s}".formatted(binaryContentId), e);
        }
    }

    @Scheduled(fixedDelay = 300_000)
    public void cleanOrphanFiles() {
        if (!Files.isDirectory(root)) {
            log.warn("스토리지 디렉토리가 없습니다: {}", root);
            return;
        }

        Instant threshold = Instant.now().minus(orphanGrace);

        int deleted = 0;

        try (Stream<Path> stream = Files.list(root)) {
            for (Path file : stream.filter(Files::isRegularFile).toList()) {
                try {
                    FileTime fileTime = Files.getLastModifiedTime(file);
                    if (fileTime.toInstant().isAfter(threshold)) {
                        continue;
                    }

                    String name = file.getFileName().toString();
                    UUID id;

                    if (!UUID_PATTERN.matcher(name).matches()) {
                        Files.deleteIfExists(file);
                        deleted++;

                        log.info("형식 불량 파일 삭제: {}", name);

                        throw new IllegalArgumentException("Invalid UUID format");
                    }

                    id = UUID.fromString(name);

                    log.info("고아 파일 검사 중: {}", id);

                    boolean exists = binaryContentRepository.existsById(id);
                    if (!exists) {
                        Files.deleteIfExists(file);
                        deleted++;
                        log.info("고아 파일 삭제: {}", name);
                    }
                } catch (Exception e) {
                    log.error("고아 파일 처리 실패: {}", file.getFileName(), e);
                }
            }
        } catch (Exception e) {
            log.error("스토리지 디렉토리 탐색 실패: {}", root, e);
            return;
        }

        if (deleted > 0) {
            log.info("고아 파일 정리 완료. 삭제 {}건.", deleted);
        } else {
            log.info("고아 파일 정리 완료. 삭제된 파일이 없습니다.");
        }
    }
}
