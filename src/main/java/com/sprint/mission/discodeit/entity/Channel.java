package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class Channel extends Base {

    private String name;
    private String topic;
    private ChannelType type;
    private String description;

    public Channel(String name, ChannelType type) {
        this.name = name;
        this.type = type;
    }

    public Channel(String name, ChannelType type, String topic, String description) {
        this(name, type);
        this.topic = topic;
        this.description = description;
    }

    public void updateName(String name) {
        this.name = name;
        updateTimestamp();
    }

    public void updateTopic(String topic) {
        this.topic = topic;
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
                "아이디: %-10s  채널명: %-10s  타입: %-10s  주제: %-10s 설명: %-10s 생성시간: %-10s, 업데이트: %-10s \n",
                getId(), getName(), getType(), getTopic(), getDescription(), getCreatedAtFormatted(), getUpdatedAtFormatted()
        );
    }
}
