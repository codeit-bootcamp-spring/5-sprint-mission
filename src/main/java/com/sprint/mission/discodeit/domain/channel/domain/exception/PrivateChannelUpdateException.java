package com.sprint.mission.discodeit.domain.channel.domain.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;

public class PrivateChannelUpdateException extends ChannelException {

    public PrivateChannelUpdateException() {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE);
    }
}
