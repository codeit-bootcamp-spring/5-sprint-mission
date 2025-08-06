package com.sprint.mission.discodeit.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record ChannelFindResponse(
        Instant lastestMessageTime,
        String name,
        String description,
        List<UUID> userIds
) {

}
