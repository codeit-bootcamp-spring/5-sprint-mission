package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JcfChannelRepository implements ChannelRepository {
    // 사용자 데이터를 저장할 HashMap. UUID를 키로, Channel 객체를 값으로 사용
    private final Map<UUID, Channel> channels = new HashMap<>();


    @Override
    public Channel save(Channel channel) {
        channels.put(channel.getId(), channel);
        System.out.println("[Repo]Channel saved to JCF cache: " + channel.getId());
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        System.out.println("[Repo]Finding Channel by ID in JCF cache: " + id);
        return Optional.ofNullable(channels.get(id));
    }

    @Override
    public Optional<Channel> findByName(String name) {
        System.out.println("[Repo]Finding channel by email in JCF cache: " + name);
        return channels.values().stream()
                .filter(channel -> channel.getName() != null && channel.getName().equals(name))
                .findFirst(); //
    }

    @Override
    public List<Channel> findAll() {
        System.out.println("[Repo]Retrieving all channel from JCF cache. Total: " + channels.size());
        return new ArrayList<>(channels.values());
    }

    @Override
    public void delete(UUID id) {
        if(!channels.containsKey(id)) {
            throw new NoSuchElementException("[Repo]Channel with id " + id + " not found");
        }
        channels.remove(id);
        System.out.println("[Repo]Channel deleted from JCF cache: "+ id);
    }
}
