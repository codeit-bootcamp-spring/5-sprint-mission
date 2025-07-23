package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    @Override
    public void create(Channel channel) {

    }

    @Override
    public void update(Channel channel) {

    }

    @Override
    public void delete(Channel channel) {

    }

    @Override
    public Channel searchByIndex(int i) {
        return null;
    }

    @Override
    public List<Channel> searchByName(String name) {
        return List.of();
    }

    @Override
    public Channel searchById(UUID id) {
        return null;
    }

    @Override
    public List<Channel> searchAll() {
        return List.of();
    }
}
