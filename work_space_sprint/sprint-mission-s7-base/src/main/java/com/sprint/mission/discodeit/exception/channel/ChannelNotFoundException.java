package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ChannelNotFoundException extends ChannelException {
    
    public ChannelNotFoundException(UUID channelId) {
        super(ErrorCode.CHANNEL_NOT_FOUND, 
              "Channel with id " + channelId + " not found", 
              Map.of("channelId", channelId));
    }
}