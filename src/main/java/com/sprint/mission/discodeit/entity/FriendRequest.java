package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class FriendRequest extends AbstractBaseEntity {
  private final UUID senderId;
  private final UUID receiverId;

  public FriendRequest(UUID senderId, UUID receiverId) {
    this.senderId = senderId;
    this.receiverId = receiverId;
  }

  public UUID getSenderId() {
    return senderId;
  }

  public UUID getReceiverId() {
    return receiverId;
  }

  @Override
  public String toString() {
    return "FriendRequest{"
        + "id="
        + getId()
        + ", createdAt="
        + getCreatedAt()
        + ", updatedAt="
        + getUpdatedAt()
        + ", senderId="
        + senderId
        + ", receiverId="
        + receiverId
        + '}';
  }
}
