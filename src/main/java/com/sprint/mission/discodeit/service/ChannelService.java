package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void create(Channel channel) throws IOException;
    Channel get(UUID id) throws IOException, ClassNotFoundException;
    Channel get(String name) throws IOException, ClassNotFoundException;
    List<Channel> getAll() throws IOException, ClassNotFoundException;
    void update(Channel channel) throws IOException;
    void delete(UUID id) throws IOException;
}
