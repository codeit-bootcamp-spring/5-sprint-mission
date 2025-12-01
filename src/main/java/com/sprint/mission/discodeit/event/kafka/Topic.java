package com.sprint.mission.discodeit.event.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topic {

    MESSAGE_CREATED("discodeit.message-created"),
    ROLE_UPDATED("discodeit.role-updated"),
    UPLOAD_FAILED("discodeit.upload-failed");

    private final String value;
}
