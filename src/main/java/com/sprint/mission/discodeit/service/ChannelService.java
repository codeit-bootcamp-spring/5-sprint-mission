package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddPrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.AddPublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.response.GetChannelByIdResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel addPublicChannel(AddPublicChannelRequest addPublicChannelRequest);
    Channel addPrivateChannel(AddPrivateChannelRequest addPrivateChannelRequest);
    GetChannelByIdResponse getChannelById(UUID channelId);
    void deleteChannel(UUID channelId);
    void deleteAllChannel();
    List<GetChannelByIdResponse> getAllChannelByUserId(UUID userId);
    Channel updateChannel(UpdateChannelRequest updateChannelRequest, UUID channelId);

}

