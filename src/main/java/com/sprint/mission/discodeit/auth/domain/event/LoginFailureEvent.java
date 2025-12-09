package com.sprint.mission.discodeit.auth.domain.event;

public record LoginFailureEvent(
    long duration
) {
    public static final String TOPIC = "discodeit.auth.login.failure";
}
