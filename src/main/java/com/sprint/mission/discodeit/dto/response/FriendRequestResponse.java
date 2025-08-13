package com.sprint.mission.discodeit.dto.response;

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
}
