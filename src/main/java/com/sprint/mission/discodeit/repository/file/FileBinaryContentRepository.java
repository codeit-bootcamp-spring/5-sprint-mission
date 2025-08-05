package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    private static final String DATA_DIR = "data.dir";
    private final String filename;
    protected final Map<UUID, BinaryContent> data;

    public FileBinaryContentRepository(){
        this.filename = DATA_DIR + "/binaryContents.ser";
        this.data = readFromFile();
    }

    @Override
    public BinaryContent save(BinaryContent entity) {
        data.put(entity.getId(), entity);
        writeToFile();
        return entity;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.of(data.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return List.of();
    }

    public List<BinaryContent> findAll() {
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

    private Map<UUID, BinaryContent> readFromFile() {
        File file = new File(filename);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
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
