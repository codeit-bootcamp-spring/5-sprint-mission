package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final String DIRECTORY;
    private final String EXTENSION;

    public FileChannelService() {
        this.DIRECTORY = "USER";
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
    public Channel createChannel(ChannelType type, String name) {
        Channel channel = new Channel(type,name);
        Path path = Paths.get(DIRECTORY, channel.getId() + EXTENSION);
        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(channel);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public Channel find(UUID channelId) {
        Channel channel = null;
        Path path = Paths.get(DIRECTORY, channelId.toString() + EXTENSION);
        try(FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis)){
            channel = (Channel) ois.readObject();
        }catch (Exception e) {
            throw new NoSuchElementException("존재하지 않는 채널입니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        Path DIRECTORY = Paths.get("USER");
        try {
            return Files.list(DIRECTORY)
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
    public Channel update(UUID channelId, ChannelType type,String name) {
        Path path = Paths.get(DIRECTORY, channelId.toString() + EXTENSION);
        Channel channel = find(channelId);
        channel.update(type,name);

        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널 정보 업데이트 실패!" + e);
        }

        return channel;
    }

    @Override
    public Channel delete(UUID userId) {
        Path path = Paths.get(DIRECTORY, userId.toString() + EXTENSION);
        Channel channel = find(userId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }
}
