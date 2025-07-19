package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class FriendRequest extends AbstractBaseEntity {
  private final UUID senderId;
  private final UUID receiverId;

  public FriendRequest(UUID senderId, UUID receiverId) {
    if (senderId == null || receiverId == null) {
      throw new IllegalArgumentException("SenderId와 ReceiverId는 null일 수 없습니다.");
    }
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
