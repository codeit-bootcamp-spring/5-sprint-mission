package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.dto.response.FriendRequestResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FriendRequestMapper {

    public static FriendRequestResponse toFriendRequestResponse(User sender, User receiver) {
        Objects.requireNonNull(sender, "sender must not be null");
        Objects.requireNonNull(receiver, "receiver must not be null");
        return new FriendRequestResponse(
                sender.getId(),
                sender.getUsername(),
                sender.getGlobalName(),
                receiver.getId(),
                receiver.getUsername(),
                receiver.getGlobalName()
        );
    }
}
