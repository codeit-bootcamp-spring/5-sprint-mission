package com.sprint.mission.discodeit.storage;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    name = "discodeit.storage.type",
    havingValue = "local",
    matchIfMissing = true
)
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") Path root) {
    this.root = root;
  }

  @PostConstruct
  public void init() throws IOException {
    if (!Files.exists(root)) {
      Files.createDirectories(root);
    }
  }

  @Override
  public UUID put(UUID id, byte[] data) {
    Path filePath = resolvePath(id);
    try (OutputStream os = Files.newOutputStream(filePath)) {
      os.write(data);
    } catch (IOException e) {
      throw new RuntimeException("Failed to store binary content", e);
    }
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    Path filePath = resolvePath(id);
    try {
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read binary content", e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(UUID id) {
    Path filePath = resolvePath(id);
    Resource resource = new FileSystemResource(filePath.toFile());
    return ResponseEntity
        .ok()
        .header("Content-Disposition", "attachment; filename=\"" + id + "\"")
        .body(resource);
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }
}
