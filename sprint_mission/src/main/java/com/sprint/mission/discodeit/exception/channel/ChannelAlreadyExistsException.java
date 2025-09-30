package com.sprint.mission.discodeit.exception.channel;


import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

// 도메인 에러
public class ChannelAlreadyExistsException extends ChannelException {
    public ChannelAlreadyExistsException() {
        super(ErrorCode.CHANNEL_ALREADY_EXISTS);
    }

    public static ChannelAlreadyExistsException withId(UUID channelId) {
        ChannelAlreadyExistsException exception = new ChannelAlreadyExistsException();
        exception.addDetail("channelId ", channelId);
        return exception;
    }
} 