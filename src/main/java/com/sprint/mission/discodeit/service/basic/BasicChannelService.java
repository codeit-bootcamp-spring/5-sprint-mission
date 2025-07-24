package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private Map<UUID, Channel> data;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
        this.data = channelRepository.loadData();
    }


    @Override
    public void create(Channel channel) {
        data.put(channel.getId(), channel);
        channelRepository.save(data);
    }

    @Override
    public Channel find(UUID id) {
        return data.get(id);
    }

    @Override
    public ArrayList<Channel> allFind() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, Channel channel) {
        if (data.containsKey(id)) {
            data.put(id, channel);
            channelRepository.save(data);
        }
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) != null) {
            channelRepository.save(data);
        }
    }
}
