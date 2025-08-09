package com.sprint.mission.discodeit.domain.enums.channel;

import lombok.Getter;

@Getter
public enum ChannelType {
    CHAT("채팅 채널"),
    VOICE("음성 채널");

    private final String description;

    ChannelType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
