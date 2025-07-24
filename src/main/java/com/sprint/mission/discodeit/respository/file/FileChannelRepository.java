package com.sprint.mission.discodeit.respository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.respository.ChannelRepository;
import java.util.*;

public class FileChannelRepository extends FileStore<Channel> implements ChannelRepository {

    private final Map<UUID, Channel> channelMap = new HashMap<>();

    public FileChannelRepository() {
        super("data/channel.store");
        Map<UUID, Channel> loaded = loadFromFile();
        channelMap.putAll(loaded);
    }

    @Override
    public Channel save(Channel channel) {
        channelMap.put(channel.getId(), channel);
        saveToFile(channelMap);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return channelMap.get(id);
    }

    @Override
    public List<Channel> findByName(String name) {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : channelMap.values()) {
            if (channel.getName().equals(name)) {
                result.add(channel);
            }
        }
        return result;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = channelMap.get(id);
        if (channel != null) {
            channel.updateName(name);
            saveToFile(channelMap);
        }
        return channel;
    }

    @Override
    public Channel updateTopic(UUID id, String topic) {
        Channel channel = channelMap.get(id);
        if (channel != null) {
            channel.updateTopic(topic);
            saveToFile(channelMap);
        }
        return channel;
    }

    @Override
    public void deleteById(UUID id) {
        channelMap.remove(id);
        saveToFile(channelMap);
    }
}
