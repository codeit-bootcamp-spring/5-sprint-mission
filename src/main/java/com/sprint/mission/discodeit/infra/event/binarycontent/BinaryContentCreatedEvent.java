package com.sprint.mission.discodeit.infra.event.binarycontent;

import java.util.UUID;

public record BinaryContentCreatedEvent(UUID binaryContentId, byte[] bytes) {
}
