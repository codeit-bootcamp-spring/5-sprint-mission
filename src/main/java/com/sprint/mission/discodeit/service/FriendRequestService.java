package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface FriendRequestService {

    void send(UUID senderId, UUID receiverId);

    void accept(UUID requestId);

    void decline(UUID requestId);

    void clear(UUID userId);
}
