package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.properties.StorageProperties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
@Slf4j
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;
    private final Duration orphanGrace;

    private final BinaryContentRepository binaryContentRepository;

    public LocalBinaryContentStorage(
        StorageProperties props,
        BinaryContentRepository binaryContentRepository
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

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }

    @Override
    public UUID put(
        UUID id,
        byte[] bytes
    ) {
        log.debug("로컬 스토리지 파일 저장 시도: id={}, size={}", id, bytes.length);

        try {
            Files.write(
                resolvePath(id),
                bytes,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            log.info("로컬 스토리지 파일 저장 완료: id={}", id);

            return id;
        } catch (IOException e) {
            log.error("로컬 스토리지 파일 저장 실패: id={}", id, e);

            throw new UncheckedIOException("파일 저장 실패: " + id, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        log.debug("로컬 스토리지 파일 조회 시도: id={}", id);

        try {
            return Files.newInputStream(resolvePath(id), StandardOpenOption.READ);
        } catch (IOException e) {
            log.error("로컬 스토리지 파일 조회 실패: id={}", id, e);
            throw new UncheckedIOException("파일 읽기 실패: " + id, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        UUID id = binaryContentDto.id();

        log.debug("파일 다운로드 응답 생성 시도: binaryContentId={}", id);

        Path filePath = resolvePath(id);

        try {
            if (!Files.isRegularFile(filePath)) {
                throw new NoSuchFileException(filePath.toString());
            }

            long size = Files.size(filePath);

            MediaType mediaType;
            try {
                mediaType = (binaryContentDto.contentType() != null && !binaryContentDto.contentType().isBlank())
                    ? MediaType.parseMediaType(binaryContentDto.contentType())
                    : MediaType.APPLICATION_OCTET_STREAM;
            } catch (InvalidMediaTypeException e) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }

            String safeName = sanitizeFilename(binaryContentDto.fileName());
            ContentDisposition cd = ContentDisposition.attachment()
                .filename(safeName, StandardCharsets.UTF_8)
                .build();

            InputStreamResource resource = new InputStreamResource(
                Files.newInputStream(
                    filePath,
                    StandardOpenOption.READ
                )
            );

            ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString());

            if (size >= 0) {
                builder.contentLength(size);
            }

            log.debug("파일 다운로드 응답 생성 완료: binaryContentId={}", id);

            return builder.body(resource);
        } catch (NoSuchFileException e) {
            throw new UncheckedIOException("파일을 찾을 수 없습니다: " + id, e);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 다운로드 실패: " + id, e);
        }
    }

    private String sanitizeFilename(String original) {
        if (original == null || original.isBlank()) {
            return "file";
        }

        String cleaned = original
            .replace("\r", "")
            .replace("\n", "")
            .replace("\t", "")
            .replace("/", "")
            .replace("\\", "")
            .replace("\"", "");

        return cleaned.isBlank() ? "file" : cleaned;
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
                    FileTime lm = Files.getLastModifiedTime(file);
                    if (lm.toInstant().isAfter(threshold)) {
                        continue;
                    }

                    String name = file.getFileName().toString();
                    UUID id;
                    try {
                        id = UUID.fromString(name);
                        log.error(id.toString());
                    } catch (IllegalArgumentException bad) {
                        Files.deleteIfExists(file);
                        deleted++;
                        log.info("형식 불량 파일 삭제: {}", name);
                        continue;
                    }

                    boolean exists = binaryContentRepository.existsById(id);
                    if (!exists) {
                        Files.deleteIfExists(file);
                        deleted++;
                        log.info("고아 파일 삭제: {}", name);
                    }
                } catch (IOException e) {
                    log.error("고아 파일 처리 실패: {}", file.getFileName(), e);
                }
            }
        } catch (IOException e) {
            log.error("스토리지 디렉토리 탐색 실패: {}", root, e);
            return;
        }

        if (deleted > 0) {
            log.info("고아 파일 정리 완료. 삭제 {}건.", deleted);
        }
    }
}
