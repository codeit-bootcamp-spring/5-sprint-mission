package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;
import java.util.UUID;

public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt; // 생성시간
    private Long updatedAt; // 수정시간

    private String channelname;

    public Channel(String channelname) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.channelname = channelname;
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

    public String getChannelname() {
        return channelname;
    }

    public void update(String content){
        this.channelname = content;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Channel.class.getSimpleName() + "[", "]")
                .add("id = " + id)
                .add("channelname = '" + channelname + "'")
                .add("createdAt = " + createdAt)
                .add("updatedAt = " + updatedAt)
                .toString();
    }
}
