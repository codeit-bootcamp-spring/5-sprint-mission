package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel original = data.get(id);
        if (original == null) return null;
        return new Channel(original); // 복사본 반환
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> result = new ArrayList<>();
        for (Channel c : data.values()) {
            result.add(new Channel(c)); // 복사본 생성
        }
        return result;
    }

    @Override
    public boolean update(UUID id, Channel updatedChannel) {
        Channel current = data.get(id);
        if (current == null) return false;

        // name, description, channelType, isPrivate는 업데이트하지만 id, createdAt은 유지
        Channel newChannel = new Channel(
                current.getId(),
                current.getCreatedAt(),
                System.currentTimeMillis(), // updatedAt 갱신
                updatedChannel.getName(),
                updatedChannel.getDescription(),
                updatedChannel.getChannelType(),
                updatedChannel.isPrivate()
        );

        data.put(id, newChannel);
        return true;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}


