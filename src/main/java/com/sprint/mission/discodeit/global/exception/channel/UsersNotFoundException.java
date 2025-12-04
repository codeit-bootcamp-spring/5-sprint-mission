package com.sprint.mission.discodeit.global.exception.channel;

import com.sprint.mission.discodeit.global.exception.ErrorCode;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class UsersNotFoundException extends ChannelException {

    public UsersNotFoundException(Collection<UUID> missingIds) {
        super(
            ErrorCode.USERS_NOT_FOUND,
            Map.of("missingUserIds", missingIds.toString())
        );
    }
}
