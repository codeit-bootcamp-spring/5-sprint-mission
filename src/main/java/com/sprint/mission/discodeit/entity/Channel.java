package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String name;
    private int userCounts;
    private final List<User> users;

    public Channel() {
        id = UUID.randomUUID();
        createdAt = Instant.now().getEpochSecond();
        updatedAt = createdAt;
        userCounts = 0;
        users = new ArrayList<>();
    }

    public Channel(String name) {
        id = UUID.randomUUID();
        createdAt = Instant.now().getEpochSecond();
        updatedAt = createdAt;
        users = new ArrayList<>();
        this.name = name;
        userCounts = 0;
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

    public int getUserCounts() {
        return userCounts;
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

    public void updateUser(User user) { // addUser랑 deleteUser로 분리해야 할 듯, 나중에 User 삭제할때 deleteUser 쓸 것.
        if (this.users.contains(user)) {
            this.users.remove(user);
            userCounts--;
            updatedAt = Instant.now().getEpochSecond();
        } else {
            this.users.add(user);
            userCounts++;
            updatedAt = Instant.now().getEpochSecond();
        }
    }
}
