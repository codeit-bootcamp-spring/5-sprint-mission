package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record ChannelFindResponse(
    Instant lastestMessageTime,
    Instant createdAt,
    Instant updatedAt,
    ChannelType type,
    String name,
    String description,
    List<UUID> userIds
) {

}
