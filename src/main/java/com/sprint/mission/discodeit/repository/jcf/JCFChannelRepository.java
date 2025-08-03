package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    final Map<UUID, Channel> data;
    public JCFChannelRepository() {
        data = new HashMap<>();
    }
    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public long count() {
        return data.size();
    }

    @Override
    public boolean delete(UUID id) {
        return  data.remove(id) != null;
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public boolean update(UUID channelUUID, String channelname, String description) {
        Channel channel = data.get(channelUUID);
        if(channel.getChannelName().equals(channelname) && channel.getDescription().equals(description)){
            System.out.println("수정 전과 일치합니다.");
            return false;
        }

        channel.update(channelname, description);
        return true;
    }
}
