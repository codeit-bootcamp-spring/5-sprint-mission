package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private long createAt;
    private long updateAt;

    public Message(UUID id) {
        this.id = id;
    }

    public Message(long createAt) {
        this.createAt = createAt;
    }

    public Message(UUID id, long createAt, long updateAt) {
        this.id = id;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("id=").append(id);
        sb.append(", createAt=").append(createAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append('}');
        return sb.toString();
    }
}
