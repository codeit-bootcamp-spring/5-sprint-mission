package com.sprint.mission.discodeit.domain.channel.exception;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class ChannelNotFoundException extends ChannelException {
    public ChannelNotFoundException(Long channelId) {
        super(ErrorCode.CHANNEL_NOT_FOUND, Map.of("channelId", channelId));
    }
}
