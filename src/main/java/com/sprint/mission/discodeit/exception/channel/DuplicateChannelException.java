package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class DuplicateChannelException extends ChannelException {

    public DuplicateChannelException(UUID firstUserId, UUID secondUserId) {
        super(
            ErrorCode.DUPLICATE_PRIVATE_CHANNEL,
            Map.of("firstUserId", firstUserId, "secondUserId", secondUserId)
        );
    }
}
