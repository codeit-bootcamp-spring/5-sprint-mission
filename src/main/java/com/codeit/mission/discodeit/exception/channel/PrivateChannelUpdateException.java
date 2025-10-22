package com.codeit.mission.discodeit.exception.channel;

import com.codeit.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class PrivateChannelUpdateException extends ChannelException {

    public PrivateChannelUpdateException(UUID channelId) {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE,
                "Private channel cannot be updated",
                Map.of("channelId", channelId));
    }
}
