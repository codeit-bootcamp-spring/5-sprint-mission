package com.sprint.mission.discodeit.entity;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class Channel {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String name;

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final StringBuilder sb = new StringBuilder("Channel{");
        sb.append("createdAt=").append(sdf.format(createdAt));
        sb.append(", updatedAt=").append(sdf.format(updatedAt));
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
