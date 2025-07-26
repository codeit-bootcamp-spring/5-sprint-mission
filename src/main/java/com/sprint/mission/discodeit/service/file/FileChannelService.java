package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        try (FileOutputStream fos = new FileOutputStream(channelDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    private List<Channel> load(Path directory) {
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
    public Channel create(Channel channel) {
        return save(channel);
    }

    @Override
    public Channel update(Channel channel) {
        return save(channel);
    }

    @Override
    public Channel delete(UUID id) {
        Path channelDirectory = Path.of(directory.toString() + "/" + id);
        Channel channel = searchById(id).orElse(null);
        if (Files.exists(channelDirectory)) {
            try {
                Files.delete(channelDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("존재하지 않는 채널입니다.");
        }
        return channel;
    }

    @Override
    public void deleteAll() {
        for (Channel channel : load(directory)) {
            delete(channel.getId());
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

        return channels;
    }

    @Override
    public Optional<Channel> searchById(UUID id) {
        Channel c = null;
        for (Channel channel : load(directory)) {
            if (channel.getId().equals(id)) {
                c = channel;
            }
        }
        return Optional.ofNullable(c);
    }

    @Override
    public List<Channel> searchAll() {
        return load(directory);
    }
}
