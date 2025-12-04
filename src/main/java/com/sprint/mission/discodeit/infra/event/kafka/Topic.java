package com.sprint.mission.discodeit.infra.event.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topic {

    ROLE_UPDATED("discodeit.RoleUpdatedEvent"),
    TOKEN_REFRESHED("discodeit.TokenRefreshedEvent"),

    USER_DELETED("discodeit.UserDeletedEvent"),

    CHANNEL_DELETED("discodeit.ChannelDeletedEvent"),

    MESSAGE_CREATED("discodeit.MessageCreatedEvent"),
    MESSAGE_DELETED("discodeit.MessageDeletedEvent"),

    BINARY_CONTENT_CREATED("discodeit.BinaryContentCreatedEvent"),

    AUTH_AUDIT("discodeit.AuthAuditEvent");

    private final String value;
}
