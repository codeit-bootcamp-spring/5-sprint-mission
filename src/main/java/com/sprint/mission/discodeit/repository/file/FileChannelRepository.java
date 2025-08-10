package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
<<<<<<< HEAD
import java.util.*;
=======
import java.util.List;
import java.util.Optional;
import java.util.UUID;
>>>>>>> 717adae (feat: 초기 커밋)

public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
<<<<<<< HEAD
        if (!Files.exists(DIRECTORY)) {
=======
        if (Files.notExists(DIRECTORY)) {
>>>>>>> 717adae (feat: 초기 커밋)
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

<<<<<<< HEAD
    private Path resolvePath(UUID channelId) {
        return DIRECTORY.resolve(channelId.toString() + EXTENSION);
=======
    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
>>>>>>> 717adae (feat: 초기 커밋)
    }

    @Override
    public Channel save(Channel channel) {
        Path path = resolvePath(channel.getId());
<<<<<<< HEAD
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
=======
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
>>>>>>> 717adae (feat: 초기 커밋)
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
<<<<<<< HEAD
    public Optional<Channel> find(UUID channelId) {
        Channel channelNullable = null;
        Path path = resolvePath(channelId);
        if (Files.exists(path)) {
            try (FileInputStream fis = new FileInputStream(path.toFile());
                 ObjectInputStream ois = new ObjectInputStream(fis)
=======
    public Optional<Channel> findById(UUID id) {
        Channel channelNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
>>>>>>> 717adae (feat: 초기 커밋)
            ) {
                channelNullable = (Channel) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(channelNullable);
    }

    @Override
    public List<Channel> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
<<<<<<< HEAD
                try (FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
                ) { return (Channel) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
=======
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
>>>>>>> 717adae (feat: 초기 커밋)
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
<<<<<<< HEAD
    public boolean existById(UUID channelId) {
        Path path = resolvePath(channelId);
=======
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
>>>>>>> 717adae (feat: 초기 커밋)
        return Files.exists(path);
    }

    @Override
<<<<<<< HEAD
    public void delete(UUID channelId) {
        Path path = resolvePath(channelId);
        try {
            Files.delete(path);
        } catch (IOException e) {
        throw new RuntimeException(e);}
=======
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
>>>>>>> 717adae (feat: 초기 커밋)
    }
}
