package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private static final String CHANNEL_DATA_DIR = "channel_data";

    public FileChannelRepository() {
        File dataDir = new File(CHANNEL_DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private String getChannelFilePath(UUID channelId) {
        return CHANNEL_DATA_DIR + File.separator + channelId.toString() + ".ser";
    }

    @Override
    public Channel save(Channel channel) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getChannelFilePath(channel.getId())))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getChannelFilePath(id)))) {
            return Optional.of((Channel) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();
        File dataDir = new File(CHANNEL_DATA_DIR);
        File[] channelFiles = dataDir.listFiles((dir, name) -> name.endsWith(".ser"));
        if (channelFiles != null) {
            for (File file : channelFiles) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    channels.add((Channel) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return channels;
    }

    @Override
    public void deleteById(UUID id) {
        File channelFile = new File(getChannelFilePath(id));
        if (channelFile.exists()) {
            channelFile.delete();
        }
    }

    @Override
    public Optional<Channel> findByChannelName(String channelName) {
        return findAll().stream()
                .filter(channel -> channel.getChannelName().equals(channelName))
                .findFirst();
    }

    @Override
    public void clear() {
        File dataDir = new File(CHANNEL_DATA_DIR);
        File[] channelFiles = dataDir.listFiles((dir, name) -> name.endsWith(".ser"));
        if (channelFiles != null) {
            for (File file : channelFiles) {
                file.delete();
            }
        }
    }
}
