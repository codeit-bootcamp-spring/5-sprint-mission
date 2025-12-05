package com.sprint.mission.discodeit.domain.channel.exception;

import com.sprint.mission.discodeit.domain.common.exception.ErrorCode;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class ParticipantsNotFoundException extends ChannelException {

    public ParticipantsNotFoundException(Collection<UUID> missingIds) {
        super(
            ErrorCode.USERS_NOT_FOUND,
            Map.of("missingUserIds", missingIds.toString())
        );
    }
}
