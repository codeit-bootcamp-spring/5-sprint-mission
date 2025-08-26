package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION;

    public FileChannelRepository() {
        this.DIRECTORY = Path.of("CHANNEL");
        this.EXTENSION = ".ser";
        if (!DIRECTORY.toFile().exists()) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Channel save(Channel channel) {
        Path path = DIRECTORY.resolve(channel.getId() + EXTENSION);
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
            oos.flush();
            return channel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel;
        Path path = DIRECTORY.resolve(id + EXTENSION);
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            channel = (Channel) ois.readObject();
            return Optional.of(channel);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Channel> findAll() {
        if (Files.isDirectory(DIRECTORY)) {
            try {
                List<Channel> channels = Files.list(DIRECTORY)
                        .filter(path -> path.toString().endsWith(EXTENSION))
                        .map(paths -> {
                                    try (FileInputStream fis = new FileInputStream(paths.toFile());
                                         ObjectInputStream ois = new ObjectInputStream(fis)) {
                                        Channel channel = (Channel) ois.readObject();
                                        return channel;
                                    } catch (IOException | ClassNotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        )
                        .toList();
                return channels;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public long count() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream.filter(path -> (path.toString().endsWith(EXTENSION))).count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel delete(UUID id) {
        Path path = DIRECTORY.resolve(id + EXTENSION);
        try {
            Channel channel = findById(id).orElseThrow(() -> new RuntimeException("채널에서 해당 " + id + "를 찾을 수 없습니다."));
            Files.deleteIfExists(path);
            return channel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean existsById(UUID id) {
        Path path = DIRECTORY.resolve(id + EXTENSION);
        return Files.exists(path);
    }
}
