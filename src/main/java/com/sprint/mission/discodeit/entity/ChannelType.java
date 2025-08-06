package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public enum ChannelType {
    PRIVATE("private"),
    PUBLIC("public");

    private final String type;

    ChannelType(String type) {
        this.type = type;
    }

}
