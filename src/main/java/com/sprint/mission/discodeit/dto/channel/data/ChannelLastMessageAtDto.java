package com.sprint.mission.discodeit.dto.channel.data;

import java.time.Instant;
import java.util.UUID;

public record ChannelLastMessageAtDto(
    UUID channelId,
    Instant lastMessageAt
) {
}
