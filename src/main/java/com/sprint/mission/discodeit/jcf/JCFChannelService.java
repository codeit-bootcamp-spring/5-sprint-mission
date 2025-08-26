package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        data = new HashMap<>();
    }

    @Override
    public Channel register(Channel channel) {
        if (isInvalid(channel.getName()) || isInvalid(channel.getDescription()))
            throw new IllegalArgumentException("채널 등록에 실패했습니다.");

        data.put(channel.getId(), channel);
        System.out.println("채널 : " + channel.getName() + " 등록 성공.");
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        if(!data.containsKey(id))
            throw new NoSuchElementException("채널에서 해당 " + id + "를 찾을 수 없습니다.");
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String newDescription) {
        if (isInvalid(newDescription))
            throw new IllegalArgumentException("새로운 채널 설명을 입력하세요.");

        Channel channel = findById(id);

        channel.setDescription(newDescription);
        channel.setUpdatedAt(System.currentTimeMillis());
        return channel;
    }

    @Override
    public Channel delete(UUID id) {
        if (!data.containsKey(id))
            throw new NoSuchElementException("채널에서 해당 " + id + "를 찾을 수 없습니다.");
        else
            return data.remove(id);
    }

    public boolean isInvalid(String value) {
        return value == null || value.isBlank();
    }
}
