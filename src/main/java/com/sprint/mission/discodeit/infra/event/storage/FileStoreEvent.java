package com.sprint.mission.discodeit.infra.event.storage;

import java.util.UUID;

public record FileStoreEvent(
    UUID binaryContentId,
    byte[] bytes
) {
}
