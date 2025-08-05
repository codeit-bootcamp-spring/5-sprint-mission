package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BaseEntity;

import java.io.*;
import java.util.*;

public abstract class AbstractFileRepository<T extends BaseEntity> {
    private final String filename;
    protected final Map<UUID, T> data;

    protected AbstractFileRepository(String basePath, String entityName) {
        this.filename = basePath + "/" + entityName + ".ser";
        this.data = readFromFile();
    }

    public T save(T entity) {
        data.put(entity.getId(), entity);
        writeToFile();
        return entity;
    }

    public Optional<T> findById(UUID id) {
        return Optional.of(data.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    public void deleteAll() {
        data.clear();
        writeToFile();
    }

    public void delete(UUID id) {
        data.remove(id);
        writeToFile();
    }

    private Map<UUID, T> readFromFile() {
        File file = new File(filename);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, T>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void writeToFile() {
        File file = new File(filename);
        try {
            file.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            // 따로 처리 필요하면...
        }
    }
}