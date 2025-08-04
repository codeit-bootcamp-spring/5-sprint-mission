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

    public Channel save(Channel channel) {
        Path channelDirectory = Path.of(directory.toString() + "/" + channel.getId());

        if (Files.exists(channelDirectory)) {
            delete(channel.getId());
        }
        try (FileOutputStream fos = new FileOutputStream(channelDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
            return channel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<UUID, Channel> load(Path directory) {
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
                Map<UUID, Channel> channelMap = new HashMap<>();
                channels.forEach(channel -> channelMap.put(channel.getId(), channel));
                return channelMap;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public Optional<Channel> delete(UUID id) {
        Path channelDirectory = Path.of(directory.toString() + "/" + id);
        Channel channel = searchById(id).orElse(null);
        if (Files.exists(channelDirectory)) {
            try {
                Files.delete(channelDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(channel);
    }

    @Override
    public void deleteAll() {
        for (Channel channel : load(directory).values()) {
            delete(channel.getId());
        }
    }

    @Override
    public List<Channel> searchByName(String name) {
        List<Channel> channels = new ArrayList<>();
        for (Channel channel : load(directory).values()) {
            if (channel.getName().contains(name)) {
                channels.add(channel);
            }
        }
        return channels;
    }

    @Override
    public Optional<Channel> searchById(UUID id) {
        return Optional.ofNullable(load(directory).get(id));
    }

    @Override
    public List<Channel> searchAll() {
        return new ArrayList<>(load(directory).values());
    }
}
