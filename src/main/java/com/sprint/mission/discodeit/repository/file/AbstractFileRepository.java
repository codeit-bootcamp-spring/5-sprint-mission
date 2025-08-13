package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BaseEntity;

import java.io.*;
import java.util.*;

public abstract class AbstractFileRepository<T extends BaseEntity> {
    private final String fileName;
    protected final Map<UUID, T> dataMap;

    protected AbstractFileRepository(String fileName) {
        this.fileName = fileName;
        dataMap = loadFile();
    }

    public void save(T entity) {
        dataMap.put(entity.getId(), entity);
        writeToFile();
    }

    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(dataMap.get(id));
    }

    public List<T> findAll() {
        return dataMap.values().stream()
                .toList();
    }

    public void update(UUID id, T updatedEntity) {
        save(updatedEntity);
    }

    public boolean delete(UUID id) {
        if (dataMap.remove(id) == null) return false;
        writeToFile();
        return true;
    }

    private Map<UUID, T> loadFile() {
        File file = new File(fileName + ".dat");
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, T>) ois.readObject();
        }
        catch (IOException | ClassNotFoundException ignored) {
            return new HashMap<>();
        }
    }

    protected void writeToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(dataMap);
        } catch (IOException ignored) {}
    }
}
