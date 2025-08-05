package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.service.BaseService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("CallToPrintStackTrace")
public abstract class BaseFileService<T extends BaseEntity> implements BaseService<T> {
    protected final Map<UUID, T> data = new HashMap<>();
    private final String storageFile;

    protected BaseFileService(String storageFile) {
        this.storageFile = storageFile;
        load();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(storageFile);
        if (!file.exists() || file.length() == 0) {
            return; // 비어있거나 존재하지 않으면 무시
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storageFile))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?> m) {
                m.forEach((k, v) -> data.put((UUID) k, (T) v));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storageFile))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void update(UUID id, Consumer<T> updater) {
        T entity = getOrThrow(id);
        updater.accept(entity);
        entity.touch();
        saveToFile();
    }

    @Override
    public List<T> findAll() {
        return data.values().stream().filter(e -> !e.isDeleted()).toList();
    }

    @Override
    public List<T> findAllIncludingDeleted() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<T> findById(UUID id) {
        T entity = data.get(id);
        return (entity == null || entity.isDeleted()) ? Optional.empty() : Optional.of(entity);
    }

    @Override
    public List<T> findAllByIds(Collection<UUID> ids) {
        return ids.stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .filter(e -> !e.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public T getOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new NoSuchElementException("엔티티를 찾을 수 없습니다: " + id));
    }

    @Override
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("엔티티는 null일 수 없습니다.");
        }
        data.put(entity.getId(), entity);
        saveToFile();
        return entity;
    }

    @Override
    public boolean hardDeleteById(UUID id) {
        boolean removed = data.remove(id) != null;
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    @Override
    public boolean deleteById(UUID id) {
        Optional<T> target =
                data.values().stream().filter(e -> !e.isDeleted() && e.getId().equals(id)).findFirst();
        target.ifPresent(BaseEntity::delete);
        if (target.isPresent()) {
            saveToFile();
        }
        return target.isPresent();
    }

    @Override
    public boolean restoreById(UUID id) {
        Optional<T> target =
                data.values().stream().filter(e -> e.isDeleted() && e.getId().equals(id)).findFirst();
        target.ifPresent(BaseEntity::restore);
        if (target.isPresent()) {
            saveToFile();
        }
        return target.isPresent();
    }

    @Override
    public long count() {
        return data.values().stream().filter(e -> !e.isDeleted()).count();
    }
}
