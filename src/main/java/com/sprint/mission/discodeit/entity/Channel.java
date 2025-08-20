package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class Channel extends Base {

    private String name;
    private ChannelType type;
    private String description;

    public Channel(String name, ChannelType type) {
        this.type = type;
        this.name = name;
    }

    public Channel(String name, ChannelType type, String description) {
        this(name, type);
        this.description = description;
    }

    public void updateName(String name) {
        this.name = name;
        updateTimestamp();
    }


    public void updateType(ChannelType newType) {
        this.type = newType;
        updateTimestamp();
    }

    public void updateDescription(String description) {
        this.description = description;
        updateTimestamp();
    }

    @Override
    public String toString() {
        return String.format(
                "아이디: %-10s  채널명: %-10s  타입: %-10s  설명: %-10s 생성시간: %-10s, 업데이트: %-10s \n",
                getId(), getName(), getType(), getDescription(), getCreatedAtFormatted(), getUpdatedAtFormatted()
        );
    }
}
