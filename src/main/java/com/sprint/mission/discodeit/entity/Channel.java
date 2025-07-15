package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Channel {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String name;
    private final List<UUID> users;

    public Channel() {
        id = UUID.randomUUID();
        createdAt = Instant.now().toEpochMilli();
        updatedAt = createdAt;
        users = new ArrayList<>();
    }

    public Channel(String name) {
        id = UUID.randomUUID();
        createdAt = Instant.now().toEpochMilli();
        updatedAt = createdAt;
        users = new ArrayList<>();
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

    public List<UUID> getUsers() {
        return users;
    }

    public Channel updateName(String name) {
        this.name = name;
        updatedAt = Instant.now().toEpochMilli();
        return this;
    }

    public void addUser(User user) {
        if (!users.contains(user.getId())) {
            this.users.add(user.getId());
            user.getChannels().add(this.id);
            updatedAt = Instant.now().toEpochMilli();
        }
    }

    public void deleteUser(User user) {
        this.users.remove(user.getId());
        user.getChannels().remove(this.id);
        updatedAt = Instant.now().toEpochMilli();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return createdAt == channel.createdAt && updatedAt == channel.updatedAt && Objects.equals(id, channel.id) && Objects.equals(name, channel.name) && Objects.equals(users, channel.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, name, users);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + Instant.ofEpochMilli(createdAt) +
                ", updatedAt=" + Instant.ofEpochMilli(updatedAt) +
                ", name='" + name + '\'' +
                ", users=" + users +
                '}';
    }
}
