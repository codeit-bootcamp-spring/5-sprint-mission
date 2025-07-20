package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    public void createChannel(String channelname,String description);
    public Channel readByIdChannel(UUID name);
    public void readAllChannel();
    public void updateChannel(UUID channelUUID,String channelname,String description);
    public void deleteByIdChannel(UUID channelUUID);
}
