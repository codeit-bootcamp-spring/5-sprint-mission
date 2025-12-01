package com.sprint.mission.discodeit.event.binarycontent;

import java.util.UUID;

public record BinaryContentUploadFailedEvent(
    UUID binaryContentId,
    String requestId,
    String errorMessage
) {
}
