package com.sprint.mission.discodeit.domain.binarycontent.event;

import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes
) {
    public static final String TOPIC = "discodeit.binary-content.created";
}
