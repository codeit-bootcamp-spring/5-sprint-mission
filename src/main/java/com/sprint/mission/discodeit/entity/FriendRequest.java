package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

public class FriendRequest extends BaseEntity {
  private final UUID senderId;
  private final UUID receiverId;

  public FriendRequest(UUID senderId, UUID receiverId) {
    this.senderId = Objects.requireNonNull(senderId, "Sender ID must not be null");
    this.receiverId = Objects.requireNonNull(receiverId, "Receiver ID must not be null");
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
