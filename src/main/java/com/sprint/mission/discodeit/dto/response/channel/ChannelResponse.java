package com.sprint.mission.discodeit.dto.response.channel;

import com.sprint.mission.discodeit.domain.enums.ChannelType;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ChannelResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    ChannelType type,
    boolean isPrivate,
    Boolean isSecret,
    UUID guildId,
    Set<UUID> memberIds,
    int activeCount
) {

}
