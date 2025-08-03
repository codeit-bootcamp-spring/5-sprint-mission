package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> userMap;

    public JCFChannelRepository() {
        userMap = new HashMap<>();
    }

    @Override
    public Channel save(Channel user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<Channel> findById(UUID userId) {
        if (userMap.containsKey(userId)) {
            return Optional.of(userMap.get(userId));
        }
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public boolean delete(UUID userId) {
        return userMap.remove(userId, userMap.get(userId));
    }
}
