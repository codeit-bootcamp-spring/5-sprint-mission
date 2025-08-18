package com.sprint.mission.discodeit.dto.response.friendrequest;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import com.sprint.mission.discodeit.domain.entity.User;
import java.time.Instant;
import java.util.UUID;

public record FriendRequestResponse(
    UUID id,
    Instant createdAt,
    UUID senderId,
    UUID senderProfileId,
    String senderUsername,
    String senderGlobalName,
    UUID receiverId,
    UUID receiverProfileId,
    String receiverUsername,
    String receiverGlobalName
) {

  public static FriendRequestResponse from(FriendRequest fr, User sender, User receiver) {
    return new FriendRequestResponse(
        fr.getId(),
        fr.getCreatedAt(),
        sender.getId(),
        sender.getProfileId(),
        sender.getUsername(),
        sender.getGlobalName(),
        receiver.getId(),
        receiver.getProfileId(),
        receiver.getUsername(),
        receiver.getGlobalName()
    );
  }
}
