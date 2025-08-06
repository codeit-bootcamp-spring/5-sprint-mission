package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.FriendRequest;

import java.util.List;
import java.util.UUID;

public interface FriendRequestService extends BaseService<FriendRequest> {
    void acceptFriendRequest(UUID requestId);

    void declineFriendRequest(UUID requestId);

    void clearFriendRequests(UUID userId);

    List<FriendRequest> getSentRequests(UUID senderId);

    List<FriendRequest> getReceivedRequests(UUID receiverId);
}
