package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
<<<<<<< HEAD
import com.sprint.mission.discodeit.entity.ChannelType;
=======
>>>>>>> 717adae (feat: 초기 커밋)
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
<<<<<<< HEAD
        data.put(channel.getId(), channel);
=======
        this.data.put(channel.getId(), channel);
>>>>>>> 717adae (feat: 초기 커밋)
        return channel;
    }

    @Override
<<<<<<< HEAD
    public Optional<Channel> find(UUID channelId) {
        return Optional.ofNullable(data.get(channelId));
=======
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
>>>>>>> 717adae (feat: 초기 커밋)
    }

    @Override
    public List<Channel> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
<<<<<<< HEAD
    public boolean existById(UUID channelId) {
        return data.containsKey(channelId);
    }

    @Override
    public void delete(UUID channelId) {
        if (!this.data.containsKey(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        this.data.remove(channelId);
=======
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
>>>>>>> 717adae (feat: 초기 커밋)
    }
}
