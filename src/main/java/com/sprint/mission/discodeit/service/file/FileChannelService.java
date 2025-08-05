package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelService() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), Channel.class.getSimpleName());
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
    public Channel create(String name, String description) {
        Channel channel = new Channel(name, description);
        Path path = resolvePath(channel.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public Channel get(UUID id) {
        Channel channel = null;
        Path path = resolvePath(id);
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            channel = (Channel) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public List<Channel> getAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (Channel) ois.readObject();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel updateName(UUID uuid, String name) {
        return null;
    }

    @Override
    public Channel updateDescription(UUID uuid, String description) {
        return null;
    }

    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("찾을 수 없습니다.");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
