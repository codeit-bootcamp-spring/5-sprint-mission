package com.sprint.mission.discodeit.channel.domain.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;

public class PrivateChannelUpdateException extends ChannelException {

    public PrivateChannelUpdateException() {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE);
    }
}
