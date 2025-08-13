package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("jcfChannelRepository")
@Profile("jcf")
public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        this.data.put(channel.getId(), channel);
        return channel;
    }



    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }

    @Override
    public void addUserId(UUID channelId,UUID userId) {
        this.data.get(channelId).addUserId(userId);
    }

    @Override
    public void deleteUserId(UUID channelId,UUID userId) {
        this.data.get(channelId).deleteUserId(userId);
    }

    @Override
    public List<UUID> findAllUserIds(UUID channelId) {
        List<UUID> userIds = new ArrayList<>(this.data.get(channelId).getUserId().keySet());
        if(userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return userIds;
    }
}
