package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.configuration.StorageProps;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.StorageInitException;
import com.sprint.mission.discodeit.exception.binarycontent.StorageNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.StorageReadException;
import com.sprint.mission.discodeit.exception.binarycontent.StorageWriteException;
import com.sprint.mission.discodeit.log.LogUtils;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Slf4j
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
        throw StorageInitException.withDetail("root", root, e);
      }
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }


  @Override
  public UUID put(UUID id, byte[] bytes) {
    log.debug("[LocalBinaryContentStorage#put] try with id: {}", id);
    Path path = resolvePath(id);
    try {
      if (Files.notExists(path)) {
        Files.write(path, bytes);
      }
    } catch (IOException e) {
      throw StorageWriteException.withDetail("path", path, e);
    }
    log.info("[LocalBinaryContentStorage#put] file uploaded: id={}, filename={}", id,
        path.getFileName());

    return id;
  }

  @Override
  public InputStream get(UUID id) {
    Path path = resolvePath(id);
    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw StorageReadException.withDetail("path", path, e);
    }
  }

  @Override
  public Resource download(BinaryContentDto dto) {
    log.debug("[LocalBinaryContentStorage#download] try: {}", LogUtils.summarizeAttachment(dto));
    Path path = resolvePath(dto.id());

    if (Files.notExists(path)) {
      throw StorageNotFoundException.withDetail("path", path);
    }

    Resource resource = new InputStreamResource(get(dto.id()));
    log.info("[LocalBinaryContentStorage#download] downloaded: {}",
        LogUtils.summarizeAttachment(dto));

    return resource;
  }
}
