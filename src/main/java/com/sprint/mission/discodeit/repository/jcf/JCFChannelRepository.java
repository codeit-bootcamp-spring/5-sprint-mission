package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    Map<UUID, Channel> data = new HashMap<>();

    public JCFChannelRepository() {}

    @Override
    public Optional<Channel> save(Channel channel) {
        if(channel == null){
            throw new IllegalArgumentException("channel 파라미터가 null 입니다.");
        }

        data.put(channel.getId(), channel);
        return Optional.of(channel);
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        if(data.containsKey(channelId)){
            return Optional.of(data.get(channelId));
        }
        throw new IllegalArgumentException("존재하지 않는 채널입니다.");
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(Channel channel) {
        UUID id = channel.getId();
        data.remove(id);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}


