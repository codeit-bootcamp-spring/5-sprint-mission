package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class PrivateChannelUpdateException extends ChannelException {

    public PrivateChannelUpdateException() {
        super(ErrorCode.PRIVATTE_CHANNEL_UPDATE);
    }

    public PrivateChannelUpdateException privateChannelUpdate(String message) {
        PrivateChannelUpdateException exception = new PrivateChannelUpdateException();
        return exception;

    }
}
