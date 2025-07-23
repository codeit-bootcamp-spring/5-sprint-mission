package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelDTO;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(User user, String channelName, boolean nsfw);
    Channel findById(UUID channelId);
    List<Channel> findAll();
    Channel update(ChannelDTO channelDTO);
    ChannelDTO createChannelDTO(UUID channelId, String channelName, User owner, boolean nsfw);
    Channel deleteById(UUID channelId);
}
