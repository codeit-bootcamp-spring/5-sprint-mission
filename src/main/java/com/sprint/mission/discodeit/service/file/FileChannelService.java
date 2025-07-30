package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileChannelService implements ChannelService {


    private final Path directory;

    public FileChannelService(Path directory) {
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

    private Channel save(Channel channel) {
        Path channelDirectory = Path.of(directory.toString() + "/" + channel.getId());

        if (Files.exists(channelDirectory)) {
            delete(channel.getId());
        }
        try (FileOutputStream fos = new FileOutputStream(channelDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
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
    public Channel create(Channel channel) {
        return save(channel);
    }

    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = searchById(id);
        channel.updateName(name);
        return save(channel);
    }

    @Override
    public Channel updateDescription(UUID id, String description) {
        Channel channel = searchById(id);
        channel.updateDescription(description);
        return save(channel);
    }

    @Override
    public Channel updateChannelType(UUID id, Channel.ChannelType channelType) {
        Channel channel = searchById(id);
        channel.updateChannelType(channelType);
        return save(channel);
    }

    @Override
    public Channel delete(UUID id) {
        Path channelDirectory = Path.of(directory.toString() + "/" + id);
        Channel channel = searchById(id);
        if (Files.exists(channelDirectory)) {
            try {
                Files.delete(channelDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return channel;
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
        if (channels.isEmpty()) {
            throw new NoSuchElementException("해당하는 채널을 찾을 수 없습니다.");
        }
        return channels;
    }

    @Override
    public Channel searchById(UUID id) {
        if (!load(directory).containsKey(id)) {
            throw new NoSuchElementException("해당하는 채널을 찾을 수 없습니다.");
        }
        return load(directory).get(id);
    }

    @Override
    public List<Channel> searchAll() {
        return new ArrayList<>(load(directory).values());
    }
}
