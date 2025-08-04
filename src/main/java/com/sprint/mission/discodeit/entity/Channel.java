package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;

@Getter
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
        if (type == null) {
            throw new IllegalArgumentException("채널 타입은 null이 될 수 없습니다.");
        }
        this.name = name;
        this.type = type;
    }

    public Channel(String name, ChannelType type, String topic, String description) {
        this(name, type); // 첫 번째 생성자 호출 → 여기서 name/type 유효성 검사 수행됨
        this.topic = topic;
        this.description = description;
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 null이 될 수 없습니다.");
        }
        this.name = name;
        updateTimestamp();
    }

    public void updateTopic(String topic) {
        this.topic = topic;
        updateTimestamp();
    }

    public void updateType(ChannelType newType) {
        this.type = newType;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format(
                "아이디: %-10s  채널명: %-10s  타입: %-10s  주제: %-10s 설명: %-10s 생성시간: %-10s, 업데이트: %-10s \n",
                getId(), getName(), getType(), getTopic(), getDescription(), getCreatedAtFormatted(), getUpdatedAtFormatted()
        );
    }
}
