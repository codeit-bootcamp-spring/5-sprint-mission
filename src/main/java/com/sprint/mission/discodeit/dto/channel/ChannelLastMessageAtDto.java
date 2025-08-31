package com.sprint.mission.discodeit.dto.channel;

import java.time.Instant;
import java.util.UUID;

public record ChannelLastMessageAtDto(
    UUID channelId,
    Instant lastMessageAt
) {

}
