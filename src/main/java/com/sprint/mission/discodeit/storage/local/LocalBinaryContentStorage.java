package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.binarycontent.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
@Slf4j
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
        @Value("${discodeit.storage.local.root-path:.discodeit/storage}")
        Path root
    ) {
        this.root = root;
    }

    @PostConstruct
    public void init() {
        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new UncheckedIOException("로컬 스토리지 디렉토리 생성 실패: " + root, e);
            }
        }
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
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
    public ResponseEntity<Resource> download(BinaryContentDto metaData) {
        log.debug("로컬 스토리지 파일 다운로드 시도: id={}", metaData.id());

        try {
            InputStream inputStream = Files.newInputStream(
                resolvePath(metaData.id()), StandardOpenOption.READ);
            Resource resource = new InputStreamResource(inputStream);

            ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(metaData.fileName(), StandardCharsets.UTF_8)
                .build();

            return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .header(HttpHeaders.CONTENT_TYPE, metaData.contentType())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metaData.size()))
                .body(resource);
        } catch (IOException e) {
            log.error("로컬 스토리지 파일 다운로드 실패: id={}", metaData.id(), e);
            throw new UncheckedIOException("파일 다운로드 실패: " + metaData.id(), e);
        }
    }

    private Path resolvePath(UUID key) {
        return root.resolve(key.toString());
    }
}
