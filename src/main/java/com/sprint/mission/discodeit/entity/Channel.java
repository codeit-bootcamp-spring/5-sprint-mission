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
    private final List<User> users;

    public Channel() {
        id = UUID.randomUUID();
        createdAt = Instant.now().getEpochSecond();
        updatedAt = createdAt;
        users = new ArrayList<>();
    }

    public Channel(String name) {
        id = UUID.randomUUID();
        createdAt = Instant.now().getEpochSecond();
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

    public List<User> getUsers() {
        return users;
    }

    public boolean updateName(String name) {
        if (this.name.equals(name)) {
            return false;
        }
        this.name = name;
        updatedAt = Instant.now().getEpochSecond();
        return true;
    }

    public boolean addUser(User user) { // 나중에 User 삭제할때 deleteUser 쓸 것.
        if (this.users.contains(user)) {
            return false;
        }
        this.users.add(user);
        updatedAt = Instant.now().getEpochSecond();
        return true;

    }

    public boolean deleteUser(User user) {
        if (this.users.contains(user)) {
            this.users.remove(user);
            updatedAt = Instant.now().getEpochSecond();
            return true;
        }
        return false;
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
                ", createdAt=" + Instant.ofEpochSecond(createdAt) +
                ", updatedAt=" + Instant.ofEpochSecond(updatedAt) +
                ", name='" + name + '\'' +
                ", users=" + users +
                '}';
    }
}
