package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final String DIRECTORY;
    private final String EXTENSION;

    public FileChannelService() {
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
    public Channel create(ChannelType type, String name, String description) {
        if (type == null || name == null || name.isBlank() || description == null || description.isBlank()) {
            throw new IllegalArgumentException("channel type or name or description is null or blank");
        }

        Channel channel = new Channel(type, name, description);
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
    public Channel find(UUID channelId) {
        Channel channel = null;
        Path path = Paths.get(DIRECTORY, channelId.toString() + EXTENSION);
        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            channel = (Channel) ois.readObject();
        } catch (Exception e) {
            throw new NoSuchElementException("channel not found");
        }

        if (channel == null) {
            throw new NoSuchElementException("channel not found");
        }

        return channel;
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
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channelNullable = null;
        Path path = Paths.get(DIRECTORY, channelId.toString() + EXTENSION);

        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                channelNullable = (Channel) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        Channel channel = Optional.ofNullable(channelNullable)
                .orElseThrow(() -> new NoSuchElementException("channel not found"));
        channel.update(newName, newDescription);

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
    public void delete(UUID channelId) {
        Path path = Paths.get(DIRECTORY, channelId.toString() + EXTENSION);

        if (Files.notExists(path)) {
            throw new NoSuchElementException("channel not found");
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}