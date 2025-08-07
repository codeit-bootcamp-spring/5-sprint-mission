package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddPrivateChannelDto;
import com.sprint.mission.discodeit.dto.request.AddPublicChannelDto;
import com.sprint.mission.discodeit.dto.request.UpdateChannelDto;
import com.sprint.mission.discodeit.dto.response.GetChannelByIdDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel addPublicChannel(AddPublicChannelDto addPublicChannelDto);
    Channel addPrivateChannel(AddPrivateChannelDto addPrivateChannelDto);
    GetChannelByIdDto getChannelById(UUID channelId);
    void deleteChannel(UUID channelId);
    void deleteAllChannel();
    List<GetChannelByIdDto> getAllChannelByUserId(UUID userId);
    Channel updateChannel(UUID channelId, UpdateChannelDto updateChannelDto);

}

