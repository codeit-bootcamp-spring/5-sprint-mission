package com.sprint.mission.discodeit.entity;

public class Channel extends Base {
    private String name;
    private String topic;

    public Channel(String name, String topic) {
        // null 체크
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 null이 될 수 없습니다.");
        }
        this.name = name;
        this.topic = topic;
    }

    public String getName() {return name;}

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 null이 될 수 없습니다.");
        }
        this.name = name;
    }

    public String getTopic() {return topic;}
    public void updateTopic(String topic) {
        this.topic = topic;
        updateTimestamp();
    }

    @Override
    public String toString() {
        return String.format(
                "\nid: %-36s  채널명: %-10s  주제: %s",
                getId(), name, topic
        );
    }
}
