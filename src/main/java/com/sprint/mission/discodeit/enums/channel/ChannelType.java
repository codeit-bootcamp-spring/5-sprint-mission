package com.sprint.mission.discodeit.enums.channel;

public enum ChannelType {
    CHAT("채팅 채널"),
    VOICE("음성 채널");

    private final String description;

    ChannelType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
