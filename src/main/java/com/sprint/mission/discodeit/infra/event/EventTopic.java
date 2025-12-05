package com.sprint.mission.discodeit.infra.event;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class EventTopic {

    public static final String LOGIN_SUCCESS = "discodeit.LoginSuccessEvent";
    public static final String LOGIN_FAILURE = "discodeit.LoginFailureEvent";
    public static final String LOGOUT = "discodeit.LogoutEvent";

    public static final String ROLE_UPDATED = "discodeit.RoleUpdatedEvent";
    public static final String TOKEN_REFRESHED = "discodeit.TokenRefreshedEvent";

    public static final String USER_DELETED = "discodeit.UserDeletedEvent";

    public static final String CHANNEL_DELETED = "discodeit.ChannelDeletedEvent";

    public static final String MESSAGE_CREATED = "discodeit.MessageCreatedEvent";
    public static final String MESSAGE_DELETED = "discodeit.MessageDeletedEvent";

    public static final String BINARY_CONTENT_CREATED = "discodeit.BinaryContentCreatedEvent";
}
