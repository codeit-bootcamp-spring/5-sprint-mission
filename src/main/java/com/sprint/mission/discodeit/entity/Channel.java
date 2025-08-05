package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    //내가 디스코드에서 필요한 필드를 추가적으로 설계하는 자리
    private String name;
    private ChannelType type;

    public Channel(ChannelType type, String name) {
        id = UUID.randomUUID();
        this.type = type;
        this.name = name;
        createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public ChannelType getType() {
        return type;
    }

    public void update(ChannelType type, String name){
        this.type = type;
        this.name = name;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
