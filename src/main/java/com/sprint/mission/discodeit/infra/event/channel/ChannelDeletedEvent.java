package com.sprint.mission.discodeit.infra.event.channel;

import java.util.UUID;

public record ChannelDeletedEvent(UUID channelId) {
}
