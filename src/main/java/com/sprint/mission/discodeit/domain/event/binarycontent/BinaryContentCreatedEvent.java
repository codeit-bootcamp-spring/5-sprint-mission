package com.sprint.mission.discodeit.domain.event.binarycontent;

import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes
) {
    public static final String TOPIC = "discodeit.binary-content.created";
}
