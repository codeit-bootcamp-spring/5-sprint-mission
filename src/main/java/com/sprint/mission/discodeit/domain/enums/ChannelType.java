package com.sprint.mission.discodeit.domain.enums;

import lombok.Getter;

@Getter
public enum ChannelType {
    CHAT("채팅 채널"),
    VOICE("음성 채널");

    private final String displayName;

    ChannelType(String displayName) {
        this.displayName = displayName;
    }
}
