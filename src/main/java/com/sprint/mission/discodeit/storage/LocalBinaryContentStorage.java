package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
    this.root = Paths.get(rootPath);
    init();
  }

  private void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize storage", e);
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  @Override
  public UUID put(UUID id, byte[] data) {
    try {
      Files.write(resolvePath(id), data);
      return id;
    } catch (IOException e) {
      throw new RuntimeException("Failed to save file", e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      return Files.newInputStream(resolvePath(id));
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file", e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentResponse contentDto) {
    try {
      InputStream inputStream = get(contentDto.id());
      InputStreamResource resource = new InputStreamResource(inputStream);

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentDto.contentType()))
          .contentLength(contentDto.size())
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + contentDto.fileName() + "\"")
          .body(resource);

    } catch (Exception e) {
      throw new RuntimeException("Download failed", e);
    }
  }
}

