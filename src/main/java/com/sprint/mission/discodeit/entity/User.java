package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String name;
    private final List<UUID> channels;

    public User() {
        id = UUID.randomUUID();
        createdAt = Instant.now().toEpochMilli();
        updatedAt = createdAt;
        channels = new ArrayList<>();
    }

    public User(String name) {
        id = UUID.randomUUID();
        createdAt = Instant.now().toEpochMilli();
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

    public List<UUID> getChannels() {
        return channels;
    }

    public User updateName(String name) {
        if (name.equals(this.name)) {
            System.out.println("기존에 사용하던 이름입니다.");
            return this;
        }
        this.name = name;
        updatedAt = Instant.now().toEpochMilli();
        return this;
    }

    public User addChannel(Channel channel) {
        if (!channels.contains(channel.getId())) {
            this.channels.add(channel.getId());
            channel.getUsers().add(this.id);
            updatedAt = Instant.now().toEpochMilli();
            return this;
        }
        System.out.println("이미 가입된 채널입니다.");
        return this;
    }

    public User deleteChannel(Channel channel) {
        if (!channels.contains(channel.getId())) {
            System.out.println("해당 채널에 가입되어있지 않습니다.");
            return this;
        }
        this.channels.remove(channel.getId());
        channel.getUsers().remove(this.id);
        updatedAt = Instant.now().toEpochMilli();
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
                ", createdAt=" + Instant.ofEpochMilli(createdAt) +
                ", updatedAt=" + Instant.ofEpochMilli(updatedAt) +
                ", name='" + name + '\'' +
                ", channels=" + channels +
                '}';
    }
}
