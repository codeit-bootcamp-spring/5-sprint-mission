package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;
import java.util.UUID;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;


    private String username;

    public User(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
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

    public String getUsername() {
        return username;
    }

    public void update(String username){
      this.username = username;
      this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id = " + id)
                .add("username = '" + username + "'")
                .add("createdAt = " + createdAt)
                .add("updatedAt = " + updatedAt)
                .toString();
    }
}
