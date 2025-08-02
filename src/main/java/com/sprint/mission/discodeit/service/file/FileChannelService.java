package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileChannelService implements ChannelService {

    private final Path DIRECTORY_PATH = Paths.get("./data/channels");

    public FileChannelService() {
        if (Files.notExists(DIRECTORY_PATH)) {
            try {
                Files.createDirectories(DIRECTORY_PATH);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + DIRECTORY_PATH, e);
            }
        }
    }

    private Path getChannelFilePath(UUID id) {
        return DIRECTORY_PATH.resolve(id + ".ser");
    }

    @Override
    public Channel create(String name, String description, ChannelType type) {
        Channel channel = new Channel(name,
                description,
                type
        );

        try(FileOutputStream fos = new FileOutputStream(getChannelFilePath(channel.getId()).toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
            return channel;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create channel: " + channel, e);
        }
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel;

        try (FileInputStream fis = new FileInputStream(getChannelFilePath(id).toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {

            channel = (Channel) ois.readObject();

        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Channel with id " + id + " not found");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read channel: " + id, e);
        }

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        try (Stream<Path> pathStream = Files.list(DIRECTORY_PATH)) {
            return pathStream
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (Channel) data;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read channel", e);
        }
    }

    @Override
    public Channel update(UUID id, String name, String description, ChannelType type) {
        File originalFile = getChannelFilePath(id).toFile();
        File tempFile = new File(getChannelFilePath(id) + ".tmp");

        Channel channel;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(originalFile));
             ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))
        ) {

            channel = (Channel) ois.readObject();
            channel.update(name, description, type);
            oos.writeObject(channel);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read channel: " + id, e);
        }

        if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
            throw new RuntimeException("Failed to replace channel file after update");
        }

        return channel;
    }

    @Override
    public void delete(UUID id) {
        try {
            Files.delete(getChannelFilePath(id));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read channel: " + id, e);
        }
    }
}
