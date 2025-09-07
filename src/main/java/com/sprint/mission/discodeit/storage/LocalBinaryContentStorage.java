package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage, InitializingBean {

    private final Path root;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path:storage}") String rootPath
    ) {
        this.root = Paths.get(rootPath).toAbsolutePath().normalize();
    }

    /** Bean 생성 시 루트 디렉토리 준비 */
    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    void init() throws IOException {
        Files.createDirectories(root);
        log.info("[LocalBinaryContentStorage] root={}", root);
    }

    /** 파일 실제 경로 규칙: {root}/{UUID} */
    Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }

    @Override
    public UUID put(UUID id, byte[] data) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(data, "data");
        try {
            Files.write(resolvePath(id), data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store binary: " + id, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        try {
            return Files.newInputStream(resolvePath(id), StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read binary: " + id, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        try {
            InputStream is = get(dto.id()); // 저장소에서 원본 읽기
            InputStreamResource resource = new InputStreamResource(is);

            String contentType = dto.contentType() != null ? dto.contentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            if (dto.size() != null && dto.size() >= 0) {
                headers.setContentLength(dto.size());
            }
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(dto.fileName(), StandardCharsets.UTF_8)
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (RuntimeException ex) {
            throw ex;
        }
    }
}
