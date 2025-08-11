package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.dto.response.FriendRequestResponse;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestMapper {

    public static FriendRequestResponse toFriendRequestResponse(User sender, User receiver) {
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
