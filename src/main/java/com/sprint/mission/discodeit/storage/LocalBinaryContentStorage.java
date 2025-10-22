package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.exception.binarycontent.FileIOErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
public class LocalBinaryContentStorage implements BinaryContentStorage {
    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path:.discodeit/storage}") String root) {
        this.root = Path.of(root);
        init();
    }

    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw FileIOErrorException.withStorage(root.toString(), e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        log.info("로컬 파일 저장 시도");
        log.debug("저장 시도하는 파일 ID: {}", id);
        Path filePath = resolvePath(id);
        try {
            Files.write(filePath, bytes);
        } catch (IOException e) {
            log.warn("파일 저장 실패: {}", filePath, e);
            throw FileIOErrorException.withStorage(filePath.toString(), e);
        }
        log.info("로컬 파일 저장 성공");
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        Path filePath = resolvePath(id);
        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw FileIOErrorException.withStorage(filePath.toString(), e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Resource> download(BinaryContentDTO binaryContentDTO) {
        log.info("로컬 파일 다운로드 시도");
        log.debug("다운로드 시도하는 파일 정보: {}", binaryContentDTO);
        Path filePath = resolvePath(binaryContentDTO.getId());

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        try {
            InputStream inputStream = get(binaryContentDTO.getId());
            InputStreamResource resource = new InputStreamResource(inputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + binaryContentDTO.getFileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(Files.size(filePath))
                    .contentType(MediaType.parseMediaType(binaryContentDTO.getContentType()))
                    .body(resource);

        } catch (IOException e) {
            log.warn("파일 다운로드 실패: {}", filePath, e);
            throw FileIOErrorException.withStorage(binaryContentDTO.getFileName(), e);
        }
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
