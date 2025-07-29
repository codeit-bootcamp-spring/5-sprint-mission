package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final String DIRECTORY;
    private final String EXTENSION;
    private final UserRepository userRepository;

    public FileChannelService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.DIRECTORY = "CHANNEL/ChannelService";
        this.EXTENSION = ".ser";
        Path path =  Paths.get(DIRECTORY);
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Channel createChannel(UUID userId, String channelName, ChannelType channelType, boolean nsfw) {
        if(userId == null || !userRepository.existsById(userId)) {
            throw new NullPointerException("A channel object is empty.");
        } if(channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("channelName is null or blank.");
        } if(channelType == null) {
            throw new IllegalArgumentException("channelType is null.");
        }

        Channel channel = new Channel(userId, channelName, channelType, nsfw);
        Path path = Paths.get(DIRECTORY, channel.getId() + EXTENSION);

        try (FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(channel);
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel findById(UUID channelId) {
        Channel channel = null;
        Path path = Paths.get(DIRECTORY, channelId + EXTENSION);

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            channel = (Channel) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();
        File dir = new File(DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(EXTENSION));
        if(files != null) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);) {
                    Channel channel = (Channel) ois.readObject();
                    channels.add(channel);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return channels;
    }

    @Override
    public Channel update(UUID channelId, UUID ownerId, String channelName, boolean nsfw) {
        Path path = Paths.get(DIRECTORY, channelId + EXTENSION);
        Channel channel = null;

        if(!userRepository.existsById(ownerId)) {
            throw new IllegalArgumentException("[Error] : 사용자가 존재하지 않습니다.");
        }

        try (FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);) {
            if (path.toFile().exists()) {
                channel = (Channel) ois.readObject();
                channel.update(ownerId, channelName, nsfw);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try (FileOutputStream fos = new FileOutputStream(path.toFile(), false);
        ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public Channel deleteById(UUID channelId) {
        Path path = Paths.get(DIRECTORY, channelId + EXTENSION);
        Channel channel = null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            if (path.toFile().exists()) {
                channel = (Channel) ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return channel;
    }
}
