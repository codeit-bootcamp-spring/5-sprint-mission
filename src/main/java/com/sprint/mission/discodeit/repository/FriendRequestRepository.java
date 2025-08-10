package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;

import java.util.List;
import java.util.UUID;

public interface FriendRequestRepository extends BaseRepository<FriendRequest> {

    void clear(UUID userId);

    boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId);

    List<FriendRequest> getSentRequests(UUID senderId);

    List<FriendRequest> getReceivedRequests(UUID receiverId);
}
