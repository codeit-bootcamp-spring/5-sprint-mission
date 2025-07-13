package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String name;
    private final List<Channel> channels;

    public User() {
        id = UUID.randomUUID();
        createdAt = Instant.now().getEpochSecond();
        updatedAt = createdAt;
        channels = new ArrayList<>();
    }

    public User(String name) {
        id = UUID.randomUUID();
        createdAt = Instant.now().getEpochSecond();
        updatedAt = createdAt;
        channels = new ArrayList<>();
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

    public String getName() {
        return name;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public User updateName(String name) {
        this.name = name;
        updatedAt = Instant.now().getEpochSecond();
        return this;
    }

    public User addChannel(Channel channel) { // 나중에 User 삭제할때 deleteUser 쓸 것.
        this.channels.add(channel);
        updatedAt = Instant.now().getEpochSecond();
        return this;
    }

    public User deleteChannel(Channel channel) {
        this.channels.remove(channel);
        updatedAt = Instant.now().getEpochSecond();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return createdAt == user.createdAt && updatedAt == user.updatedAt && Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(channels, user.channels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, name, channels);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + Instant.ofEpochSecond(createdAt) +
                ", updatedAt=" + Instant.ofEpochSecond(updatedAt) +
                ", name='" + name + '\'' +
                ", channels=" + channels +
                '}';
    }
}
