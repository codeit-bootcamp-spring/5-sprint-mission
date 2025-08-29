package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public record ChannelParticipantId(
    UUID channelId,
    UUID userId
) implements Serializable {

}