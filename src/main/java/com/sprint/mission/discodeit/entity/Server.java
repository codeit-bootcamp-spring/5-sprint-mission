package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.serverEntity.ServerLevel;
import com.sprint.mission.discodeit.enums.serverEntity.ServerPerk;

import java.util.List;
import java.util.UUID;

public class Server {
    private final UUID id;
    private long createdAt;
    private long updatedAt;
    private String name;
    private List<Channel> channels;
    private List<User> members;
    private User owner;
    private boolean isPublic;
    private long boost;
    private ServerLevel level;
    private List<ServerPerk> perks;

    public Server(String name, User owner, boolean isPublic, List<User> members, List<Channel> channels, long boost, ServerLevel level, List<ServerPerk> perks) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
        this.owner = owner;
        this.isPublic = isPublic;
        this.members = members;
        this.channels = channels;
        this.boost = boost;
        this.level = level;
        this.perks = perks;
    }

    public Server(String name, User owner, boolean isPublic, List<User> members, List<Channel> channels, long boost, ServerLevel level) {
        this(name, owner, isPublic, members, channels, boost, level, null);
    }

    public Server(String name, User owner, boolean isPublic, List<User> members, List<Channel> channels, long boost) {
        this(name, owner, isPublic, members, channels, boost, ServerLevel.ONE, null);
    }

    public Server(String name, User owner, boolean isPublic, List<User> members, List<Channel> channels) {
        this(name, owner, isPublic, members, channels, 0, ServerLevel.ONE, null);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
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

    public List<ServerPerk> getPerks() {
        return perks;
    }

    public void setPerks(List<ServerPerk> perks) {
        this.perks = perks;
    }

    @Override
    public String toString() {
        return "Server{" + "name='" + name + '\'' +
                ", owner=" + owner.getUsername() +
                ", isPublic=" + isPublic +
                '}';
    }
}
