package com.sprint.mission.discodeit.dto.request.friendrequest;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record FriendRequestHandleRequest(

    @NotNull
    UUID userId
) {

}
