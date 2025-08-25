package com.sprint.mission.discodeit.dto.friendrequest;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import com.sprint.mission.discodeit.domain.entity.User;
import java.time.Instant;
import java.util.UUID;

public record FriendRequestDto(

    UUID id,
    Instant createdAt,
    UUID senderId,
    UUID senderProfileId,
    String senderUsername,
    UUID receiverId,
    UUID receiverProfileId,
    String receiverUsername
) {

  public static FriendRequestDto from(FriendRequest fr, User sender, User receiver) {
    return new FriendRequestDto(
        fr.getId(),
        fr.getCreatedAt(),
        sender.getId(),
        sender.getProfileId(),
        sender.getUsername(),
        receiver.getId(),
        receiver.getProfileId(),
        receiver.getUsername()
    );
  }
}
