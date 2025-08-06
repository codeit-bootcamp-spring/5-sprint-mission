package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.FriendRequest;

import java.util.List;
import java.util.UUID;

public interface FriendRequestRepository extends BaseRepository<FriendRequest> {
    List<FriendRequest> getSentRequests(UUID senderId);

    List<FriendRequest> getReceivedRequests(UUID receiverId);
}
