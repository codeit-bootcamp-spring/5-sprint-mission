package com.codeit.mission.discodeit.storage;

import com.codeit.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
        @Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory: " + root, e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path filePath = resolvePath(id);

        try {
            Files.write(filePath, bytes);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save binary content", e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path filePath = resolvePath(id);

        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found: " + id);
        }

        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read binary content", e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        try {
            InputStream inputStream = get(binaryContentDto.id());
            Resource resource = new InputStreamResource(inputStream);

            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            if (binaryContentDto.contentType() != null && !binaryContentDto.contentType()
                .isEmpty()) {
                try {
                    mediaType = MediaType.parseMediaType(binaryContentDto.contentType());
                } catch (Exception ignored) {
                }
            }

            String fileName = binaryContentDto.fileName();
            if (fileName == null || fileName.isEmpty()) {
                fileName = binaryContentDto.id().toString();
            }

            return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_LENGTH,
                    String.valueOf(binaryContentDto.size()))
                .body(resource);

        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("File not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
