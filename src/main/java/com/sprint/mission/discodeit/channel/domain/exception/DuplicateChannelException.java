package com.sprint.mission.discodeit.channel.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class DuplicateChannelException extends ChannelException {

    public DuplicateChannelException(UUID userId1, UUID userId2) {
        super(
            ErrorCode.DUPLICATE_PRIVATE_CHANNEL,
            Map.of("userId1", userId1, "userId2", userId2)
        );
    }
}
