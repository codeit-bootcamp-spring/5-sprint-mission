package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.entity.BaseEntity;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BaseRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
public abstract class FileBaseRepository<T extends BaseEntity> implements BaseRepository<T> {

    protected static final String EXTENSION = ".ser";

    private final Class<T> entityType;
    private final Path directory;

    protected FileBaseRepository(Class<T> entityType, AppStorageProperties storageProperties) {
        this.entityType = entityType;
        this.directory = Paths.get(System.getProperty("user.dir"), storageProperties.rootDir(), entityType.getSimpleName());
        try {
            if (Files.notExists(directory)) Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("디렉터리 생성 실패: " + directory, e);
        }
    }

    protected Path getDirectory() {
        return directory;
    }

    protected Path resolvePath(UUID id) {
        if (id == null) throw new IllegalArgumentException("id must not be null");
        return directory.resolve(id + EXTENSION);
    }

    protected List<Path> listSerializedFiles() {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.filter(this::isSerializedFile).toList();
        } catch (IOException e) {
            throw new RuntimeException("저장 파일 나열 실패: " + directory, e);
        }
    }

    protected Optional<T> readObject(Path path) {
        try {
            if (path == null || !Files.exists(path)) return Optional.empty();
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
                return Optional.of(entityType.cast(ois.readObject()));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("엔티티 로드 실패: " + path, e);
        }
    }

    protected void writeObject(T entity) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resolvePath(entity.getId()).toFile()))) {
            oos.writeObject(entity);
        } catch (IOException e) {
            throw new RuntimeException("엔티티 저장 실패: " + entity.getId(), e);
        }
    }

    private boolean isSerializedFile(Path path) {
        return Files.isRegularFile(path) && path.getFileName().toString().endsWith(EXTENSION);
    }

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        writeObject(entity);
        return entity;
    }

    @Override
    public List<T> saveAll(Collection<T> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream().map(this::save).toList();
    }

    @Override
    public Optional<T> findById(UUID id) {
        return readObject(resolvePath(id)).filter(e -> !e.isDeleted());
    }

    @Override
    public Optional<T> findByIdIncludingDeleted(UUID id) {
        return readObject(resolvePath(id));
    }

    @Override
    public T getOrThrow(UUID id) {
        return findById(id).orElseThrow(() ->
                new NotFoundException("엔티티(" + entityType.getSimpleName() + ")를 찾을 수 없습니다: " + id));
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
    public List<T> findAllDeleted() {
        return listSerializedFiles().stream()
                .map(this::readObject).flatMap(Optional::stream)
                .filter(BaseEntity::isDeleted)
                .toList();
    }

    @Override
    public List<T> findAllByIds(Collection<UUID> ids) {
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
    public boolean existsAllByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return false;
        return ids.stream().allMatch(this::existsById);
    }

    @Override
    public boolean softDeleteById(UUID id) {
        Optional<T> opt = readObject(resolvePath(id));
        if (opt.isPresent() && !opt.get().isDeleted()) {
            T e = opt.get();
            e.delete();
            writeObject(e);
            return true;
        }
        return false;
    }

    @Override
    public int softDeleteAllByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        int count = 0;
        for (UUID id : ids) if (softDeleteById(id)) count++;
        return count;
    }

    @Override
    public boolean restoreById(UUID id) {
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
    public int restoreAllByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        int count = 0;
        for (UUID id : ids) if (restoreById(id)) count++;
        return count;
    }

    @Override
    public boolean hardDeleteById(UUID id) {
        try {
            return Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            log.warn("hard delete 실패({}): {}", entityType.getSimpleName(), id, e);
            return false;
        }
    }

    @Override
    public int hardDeleteAllByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        int deleted = 0;
        for (UUID id : ids) {
            try {
                if (Files.deleteIfExists(resolvePath(id))) deleted++;
            } catch (IOException e) {
                log.warn("hard delete in batch 실패({}): {}", entityType.getSimpleName(), id, e);
            }
        }
        return deleted;
    }

    @Override
    public int hardDeleteAllExpired(Instant now) {
        Instant ref = (now != null) ? now : Instant.now();
        List<UUID> toRemove = listSerializedFiles().stream()
                .map(this::readObject).flatMap(Optional::stream)
                .filter(BaseEntity::isDeleted)
                .filter(e -> e.shouldPurge(ref))
                .map(BaseEntity::getId)
                .toList();
        int deleted = 0;
        for (UUID id : toRemove) if (hardDeleteById(id)) deleted++;
        return deleted;
    }

    @Override
    public long count() {
        return listSerializedFiles().stream()
                .map(this::readObject).flatMap(Optional::stream)
                .filter(e -> !e.isDeleted())
                .count();
    }

    @Override
    public long countIncludingDeleted() {
        return listSerializedFiles().size();
    }
}
