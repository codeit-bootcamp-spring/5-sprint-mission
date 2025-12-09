package com.sprint.mission.discodeit.binarycontent.domain.event;

import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes
) {
    public static final String TOPIC = "discodeit.binary-content.created";
}
