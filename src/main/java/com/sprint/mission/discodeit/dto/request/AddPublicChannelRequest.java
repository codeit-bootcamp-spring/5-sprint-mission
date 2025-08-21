package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record AddPublicChannelRequest(
    String channelName, String channelDescription, UUID ownerUserId
) {

}

