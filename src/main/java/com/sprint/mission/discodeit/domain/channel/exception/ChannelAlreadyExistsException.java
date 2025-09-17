package com.sprint.mission.discodeit.domain.channel.exception;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class ChannelAlreadyExistsException extends ChannelException {
    public ChannelAlreadyExistsException(String channelName) {
        super(ErrorCode.CHANNEL_ALREADY_EXISTS, Map.of("channelName", channelName));
    }
}
