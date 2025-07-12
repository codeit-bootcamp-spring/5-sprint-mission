package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.ChannelCategory;

import java.util.List;
import java.util.UUID;

public class Channel {
    private final UUID id;
    private long createdAt;
    private long updatedAt;
    private String name;
    private String groupName;
    private ChannelCategory category;
    private boolean isPublic;
    private List<User> allowedUsers;

    public Channel(String name, String groupName, ChannelCategory category, boolean isPublic, List<User> allowedUsers) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
        this.groupName = groupName;
        this.category = category;
        this.isPublic = isPublic;
        this.allowedUsers = allowedUsers;
    }

    public Channel(String name, String groupName, ChannelCategory category, boolean isPublic) {
        this(name, groupName, category, isPublic, null);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ChannelCategory getCategory() {
        return category;
    }

    public void setCategory(ChannelCategory category) {
        this.category = category;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<User> getAllowedUsers() {
        return allowedUsers;
    }

    public void setAllowedUsers(List<User> allowedUsers) {
        this.allowedUsers = allowedUsers;
    }
}
