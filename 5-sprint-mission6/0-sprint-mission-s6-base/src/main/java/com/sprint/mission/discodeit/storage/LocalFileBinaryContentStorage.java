package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LocalFileBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalFileBinaryContentStorage(Environment env) {
    String rootPath = env.getProperty("discodeit.storage.local.root-path", ".discodeit/files");
    this.root = Path.of(rootPath);
  }

  @PostConstruct
  void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  @Override
  public UUID put(UUID id, byte[] data) {
    try {
      Files.write(resolvePath(id), data,
          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      return id;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      return Files.newInputStream(resolvePath(id), StandardOpenOption.READ);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto meta) {
    try {
      Path p = resolvePath(meta.id());
      if (!Files.exists(p)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
      Resource body = new InputStreamResource(Files.newInputStream(p));
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + safeFilename(meta.fileName()) + "\"")
          .contentType(MediaType.parseMediaType(meta.contentType()))
          .contentLength(meta.size())
          .body(body);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  private String safeFilename(String name) {
    return (name == null) ? "file" : name.replace("\"", "");
  }
}