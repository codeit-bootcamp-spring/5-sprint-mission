package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {

    private ChannelType type;
    private String name;
    private String description;
    private UUID adminUserId;
    private List<UUID> userIds;
    private List<UUID> messageIds;

    public Channel(ChannelType type, String name, String description, UUID adminUserId) {
        super();
        this.type = type == null ? ChannelType.PUBLIC : type;
        this.name = name;
        this.description = description;
        this.adminUserId = adminUserId;
        this.userIds = new ArrayList<>(List.of(adminUserId));
        this.messageIds = new ArrayList<>();
    }


    public Channel(ChannelType type, String name, String description, UUID adminUserId, List<UUID> userIds, List<UUID> messageIds) {
        super();
        this.type = type == null ? ChannelType.PUBLIC : type;
        this.type = ChannelType.PUBLIC;
        this.name = name;
        this.description = description;
        this.adminUserId = adminUserId;
        this.userIds = userIds == null ? new ArrayList<>(List.of(adminUserId)) : userIds;
        this.messageIds = messageIds == null ? new ArrayList<>(List.of()) : messageIds;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        updateTimestamp();
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
            "type=" + type +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", adminUserId=" + adminUserId +
            ", userIds=" + userIds +
            ", messageIds=" + messageIds +
            ", id=" + id +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
