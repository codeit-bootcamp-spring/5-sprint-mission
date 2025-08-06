package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FileChannelRepository implements ChannelRepository {

    private final Path DIRECTORY_PATH = Paths.get("./data/channels");

    public FileChannelRepository() {
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
    public Channel save(Channel channel) {
        try(FileOutputStream fos = new FileOutputStream(getChannelFilePath(channel.getId()).toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create channel: " + channel, e);
        }

        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
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

        return Optional.ofNullable(channel);
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
    public void deleteById(UUID id) {
        try {
            Files.delete(getChannelFilePath(id));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read channel: " + id, e);
        }
    }
}