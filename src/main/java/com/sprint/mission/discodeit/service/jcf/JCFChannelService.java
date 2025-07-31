package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelMap;

    public JCFChannelService() {
        channelMap = new HashMap<>();
    }

    // 채널 추가
    @Override
    public Channel create(String name, UUID ownerId) {
        if (name == null || name.isBlank() || ownerId == null) {
            throw new IllegalArgumentException("Channel info is invalid");
        }
        Channel channel = new Channel(name, ownerId);
        channelMap.put(channel.getId(), channel);
        return channel;
    }

    // 채널 조회
    @Override
    public Channel find(UUID channelId) {
        Channel channel = channelMap.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("Channel not found");
        }
        return channel;
    }

    // 채널 전체 조회
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    // 채널 수정
    @Override
    public Channel update(UUID channelId, String name, UUID ownerId) {
        Channel channel = channelMap.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("Channel not found");
        }
        channel.update(name, ownerId);
        return channel;
    }

    // 채널 삭제
    @Override
    public void delete(UUID channelId) {
        channelMap.remove(channelId);
    }
}
