package com.sprint.mission.discodeit.event.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topic {

    USER_DELETED("discodeit.UserDeletedEvent"),
    CHANNEL_DELETED("discodeit.ChannelDeletedEvent"),
    MESSAGE_CREATED("discodeit.MessageCreatedEvent"),
    MESSAGE_DELETED("discodeit.MessageDeletedEvent"),
    ROLE_UPDATED("discodeit.RoleUpdatedEvent"),
    BINARY_CONTENT_CREATED("discodeit.BinaryContentCreatedEvent"),
    UPLOAD_FAILED("discodeit.BinaryContentUploadFailedEvent"),
    AUTH_AUDIT("discodeit.AuthAuditEvent");

    private final String value;
}
