package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class FriendRequest extends BaseEntity implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private final UUID senderId;
  private final UUID receiverId;

  public FriendRequest(UUID senderId, UUID receiverId) {
    if (senderId == null) {
      throw new IllegalArgumentException("Sender ID must not be null.");
    }
    if (receiverId == null) {
      throw new IllegalArgumentException("Receiver ID must not be null.");
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
    return "FriendRequest{" + "senderId=" + senderId + ", receiverId=" + receiverId + '}';
  }
}
