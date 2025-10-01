package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateChannelNameException extends ChannelException {
    public DuplicateChannelNameException() {
        super(ErrorCode.DUPLICATE_CHANNEL_NAME);
    }

    public static DuplicateChannelNameException withChannelName(String channelName) {
        DuplicateChannelNameException exception = new DuplicateChannelNameException();
        exception.addDetail("channelName", channelName);
        return exception;
    }
}
