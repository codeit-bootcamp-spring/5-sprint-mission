package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.ServerLevel;
import com.sprint.mission.discodeit.enums.ServerPerk;

import java.util.UUID;

public class Server {
    private final UUID id;
    private long createdAt;
    private long updatedAt;
    private Channel[] channels;
    private User[] members;
    private User owner;
    private long boost;
    private ServerLevel level;
    private ServerPerk[] perks;

    public Server(Channel[] channels, long boost, ServerLevel level, ServerPerk[] perks) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.channels = channels;
        this.boost = boost;
        this.level = level;
        this.perks = perks;
    }

    public Server(Channel[] channels, long boost, ServerLevel level) {
        this(channels, boost, level, null);
    }

    public Server(Channel[] channels, long boost) {
        this(channels, boost, ServerLevel.ONE, null);
    }

    public Server(Channel[] channels) {
        this(channels, 0, ServerLevel.ONE, null);
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Channel[] getChannels() {
        return channels;
    }

    public void setChannels(Channel[] channels) {
        this.channels = channels;
    }

    public User[] getMembers() {
        return members;
    }

    public void setMembers(User[] members) {
        this.members = members;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public long getBoost() {
        return boost;
    }

    public void setBoost(long boost) {
        this.boost = boost;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public void setLevel(ServerLevel level) {
        this.level = level;
    }

    public ServerPerk[] getPerks() {
        return perks;
    }

    public void setPerks(ServerPerk[] perks) {
        this.perks = perks;
    }
}
