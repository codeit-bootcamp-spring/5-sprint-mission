package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.friend.FriendRequestStatus;
import java.util.UUID;

public class FriendRequest extends AbstractBaseEntity {
  private final UUID senderId;
  private final UUID receiverId;
  private FriendRequestStatus status;

  public FriendRequest(UUID senderId, UUID receiverId) {
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.status = FriendRequestStatus.PENDING;
  }

  public UUID getSenderId() {
    return senderId;
  }

  public UUID getReceiverId() {
    return receiverId;
  }

  public FriendRequestStatus getStatus() {
    return status;
  }

  public void setStatus(FriendRequestStatus status) {
    this.status = status;
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
        + ", status="
        + status
        + '}';
  }
}
