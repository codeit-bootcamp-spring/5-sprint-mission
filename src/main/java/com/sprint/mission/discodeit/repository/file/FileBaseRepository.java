package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.entitydev.DevBaseEntity;
import com.sprint.mission.discodeit.repository.BaseRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
public abstract class FileBaseRepository<T extends DevBaseEntity> implements BaseRepository<T> {

    private static final String EXTENSION = ".ser";
    private final Class<T> entityType;
    private final Path directory;

    protected FileBaseRepository(Class<T> entityType, AppStorageProperties storageProperties) {
        this.entityType = entityType;
        this.directory = Paths.get(System.getProperty("user.dir"), storageProperties.rootDir(), entityType.getSimpleName());

        try {
            if (Files.notExists(directory)) Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + directory, e);
        }
    }

    protected Path resolvePath(UUID id) {
        if (id == null) throw new IllegalArgumentException("id must not be null");
        return directory.resolve(id + EXTENSION);
    }

    private boolean isSerializedFile(Path path) {
        return Files.isRegularFile(path) && path.getFileName().toString().endsWith(EXTENSION);
    }

    private List<Path> listSerializedFiles() {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.filter(this::isSerializedFile).toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to list serialized files: " + directory, e);
        }
    }

    private Optional<T> readObject(Path path) {
        try {
            if (path == null || !Files.exists(path)) return Optional.empty();
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
                return Optional.of(entityType.cast(ois.readObject()));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read entity from storage: " + path, e);
        }
    }

    @Override
    public T save(T entity) {
        if (entity == null) throw new IllegalArgumentException("entity must not be null");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resolvePath(entity.getId()).toFile()))) {
            oos.writeObject(entity);
            return entity;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save entity: " + entity.getId(), e);
        }
    }

    @Override
    public List<T> saveAll(Collection<T> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::save).toList();
    }

    @Override
    public Optional<T> findById(UUID id) {
        return readObject(resolvePath(id)).filter(e -> !e.isDeleted());
    }

    @Override
    public T getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
                new NoSuchElementException("엔티티(" + entityType.getSimpleName() + ")를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<T> findAll() {
        return listSerializedFiles().stream()
                .map(this::readObject).flatMap(Optional::stream)
                .filter(e -> !e.isDeleted())
                .toList();
    }

    @Override
    public List<T> findAllIncludingDeleted() {
        return listSerializedFiles().stream()
                .map(this::readObject).flatMap(Optional::stream)
                .toList();
    }

    @Override
    public List<T> findAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        return ids.stream()
                .map(this::resolvePath)
                .map(this::readObject).flatMap(Optional::stream)
                .filter(e -> !e.isDeleted())
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public boolean existsAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return false;
        return ids.stream().allMatch(this::existsById);
    }

    @Override
    public boolean deleteById(UUID id) {
        Optional<T> opt = readObject(resolvePath(id));
        if (opt.isPresent() && !opt.get().isDeleted()) {
            opt.get().delete();
            save(opt.get());
            return true;
        }
        return false;
    }

    @Override
    public int deleteAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;

        int count = 0;
        for (UUID id : ids) {
            Optional<T> opt = readObject(resolvePath(id));
            if (opt.isPresent() && !opt.get().isDeleted()) {
                opt.get().delete();
                save(opt.get());
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean restoreById(UUID id) {
        Optional<T> opt = readObject(resolvePath(id));
        if (opt.isPresent() && opt.get().isDeleted()) {
            opt.get().restore();
            save(opt.get());
            return true;
        }
        return false;
    }

    @Override
    public int restoreAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;

        int count = 0;
        for (UUID id : ids) {
            Optional<T> opt = readObject(resolvePath(id));
            if (opt.isPresent() && opt.get().isDeleted()) {
                opt.get().restore();
                save(opt.get());
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean hardDeleteById(UUID id) {
        try {
            return Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            log.warn("Failed to hard delete {}: {}", entityType.getSimpleName(), id, e);
            return false;
        }
    }

    @Override
    public int hardDeleteAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;

        int deletedCount = 0;
        for (UUID id : ids) {
            try {
                if (Files.deleteIfExists(resolvePath(id))) deletedCount++;
            } catch (IOException e) {
                log.warn("Failed to hard delete {} (batch): {}", entityType.getSimpleName(), id, e);
            }
        }
        return deletedCount;
    }

    @Override
    public long count() {
        return listSerializedFiles().stream()
                .map(this::readObject).flatMap(Optional::stream)
                .filter(e -> !e.isDeleted())
                .count();
    }

    @Override
    public long count(Predicate<T> condition) {
        Objects.requireNonNull(condition, "condition must not be null");
        return listSerializedFiles().stream()
                .map(this::readObject).flatMap(Optional::stream)
                .filter(e -> !e.isDeleted())
                .filter(condition)
                .count();
    }
}