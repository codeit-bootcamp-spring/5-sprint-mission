package com.sprint.mission.discodeit.dto.channel;

import java.util.List;
import java.util.UUID;

public record CreateChannelRequest(
        String name,
        String description,
        List<UUID> userIdList
) {}
