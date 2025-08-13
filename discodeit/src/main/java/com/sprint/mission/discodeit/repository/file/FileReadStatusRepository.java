package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository("fileReadStatusRepository")
@Profile("file")
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private final Map<UUID,ReadStatus> data;

    public FileReadStatusRepository() {
        this.data=new HashMap<>();
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", ReadStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }


    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            data.put(readStatus.getId(),readStatus);
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        ReadStatus statusNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                statusNullable = (ReadStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return statusNullable;
    }

    @Override
    public List<ReadStatus> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            data.remove(id);
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
