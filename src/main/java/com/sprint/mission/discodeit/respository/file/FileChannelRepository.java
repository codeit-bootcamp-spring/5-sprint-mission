package com.sprint.mission.discodeit.respository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.respository.ChannelRepository;

import java.util.*;


public class FileChannelRepository extends FileStore<Channel> implements ChannelRepository {

    private final Map<UUID, Channel> channelMap = new HashMap<>();

    public FileChannelRepository(String rootDir) {
        super(rootDir + "channel.ser");
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
    public List<Channel> findAll() {
        return List.copyOf(channelMap.values());
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelMap.get(id));
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
    public Optional<Channel> updateName(UUID id, String name) {
        Channel channel = channelMap.get(id);
        if (channel != null) {
            channel.updateName(name);
            saveToFile(channelMap);
            return Optional.of(channel);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Channel> updateTopic(UUID id, String topic) {
        Channel channel = channelMap.get(id);
        if (channel != null) {
            channel.updateTopic(topic);
            saveToFile(channelMap);
            return Optional.of(channel);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(UUID id) {
        if (channelMap.containsKey(id)) {
            channelMap.remove(id);
            saveToFile(channelMap);
            return true;
        } else {
            return false;
        }
    }
}
