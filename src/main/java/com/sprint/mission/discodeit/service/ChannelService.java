package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.enums.channel.ChannelType;
import java.util.UUID;

public interface ChannelService extends BaseService<Channel> {
  void updateName(UUID channelId, String name);

  void updateType(UUID channelId, ChannelType type);

  void updatePublic(UUID channelId, boolean isPublic);

  void addJoinedUser(UUID channelId, UUID userId);

  void removeJoinedUser(UUID channelId, UUID userId);
}
