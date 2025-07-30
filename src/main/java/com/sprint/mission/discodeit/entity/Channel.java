package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String name;
    private final List<UUID> users;
    private String description;
    private ChannelType channelType;

    public enum ChannelType {
        PUBLIC, PRIVATE;
    }

    public Channel() {
        id = UUID.randomUUID();
        createdAt = Instant.now().toEpochMilli();
        updatedAt = createdAt;
        users = new ArrayList<>();
    }

    public Channel(String name, String description, ChannelType channelType) {
        id = UUID.randomUUID();
        createdAt = Instant.now().toEpochMilli();
        updatedAt = createdAt;
        users = new ArrayList<>();
        this.name = name;
        this.description = description;
        this.channelType = channelType;
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

    public ChannelType getChannelType() {
        return channelType;
    }

    public String getDescription() {
        return description;
    }

    public Channel updateName(String name) {
        if (name.equals(this.name)) {
            System.out.println("기존에 사용하던 채널명입니다.");
            return this;
        }
        this.name = name;
        updatedAt = Instant.now().toEpochMilli();
        return this;
    }

    public void updateDescription(String description) {
        this.description = description;
        updatedAt = Instant.now().toEpochMilli();
    }

    public void updateChannelType(ChannelType channelType) {
        this.channelType = channelType;
        updatedAt = Instant.now().toEpochMilli();
    }

    public Channel addUser(User user) {
        if (!users.contains(user.getId())) {
            this.users.add(user.getId());
            user.getChannels().add(this.id);
            updatedAt = Instant.now().toEpochMilli();
            return this;
        }
        System.out.println("이미 가입된 유저입니다.");
        return this;
    }

    public Channel deleteUser(User user) {
        if (!users.contains(user.getId())) {
            System.out.println("해당 유저를 찾을 수 없습니다.");
            return this;
        }
        this.users.remove(user.getId());
        user.getChannels().remove(this.id);
        updatedAt = Instant.now().toEpochMilli();
        return this;
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
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", users=" + users +
                ", description='" + description + '\'' +
                ", channelType='" + channelType + '\'' +
                '}';
    }
}
