package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record FriendRequestResponse(
        UUID senderId,
        String senderUsername,
        String senderGlobalName,
        UUID receiverId,
        String receiverUsername,
        String receiverGlobalName
) {
}
