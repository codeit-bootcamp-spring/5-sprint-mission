package com.sprint.mission.discodeit.service.dev;

import com.sprint.mission.discodeit.domain.deventity.DevFriendRequest;

import java.util.List;
import java.util.UUID;

public interface DevFriendRequestService {

    List<DevFriendRequest> listSent(UUID senderId);

    List<DevFriendRequest> listReceived(UUID receiverId);

    List<DevFriendRequest> listAllMine(UUID userId);

    DevFriendRequest send(UUID senderId, UUID receiverId);

    void accept(UUID requestId);

    void reject(UUID requestId);

    void clear(UUID userId);
}
