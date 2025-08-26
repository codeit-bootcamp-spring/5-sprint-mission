package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ChannelParticipant;
import com.sprint.mission.discodeit.repository.ChannelParticipantRepository;
import com.sprint.mission.discodeit.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileChannelParticipantRepository implements ChannelParticipantRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelParticipantRepository(@Value("${discodeit.repository.file-path}") String directory) {
        this.DIRECTORY = Paths.get(directory, ChannelParticipant.class.getSimpleName());
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
    public ChannelParticipant save(ChannelParticipant channelParticipant) {
        Path path = resolvePath(channelParticipant.getId());
        FileUtils.writeObjectToFile(channelParticipant, path);
        return channelParticipant;
    }

    @Override
    public Optional<ChannelParticipant> findByUserId(UUID id) {
        ChannelParticipant cp = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (FileInputStream fis = new FileInputStream(path.toFile());
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                cp = (ChannelParticipant) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(cp);
    }

    @Override
    public List<ChannelParticipant> findAllByChannelId(UUID channelId) {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (ChannelParticipant) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(cp -> cp.getChannelId().equals(channelId))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChannelParticipant> findAllByUserId(UUID userId) {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (ChannelParticipant) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(cp -> cp.getUserId().equals(userId))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByChannelIdAndUserId(UUID channelId, UUID userId) {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (ChannelParticipant) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .anyMatch(cp -> cp.getChannelId().equals(channelId) && cp.getUserId().equals(userId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByChannelIdAndUserId(UUID channelId, UUID userId) {
        try {
            Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .forEach(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            ChannelParticipant cp = (ChannelParticipant) ois.readObject();
                            if (cp.getChannelId().equals(channelId) && cp.getUserId().equals(userId)) {
                                Files.delete(path);
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
