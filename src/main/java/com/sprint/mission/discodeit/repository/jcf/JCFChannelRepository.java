package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public void delete(Channel channel) {
        if (!data.containsKey(channel.getId())) {
            System.err.println("해당하는 채널을 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        data.remove(channel.getId());
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public List<Channel> searchByName(String name) {
        List<Channel> channels = new ArrayList<>();
        for (Channel channel : data.values()) {
            if (channel.getName().contains(name)) {
                channels.add(channel);
            }
        }
        if(channels.isEmpty()) {
            System.err.println("해당하는 채널을 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return channels;
    }

    @Override
    public Channel searchById(UUID id) {
        if (!data.containsKey(id)) {
            System.err.println("해당하는 채널을 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return data.get(id);
    }

    @Override
    public List<Channel> searchAll() {
        return new ArrayList<>(data.values());
    }
}
