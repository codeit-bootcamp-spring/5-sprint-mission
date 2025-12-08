package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;

import java.util.UUID;

public record S3UploadFailedEvent(
        Exception ex,
        UUID binaryContentId
) {
}
