package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends BaseEntity {
    private String name;
    private String description;
    private UUID adminUserId;
    private List<UUID> userIds;
    private List<UUID> messageIds;

    public Channel(String name, String description, UUID adminUserId) {
        super();
        this.name = name;
        this.description = description;
        this.adminUserId = adminUserId;
        this.userIds = new ArrayList<>(List.of(adminUserId));
        this.messageIds = new ArrayList<>();
    }


    public Channel(String name, String description, UUID adminUserId, List<UUID> userIds, List<UUID> messageIds) {
        super();
        this.name = name;
        this.description = description;
        this.adminUserId = adminUserId;
        this.userIds = userIds == null ? new ArrayList<>(List.of(adminUserId)) : userIds;
        this.messageIds = messageIds == null ? new ArrayList<>(List.of()) : messageIds;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public void update(String name, String description) {
        setName(name);
        setDescription(description);
        setUpdatedAt(System.currentTimeMillis());
    }

    public List<UUID> getUserIds() {
        return userIds;
    }

    private void setUserIds(List<UUID> userIds) {
        this.userIds = userIds;
    }

    public UUID getAdminUserId() {
        return adminUserId;
    }

    private void setAdminUserId(UUID adminUserId) {
        this.adminUserId = adminUserId;
    }

    public List<UUID> getMessageIds() {
        return messageIds;
    }

    private void setMessageIds(List<UUID> messageIds) {
        this.messageIds = messageIds;
    }

    public void addUser(UUID userId) {
        if (userId != null) {
            this.userIds.add(userId);
        }
    }

    public void removeUser(UUID userId) {
        if (userId != null) {
            this.userIds.remove(userId);
        }
    }

    public void addMessage(UUID messageId) {
        if (messageId != null) {
            this.messageIds.add(messageId);
        }
    }

    public void removeMessage(UUID messageId) {
        if (messageId != null) {
            this.messageIds.remove(messageId);
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            ", adminUserId=" + adminUserId +
            ", userIds=" + userIds +
            ", messageIds=" + messageIds +
            '}';
    }
}
