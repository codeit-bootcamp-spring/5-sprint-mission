package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record AddPublicChannelDto(
        String channelName, String channelDescription, UUID ownerUserId
) {
}
