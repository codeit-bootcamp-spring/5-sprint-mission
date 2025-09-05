package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") String rootPath) {
    this.root = Paths.get(rootPath).toAbsolutePath().normalize();
  }

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(root);
  }

  private Path resolvePath(UUID id) { return root.resolve(id.toString()); }

  @Override
  public UUID put(UUID id, byte[] bytes) throws IOException {
    Files.write(resolvePath(id), bytes,
        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    return id;
  }

  @Override
  public InputStream get(UUID id) throws IOException {
    return Files.newInputStream(resolvePath(id), StandardOpenOption.READ);
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto dto) throws IOException {
    InputStreamResource resource = new InputStreamResource(get(dto.id()));
    MediaType mediaType = Optional.ofNullable(dto.contentType())
        .map(MediaType::parseMediaType)
        .orElse(MediaType.APPLICATION_OCTET_STREAM);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + dto.fileName() + "\"")
        .contentType(mediaType)
        .contentLength(dto.size() == null ? -1 : dto.size())
        .body(resource);
  }
}