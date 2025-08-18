package com.sprint.mission.discodeit.domain.entity;

import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class FriendRequest extends AbstractEntity {

  private final UUID senderId;
  private final UUID receiverId;

  public FriendRequest(UUID senderId, UUID receiverId) {
    this.senderId = Objects.requireNonNull(senderId, "senderId must not be null.");
    this.receiverId = Objects.requireNonNull(receiverId, "receiverId must not be null.");
    if (senderId.equals(receiverId)) {
      throw new IllegalArgumentException("자기 자신에게 보낼 수 없습니다.");
    }
  }

  public void verifySender(UUID actorId) {
    if (!isSender(actorId)) {
      throw new IllegalStateException("Only sender can perform this action.");
    }
  }

  public void verifyReceiver(UUID actorId) {
    if (!isReceiver(actorId)) {
      throw new IllegalStateException("Only receiver can perform this action.");
    }
  }

  public boolean isSender(UUID userId) {
    return senderId.equals(Objects.requireNonNull(userId, "userId must not be null"));
  }

  public boolean isReceiver(UUID userId) {
    return receiverId.equals(Objects.requireNonNull(userId, "userId must not be null"));
  }

  public UUID otherParty(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    if (isSender(userId)) {
      return receiverId;
    }
    if (isReceiver(userId)) {
      return senderId;
    }
    throw new IllegalStateException("User is not a participant of this request.");
  }

  public UUID pairLow() {
    return senderId.compareTo(receiverId) <= 0 ? senderId : receiverId;
  }

  public UUID pairHigh() {
    return senderId.compareTo(receiverId) <= 0 ? receiverId : senderId;
  }

  @Override
  public String toString() {
    return "FriendRequest[id=%s, from=%s, to=%s]"
        .formatted(getId(), senderId, receiverId);
  }
}
