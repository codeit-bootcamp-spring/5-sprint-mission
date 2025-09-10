package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.configuration.StorageProps;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.exception.ThrowableIOException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  LocalBinaryContentStorage(StorageProps props) {
    root = Paths.get(System.getProperty("user.dir"), props.getRootPath());
  }

  @PostConstruct
  private void init() {
    if (Files.notExists(root)) {
      try {
        Files.createDirectories(root);
      } catch (IOException e) {
        throw new ThrowableIOException("파일 저장에 실패하였습니다. : " + root, e);
      }
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }


  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path path = resolvePath(id);
    try {
      if (Files.notExists(path)) {
        Files.write(path, bytes);
      }
    } catch (IOException e) {
      throw new ThrowableIOException("파일 저장에 실패하였습니다. : " + path, e);
    }
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    Path path = resolvePath(id);
    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw new ThrowableIOException("파일 읽기에 실패하였습니다. : " + path, e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto dto) {
    Path path = resolvePath(dto.id());

    if (Files.notExists(path)) {
      throw new NoSuchElementException("파일이 존재하지 않습니다. : " + path);
    }

    Resource resource = new InputStreamResource(get(dto.id()));

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(dto.contentType()))
        .contentLength(dto.size())
        .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
            .attachment()
            .filename(dto.fileName(), StandardCharsets.UTF_8)
            .build()
            .toString())
        .body(resource);
  }
}
