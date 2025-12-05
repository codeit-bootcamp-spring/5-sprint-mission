package com.sprint.mission.discodeit.infra.cache;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.NONE)
public final class CacheType {
    public static final String USER = "user";
    public static final String USERS = "users";
    public static final String USER_DETAILS = "userDetails";
    public static final String BINARY_CONTENTS = "binaryContents";
    public static final String CHANNELS = "channel";
    public static final String MESSAGES = "message";
    public static final String NOTIFICATIONS = "notification";
    public static final String READ_STATUSES = "readStatuses";
}
