package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final String DIRECTORY;
    private final String EXTENSION;

    public FileChannelRepository() {
        this.DIRECTORY = "CHANNEL";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if (!path.toFile().exists()) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Channel save(Channel channel) {
        Path path = Paths.get(DIRECTORY, channel.getId() + EXTENSION);
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel = null;
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                channel = (Channel) ois.readObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.ofNullable(channel);
    }

    @Override
    public List<Channel> findAll() {
        Path directory = Paths.get(DIRECTORY);

        try {
            return Files.list(directory)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        long count = 0;
        Path directory = Paths.get(DIRECTORY);

        try {
            if (Files.exists(directory)) {
                count = Files.list(directory)
                        .filter(path -> path.toString().endsWith(EXTENSION))
                        .count();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    @Override
    public void delete(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existById(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        return Files.exists(path);
    }
}
