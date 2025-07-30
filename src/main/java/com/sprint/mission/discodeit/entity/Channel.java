package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public class Channel extends Base implements Serializable {
    private String name;
    private String topic;
    private ChannelType type;
    private String description;

    public Channel(String name, ChannelType type) {
        // null 체크
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 null이 될 수 없습니다.");
        }
        this.name = name;
        this.topic = topic;
        this.type = type;
    }

    public String getName() {return name;}

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 null이 될 수 없습니다.");
        }
        this.name = name;
        updateTimestamp();
    }

    public String getTopic() {
        return topic;
    }

    public void updateTopic(String topic) {
        this.topic = topic;
        updateTimestamp();
    }

    public ChannelType getType() {
        return type;
    }

    public void updateType(ChannelType newType) {
        this.type = newType;
    }

    public String getDescription() {
        return description;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format(
                "\nid: %-36s  채널명: %-10s  주제: %s",
                getId(), name, topic
        );
    }
}
