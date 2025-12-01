package com.sprint.mission.discodeit.event.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topic {

    MESSAGE_CREATED("discodeit.MessageCreatedEvent"),
    ROLE_UPDATED("discodeit.RoleUpdatedEvent"),
    UPLOAD_FAILED("discodeit.S3UploadFailedEvent");

    private final String value;
}
