package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
//    Channel createPublic(ChannelType type, String name, String description);
    Channel createPublic(ChannelCreateRequest request);
    Channel createPrivate(ChannelType type,User user);
    ChannelFindResponse find(UUID channelId);
    List<ChannelFindResponse> findAllByUserId(User user);
    Channel update(ChannelUpdateRequest request);
    void delete(UUID channelId);
}
