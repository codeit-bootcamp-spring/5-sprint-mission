package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FriendRequestSendRequest(
        @NotNull UUID senderId,
        @NotBlank String receiverUsername
) {
}
