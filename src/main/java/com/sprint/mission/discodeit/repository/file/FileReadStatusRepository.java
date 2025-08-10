package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository("fileReadStatusRepository")
public class FileReadStatusRepository implements ReadStatusRepository {

    private static final String DIRECTORY = "file-data-map/ReadStatus";
    private final Path directoryPath;

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory}")String fileDirectory) {
        this.directoryPath = Paths.get(System.getProperty("user.dir"), DIRECTORY);
        if (Files.notExists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new RuntimeException("Directory creation failed", e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return directoryPath.resolve(id + ".ser");
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(readStatus);
            return readStatus;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save ReadStatus", e);
        }
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        try {
            Files.list(directoryPath)
                    .filter(path -> path.toString().contains(channelId.toString()))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete ReadStatus", e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete ReadStatus by ChannelId", e);
        }
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        try {
            return Files.list(directoryPath)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(rs -> rs.getChannelId().equals(channelId))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UUID> findChannelIdsByUserId(UUID userId) {
        try {
            return Files.list(directoryPath)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(rs -> rs.getUserId().equals(userId))
                    .map(ReadStatus::getChannelId)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        try {
            return Files.list(directoryPath)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(rs -> rs.getUserId().equals(userId))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UUID> findUserIdsByChannelId(UUID channelId) {
        return findByChannelId(channelId).stream()
                .map(ReadStatus::getUserId)
                .distinct()
                .collect(Collectors.toList());
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
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete ReadStatus with id " + id, e);
        }
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                ReadStatus readStatus = (ReadStatus) ois.readObject();
                return Optional.of(readStatus);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Failed to read ReadStatus with id " + id, e);
            }
        }
        return Optional.empty();
    }
}
