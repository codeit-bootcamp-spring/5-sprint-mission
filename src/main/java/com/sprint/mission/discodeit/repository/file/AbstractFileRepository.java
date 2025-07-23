package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BaseEntity;

import java.io.*;
import java.util.*;

@SuppressWarnings("unchecked")
public abstract class AbstractFileRepository<T extends BaseEntity> {
    private static final String DATA_DIR = "data.dir";
    private final String filename;
    protected final Map<UUID, T> data;

    protected AbstractFileRepository(String entityName) {
        this.filename = DATA_DIR + "/" + entityName + ".ser";
        this.data = readFromFile();
    }

    public T save(T entity) {
        data.put(entity.getId(), entity);
        writeToFile();
        return entity;
    }

    public T findById(UUID id) {
        return data.get(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    public void deleteById(UUID id) {
        data.remove(id);
        writeToFile();
    }

    public void deleteAll() {
        data.clear();
        writeToFile();
    }

    private Map<UUID, T> readFromFile() {
        File file = new File(filename);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
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
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file: " + filename, e);
        }
    }
}