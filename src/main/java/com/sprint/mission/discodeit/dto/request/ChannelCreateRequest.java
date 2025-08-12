package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public record ChannelCreateRequest (
        ChannelType type,
        List<UUID> userIds,
        String name,
        String description
){
}
