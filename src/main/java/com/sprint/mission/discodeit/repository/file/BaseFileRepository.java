package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.repository.BaseRepository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class BaseFileRepository<T extends BaseEntity> implements BaseRepository<T> {
    private static final String EXTENSION = ".ser";

    private final Path directory;
    private final Class<T> type;

    protected BaseFileRepository(Class<T> type) {
        this.type = type;
        this.directory = Paths.get(System.getProperty("user.dir"), "file-data-map", type.getSimpleName());

        try {
            if (Files.notExists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + directory, e);
        }
    }

    protected Path resolvePath(UUID id) {
        return directory.resolve(id + EXTENSION);
    }

    private Optional<T> readObject(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return Optional.of(type.cast(ois.readObject()));
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public T save(T entity) {
        if (entity == null) throw new IllegalArgumentException("entity must not be null");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resolvePath(entity.getId()).toFile()))) {
            oos.writeObject(entity);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save entity: " + entity.getId(), e);
        }
        return entity;
    }

    @Override
    public Optional<T> findById(UUID id) {
        Path path = resolvePath(id);
        if (!Files.exists(path)) return Optional.empty();
        return readObject(path).filter(e -> !e.isDeleted());
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
    public boolean deleteById(UUID id) {
        Optional<T> opt = findById(id);
        opt.ifPresent(BaseEntity::delete);
        opt.ifPresent(this::save);
        return opt.isPresent();
    }

    @Override
    public boolean hardDeleteById(UUID id) {
        try {
            return Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("Failed to hard delete entity: " + id, e);
        }
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
    public long count() {
        return findAll().size();
    }
}