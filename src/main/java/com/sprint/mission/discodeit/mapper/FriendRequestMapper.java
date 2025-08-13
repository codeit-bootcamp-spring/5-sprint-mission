package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.dto.response.FriendRequestResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FriendRequestMapper {

    public static FriendRequestResponse toFriendRequestResponse(FriendRequest fr, User sender, User receiver) {
        Objects.requireNonNull(fr, "fr must not be null");
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
