package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

    public boolean updateName(String name) {
        if (this.name.equals(name)) {
            return false;
        }
        this.name = name;
        updatedAt = Instant.now().getEpochSecond();
        return true;
    }

    public void updateChannels(Channel channel) {
        if (this.channels.contains(channel)) {
            this.channels.remove(channel);
            updatedAt = Instant.now().getEpochSecond();
        } else {
            channels.add(channel);
            updatedAt = Instant.now().getEpochSecond();
        }
    }
}
