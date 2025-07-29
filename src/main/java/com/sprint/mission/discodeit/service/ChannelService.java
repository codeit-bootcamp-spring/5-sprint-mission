package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel createChannel(Channel.ChannelType type, String  title , String description);

//    UUID addUser(UUID channelId,UUID userId);
//
//    UUID addMessage(UUID channelId,UUID messageId);

    Channel getChannelTitleById(UUID channelId);

    UUID getChannelIdByTitle(String title);

    List<Channel> getAllChannels();
//
//    List<UUID> findAllUsersId(UUID channelId);
//
//    List<UUID> findAllMessagesId(UUID channelId);

    Channel updateChannelTitle(UUID channelId, String title);

    Channel updateChannelDescription(UUID channelId, String description);

    Channel updateChannelType(UUID channelId, Channel.ChannelType type);

    Channel deleteChannel(UUID channelId);
}
