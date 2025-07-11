package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class User {


    private UUID id;
    private long createdAt;
    private long updatedAt;

    public UUID getId() {
        return id;
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void updateCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }



    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().toEpochMilli();
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }





}
