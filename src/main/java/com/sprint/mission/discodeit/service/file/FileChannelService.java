package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final Path directory;

    public Path getDirectory() {
        return directory;
    }

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

    private void save(Channel channel) {
        Path channelDirectory = Path.of(directory.toString() + "/" + channel.getId());

        try (FileOutputStream fos = new FileOutputStream(channelDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    public void create(Channel channel) {
        if (!load(directory).contains(channel)) {
            save(channel);
        } else {
            System.out.println("이미 존재하는 채널입니다.");
        }
    }

    @Override
    public void update(Channel channel) {
        for (Channel c : load(directory)) {
            if (c.equals(channel)) {
                System.out.println("변경 사항이 없습니다.");
                return;
            }
        }
        delete(channel);
        save(channel);
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
            System.out.println("존재하지 않는 채널입니다.");
        }
    }

    @Override
    public void deleteAll() {
        try {
            Files.deleteIfExists(directory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel searchByIndex(int i) {
        return null;
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
    public Channel searchById(UUID id) {
        for (Channel channel : load(directory)) {
            if (channel.getId().equals(id)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> searchAll() {
        return load(directory);
    }
}
