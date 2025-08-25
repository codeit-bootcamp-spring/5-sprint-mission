package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Channel extends BaseEntity {

  private ChannelType type;
  private String name;
  private String description;
  private final UUID adminUserId;
  private List<UUID> userIds;
  private List<UUID> messageIds;

  public Channel(ChannelType type, String name, String description, UUID adminUserId,
      List<UUID> userIds) {
    super();
    this.type = type == null ? ChannelType.PUBLIC : type;
    this.name = name;
    this.description = description;
    this.adminUserId = adminUserId;
    this.userIds = userIds == null ? new ArrayList<>(List.of()) : userIds;
    this.messageIds = new ArrayList<>(List.of());
  }

  public void update(String newName, String newDescription) {
    boolean anyValueUpdated = false;

    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
      anyValueUpdated = true;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      updateTimestamp();
    }
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
}
