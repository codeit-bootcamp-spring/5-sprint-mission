package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private static JCFChannelService instance;

    private JCFChannelService() {}

    public static JCFChannelService getInstance() {
        if (instance == null) {
            instance = new JCFChannelService();
        }
        return instance;
    }

    final Map<UUID, Channel> cdata = new HashMap<>();

    @Override
    public Channel create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름이 비어있습니다.");
        }
        Channel channel = new Channel(name);
        cdata.put(channel.getChId(), channel);
        return channel;
    }

    @Override
    public Channel delete(UUID id) {
        if (!cdata.containsKey(id)) {
            throw new NoSuchElementException("비어있거나 일치하는 결과가 없습니다.");
        }
        Channel deletedCdata = cdata.remove(id);
        return deletedCdata;
    }

    @Override
    public Channel update(UUID id, String name) {
        if (name == null || name.isBlank() || !cdata.containsKey(id)) {
            throw new NoSuchElementException("비어있거나 일치하는 결과가 없습니다.");
        }
        Channel channel = cdata.get(id);
        channel.update(name);
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        if (cdata.isEmpty()) {
            throw new NoSuchElementException("채널이 존재하지 않습니다.");
        }
        return new ArrayList<>(cdata.values());
    }

    @Override
    public Channel find(UUID id) {
        if (id == null || !cdata.containsKey(id)) {
            throw new NoSuchElementException("채널을 찾을 수 없습니다.");
        }
        return cdata.get(id);
    }
}
