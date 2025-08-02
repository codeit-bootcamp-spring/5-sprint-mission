package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private static final String CHANNEL_DATA_DIR = "channel_data";

    public FileChannelService() {
        File dataDir = new File(CHANNEL_DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private String getChannelFilePath(UUID channelId) {
        return CHANNEL_DATA_DIR + File.separator + channelId.toString() + ".ser";
    }

    @Override
    public Channel create(String channelName, String description) {
        Channel channel = new Channel(channelName, description);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getChannelFilePath(channel.getId())))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }

    @Override
    public Channel find(UUID channelId) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getChannelFilePath(channelId)))) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
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
    public Channel update(UUID channelId, String channelName, String description) {
        Channel channel = find(channelId);
        if (channel != null) {
            channel.setChannelName(channelName);
            channel.setDescription(description);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getChannelFilePath(channel.getId())))) {
                oos.writeObject(channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        File channelFile = new File(getChannelFilePath(channelId));
        if (channelFile.exists()) {
            channelFile.delete();
        }
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
