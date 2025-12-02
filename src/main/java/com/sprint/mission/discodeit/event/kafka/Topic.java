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
    UPLOAD_FAILED("discodeit.S3UploadFailedEvent");

    private final String value;
}
