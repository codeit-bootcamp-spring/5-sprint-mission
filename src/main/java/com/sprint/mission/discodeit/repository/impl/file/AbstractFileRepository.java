package com.sprint.mission.discodeit.repository.impl.file;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import com.sprint.mission.discodeit.domain.entity.AbstractEntity;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.AbstractRepository;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractFileRepository<T extends AbstractEntity> implements
    AbstractRepository<T> {

  private static final String EXTENSION = ".ser";

  private final Class<T> entityType;

  @Getter(AccessLevel.PROTECTED)
  protected final Path directory;

  protected AbstractFileRepository(Class<T> entityType) {
    this.entityType = entityType;
    this.directory = Paths.get(System.getProperty("user.dir"),
        entityType.getSimpleName());
    try {
      Files.createDirectories(directory);
    } catch (IOException e) {
      log.warn("Failed to create directory: {}", directory, e);
      throw new RuntimeException("Failed to create directory: " + directory, e);
    }
  }

  protected Path resolvePath(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    return directory.resolve(id + EXTENSION);
  }

  protected List<Path> listSerializedFiles() {
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory, "*" + EXTENSION)) {
      return StreamSupport.stream(ds.spliterator(), false).toList();
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  protected Stream<Path> streamSerializedFiles() throws IOException {
    DirectoryStream<Path> ds = Files.newDirectoryStream(directory, "*" + EXTENSION);
    return StreamSupport.stream(ds.spliterator(), false).onClose(() -> {
      try {
        ds.close();
      } catch (IOException e) {
        log.warn("Failed to list save file streams: {}", directory, e);
      }
    });
  }

  protected Optional<T> readObject(Path path) {
    if (path == null || !Files.exists(path)) {
      return Optional.empty();
    }
    try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(path));
        ObjectInputStream ois = new ObjectInputStream(bis)) {
      return Optional.of(entityType.cast(ois.readObject()));
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("Entity load failed: " + path, e);
    }
  }

  protected void writeObject(T entity) {
    Path target = resolvePath(entity.getId());
    Path tmp = target.resolveSibling(entity.getId() + EXTENSION + ".tmp");
    try (BufferedOutputStream bos = new BufferedOutputStream(
        Files.newOutputStream(tmp, CREATE, TRUNCATE_EXISTING, WRITE));
        ObjectOutputStream oos = new ObjectOutputStream(bos)) {
      oos.writeObject(entity);
      oos.flush();
      try {
        Files.move(tmp, target, ATOMIC_MOVE, REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException ex) {
        Files.move(tmp, target, REPLACE_EXISTING);
      }
    } catch (IOException e) {
      try {
        Files.deleteIfExists(tmp);
      } catch (IOException e2) {
        e.addSuppressed(e2);
      }
      throw new RuntimeException("Failed to save entity: " + entity.getId(), e);
    }
  }

  @Override
  public T save(T entity) {
    Objects.requireNonNull(entity, "entity must not be null");
    writeObject(entity);
    return entity;
  }

  @Override
  public List<T> saveAll(Collection<T> entities) {
    if (entities == null || entities.isEmpty()) {
      return List.of();
    }
    return entities.stream().map(this::save).toList();
  }

  @Override
  public List<T> findAll() {
    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .filter(AbstractEntity::isNotDeleted)
          .toList();
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  @Override
  public List<T> findAllIncludingDeleted() {
    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream).toList();
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  @Override
  public List<T> findAllDeleted() {
    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .filter(AbstractEntity::isDeleted)
          .toList();
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  @Override
  public List<UUID> findAllIds() {
    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .filter(AbstractEntity::isNotDeleted)
          .sorted(Comparator.comparing(AbstractEntity::getCreatedAt).reversed())
          .map(AbstractEntity::getId)
          .toList();
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }


  @Override
  public List<T> findAllByIdIn(Collection<UUID> ids) {
    return ids.stream()
        .map(this::resolvePath)
        .map(this::readObject).flatMap(Optional::stream)
        .filter(AbstractEntity::isNotDeleted)
        .sorted(Comparator.comparing(AbstractEntity::getCreatedAt).reversed())
        .toList();
  }

  @Override
  public List<T> findAllByIdIncludingDeleted(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return ids.stream()
        .map(this::resolvePath)
        .map(this::readObject).flatMap(Optional::stream)
        .toList();
  }

  @Override
  public Optional<T> find(UUID id) {
    return readObject(resolvePath(id)).filter(AbstractEntity::isNotDeleted);
  }

  @Override
  public Optional<T> findIncludingDeleted(UUID id) {
    return readObject(resolvePath(id));
  }

  @Override
  public Map<UUID, Instant> findAllCreatedAtById(Set<UUID> ids) {
    return ids.stream()
        .map(this::resolvePath)
        .map(this::readObject).flatMap(Optional::stream)
        .filter(AbstractEntity::isNotDeleted)
        .filter(e -> ids.contains(e.getId()))
        .collect(Collectors.toMap(
            AbstractEntity::getId,
            AbstractEntity::getCreatedAt
        ));
  }

  @Override
  public T getOrThrow(UUID id) {
    return find(id).orElseThrow(() ->
        new NotFoundException("%s with id %s not found".formatted(entityType.getSimpleName(), id)));
  }

  @Override
  public boolean existsById(UUID id) {
    return readObject(resolvePath(id)).map(AbstractEntity::isNotDeleted).orElse(false);
  }

  @Override
  public boolean delete(UUID id) {
    Optional<T> opt = readObject(resolvePath(id));
    if (opt.isPresent() && opt.get().isNotDeleted()) {
      T e = opt.get();
      e.delete();
      writeObject(e);
      return true;
    }
    return false;
  }

  @Override
  public void deleteAllByIdIn(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return;
    }
    ids.forEach(this::delete);
  }

  @Override
  public boolean restore(UUID id) {
    Optional<T> opt = readObject(resolvePath(id));
    if (opt.isPresent() && opt.get().isDeleted()) {
      T e = opt.get();
      e.restore();
      writeObject(e);
      return true;
    }
    return false;
  }

  @Override
  public int restoreAllByIdIn(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }
    int count = 0;
    for (UUID id : ids) {
      if (restore(id)) {
        count++;
      }
    }
    return count;
  }

  @Override
  public boolean hardDelete(UUID id) {
    try {
      return Files.deleteIfExists(resolvePath(id));
    } catch (IOException e) {
      log.warn("Failed to hard-delete({}): {}", entityType.getSimpleName(), id, e);
      return false;
    }
  }

  @Override
  public int hardDeleteAllByIdIn(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }
    int deleted = 0;
    int failed = 0;
    for (UUID id : ids) {
      try {
        if (Files.deleteIfExists(resolvePath(id))) {
          deleted++;
        }
      } catch (IOException e) {
        failed++;
      }
    }
    if (failed > 0) {
      log.warn("Failed to batch-hard-delete({}): {} failed", entityType.getSimpleName(), failed);
    }
    return deleted;
  }

  @Override
  public int hardDeleteAllExpired(Instant now) {
    Instant ref = (now != null) ? now : Instant.now();
    List<UUID> toRemove;
    try (Stream<Path> s = streamSerializedFiles()) {
      toRemove = s.map(this::readObject).flatMap(Optional::stream)
          .filter(AbstractEntity::isDeleted)
          .filter(e -> e.shouldPurge(ref))
          .map(AbstractEntity::getId)
          .toList();
    } catch (IOException e) {
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
    int deleted = 0;
    for (UUID id : toRemove) {
      if (hardDelete(id)) {
        deleted++;
      }
    }
    return deleted;
  }

  @Override
  public long count() {
    long cnt = 0;
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory, "*" + EXTENSION)) {
      for (Path p : ds) {
        Optional<T> e = readObject(p);
        if (e.isPresent() && e.get().isNotDeleted()) {
          cnt++;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
    return cnt;
  }

  @Override
  public long countIncludingDeleted() {
    long cnt = 0;
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory, "*" + EXTENSION)) {
      for (Path ignored : ds) {
        cnt++;
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
    return cnt;
  }
}