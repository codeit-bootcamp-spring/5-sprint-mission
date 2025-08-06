package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.service.BaseService;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class BaseFileService<T extends BaseEntity> implements BaseService<T> {
    private static final String EXTENSION = ".ser";

    private final Path directory;
    private final Class<T> type;

    protected BaseFileService(Class<T> type) {
        if (type == null) throw new IllegalArgumentException("Type must not be null.");
        this.directory = Paths.get(System.getProperty("user.dir"), "file-data-map", type.getSimpleName());
        try {
            if (Files.notExists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directory", e);
        }

        this.type = type;
    }

    private Path resolvePath(UUID id) {
        return directory.resolve(id + EXTENSION);
    }

    private Optional<T> readObject(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile()); ObjectInputStream ois = new ObjectInputStream(fis)) {
            return Optional.of(type.cast(ois.readObject()));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read entity from storage", e);
        }
    }

    protected void update(UUID id, Consumer<T> updater) {
        T entity = getOrThrow(id);
        updater.accept(entity);
        entity.touch();
        save(entity);
    }

    @Override
    public List<T> findAll() {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.filter(path -> path.toString().endsWith(EXTENSION)).map(this::readObject)
                    .flatMap(Optional::stream).filter(entity -> !entity.isDeleted()).toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load non-deleted entities from: " + directory, e);
        }
    }

    @Override
    public List<T> findAllIncludingDeleted() {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.filter(path -> path.toString().endsWith(EXTENSION)).map(this::readObject).flatMap(Optional::stream).toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load non-deleted entities from: " + directory, e);
        }
    }

    @Override
    public Optional<T> findById(UUID id) {
        Path path = resolvePath(id);
        if (!Files.exists(path)) return Optional.empty();
        return readObject(path).filter(e -> !e.isDeleted());
    }

    @Override
    public T getOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new NoSuchElementException("엔티티를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<T> findAllByIds(Set<UUID> ids) {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.filter(path -> path.toString().endsWith(EXTENSION)).map(this::readObject)
                    .flatMap(Optional::stream).filter(entity -> ids.contains(entity.getId())).toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load non-deleted entities from: " + directory, e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public T save(T entity) {
        if (entity == null) throw new IllegalArgumentException("엔티티는 null일 수 없습니다.");
        Path path = resolvePath(entity.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile()); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public boolean hardDeleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to hard delete file: " + path, e);
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        Optional<T> opt = readObject(resolvePath(id));
        if (opt.isPresent() && !opt.get().isDeleted()) {
            T entity = opt.get();
            entity.delete();
            save(entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean restoreById(UUID id) {
        Optional<T> opt = readObject(resolvePath(id));
        if (opt.isPresent() && opt.get().isDeleted()) {
            T entity = opt.get();
            entity.restore();
            save(entity);
            return true;
        }
        return false;
    }

    @Override
    public long count() {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.filter(path -> path.toString().endsWith(EXTENSION)).map(this::readObject).flatMap(Optional::stream).count();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load non-deleted entities from: " + directory, e);
        }
    }
}
