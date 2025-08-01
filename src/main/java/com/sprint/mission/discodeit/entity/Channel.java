package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.StringJoiner;
import java.util.UUID;

public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt; // 생성시간
    private Long updatedAt; // 수정시간

    private ChannelType type;
    private String channelname;
    private String description;

    public Channel(String channelname) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().getEpochSecond();
        this.type = type;
        this.updatedAt = createdAt;
        this.channelname = channelname;
        this.description = description;
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

    public ChannelType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void update(String name, String description) {
        boolean flag = false;
        if(name != null && name.equals(this.channelname)){
            this.channelname = name;
            flag=true;
        }
        if(description != null && !description.equals(this.description)){
            this.description = description;
            flag=true;
        }
        if(flag){
            this.updatedAt = Instant.now().toEpochMilli();
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", type=" + type +
                ", channelname='" + channelname + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
