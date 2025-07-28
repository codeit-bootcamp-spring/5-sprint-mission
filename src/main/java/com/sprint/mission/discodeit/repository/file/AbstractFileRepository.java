package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BaseEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractFileRepository<T extends BaseEntity> {
    private final String fileName;
    protected final List<T> dataList;

    protected AbstractFileRepository(String fileName) {
        this.fileName = fileName;
        dataList = loadFile();
    }

    public void save(T entity) {
        dataList.add(entity);
        writeToFile();
    }

    public T findById(UUID id) {
        return dataList.stream()
                .filter(entity -> entity.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<T> findAll() {
        return dataList;
    }

    public void update(UUID id, T updatedEntity) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId().equals(id)) {
                dataList.set(i, updatedEntity);
                writeToFile();
                break;
            }
        }
    }

    public void delete(UUID id) {
        dataList.remove(findById(id));
    }

    private List<T> loadFile() {
        File file = new File(fileName + ".dat");
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        }
        catch (IOException | ClassNotFoundException ignored) {
            return new ArrayList<>();
        }
    }

    protected void writeToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(dataList);
        } catch (IOException ignored) {}
    }
}
