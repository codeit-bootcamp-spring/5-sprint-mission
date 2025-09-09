package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            // 루트 디렉토리가 존재하지 않으면 생성
            if (!Files.exists(root)) {
                Files.createDirectories(root);
                log.info("루트 디렉토리 생성 완료: {}", root.toAbsolutePath());
            } else {
                log.info("루트 디렉토리 확인 완료: {}", root.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("루트 디렉토리 초기화 실패: {}", root.toAbsolutePath(), e);
            throw new RuntimeException("루트 디렉토리 초기화 실패", e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path filePath = resolvePath(id);

        try {
            // 파일의 부모 디렉토리가 없으면 생성
            Files.createDirectories(filePath.getParent());

            // 파일 저장
            Files.write(filePath, bytes);
            log.info("파일 저장 완료: {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("파일 저장 실패: {}", filePath.toAbsolutePath(), e);
            throw new RuntimeException("파일 저장 실패: " + filePath.toAbsolutePath(), e);
        }
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        Path filePath = resolvePath(id);

        if (!Files.exists(filePath)) {
            log.warn("파일이 존재하지 않습니다: {}", id);
            throw new RuntimeException("파일이 존재하지 않습니다: " + id);
        }

        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            log.error("파일 읽기 실패: {}", filePath.toAbsolutePath(), e);
            throw new RuntimeException("파일 읽기 실패: " + filePath.toAbsolutePath(), e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        try {
            Path filePath = resolvePath(binaryContentDto.id());

            if (!Files.exists(filePath)) {
                log.warn("다운로드할 파일이 존재하지 않습니다: {}", binaryContentDto.id());
                return ResponseEntity.notFound().build();
            }

            // FileSystemResource 사용 (더 효율적)
            Resource resource = new FileSystemResource(filePath);

            // 파일명 설정 (null이면 ID 사용)
            String fileName = binaryContentDto.fileName() != null ?
                    binaryContentDto.fileName() :
                    binaryContentDto.id().toString();

            // Content-Type 설정 (기본값: application/octet-stream)
            String contentType = binaryContentDto.contentType() != null ?
                    binaryContentDto.contentType() :
                    "application/octet-stream";

            ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType);

            // 파일 크기가 있으면 Content-Length 헤더 추가
            if (binaryContentDto.size() != null) {
                responseBuilder.header(HttpHeaders.CONTENT_LENGTH,
                        String.valueOf(binaryContentDto.size()));
            }

            return responseBuilder.body(resource);

        } catch (Exception e) {
            log.error("파일 다운로드 실패: {}", binaryContentDto.id(), e);
            return ResponseEntity.notFound().build();
        }
    }

    private Path resolvePath(UUID binaryContentId) {
        return root.resolve(binaryContentId.toString());
    }
}