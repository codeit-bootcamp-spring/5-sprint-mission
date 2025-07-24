package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {

    private final Path directory;

    public FileChannelRepository(Path directory) {
        this.directory = directory;
        initPath(directory);
    }

    private void initPath(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void save(Channel channel) {
        Path channelDirectory = Path.of(directory.toString() + "/" + channel.getId());

        try (FileOutputStream fos = new FileOutputStream(channelDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Channel> load(Path directory) {
        if (Files.exists(directory)) {
            try {
                List<Channel> channels = Files.list(directory)
                        .map(path -> {
                            try (FileInputStream fis = new FileInputStream(path.toFile());
                                 ObjectInputStream ois = new ObjectInputStream(fis);) {
                                Object data = ois.readObject();
                                return (Channel) data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }).toList();
                return channels;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void delete(Channel channel) {
        Path channelDirectory = Path.of(directory.toString() + "/" + channel.getId());
        if (Files.exists(channelDirectory)) {
            try {
                Files.delete(channelDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("해당하는 채널을 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
    }

    @Override
    public void deleteAll() {
        for (Channel channel : load(directory)) {
            delete(channel);
        }
    }

    @Override
    public List<Channel> searchByName(String name) {
        List<Channel> channels = new ArrayList<>();
        for (Channel channel : load(directory)) {
            if (channel.getName().contains(name)) {
                channels.add(channel);
            }
        }
        if (channels.isEmpty()) {
            System.err.println("해당하는 채널을 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return channels;
    }

    @Override
    public Channel searchById(UUID id) {
        for (Channel c : load(directory)) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        System.err.println("해당하는 채널을 찾을 수 없습니다.");
        throw new NoSuchElementException();
    }

    @Override
    public List<Channel> searchAll() {
        return load(directory);
    }
}
