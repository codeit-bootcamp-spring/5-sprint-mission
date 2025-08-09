package com.sprint.mission.discodeit.repository.devrepository;

import com.sprint.mission.discodeit.domain.entitydev.DevFriendRequest;
import com.sprint.mission.discodeit.repository.BaseRepository;

import java.util.List;
import java.util.UUID;

public interface DevFriendRequestRepository extends BaseRepository<DevFriendRequest> {

    void clear(UUID userId);

    boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId);

    List<DevFriendRequest> getSentRequests(UUID senderId);

    List<DevFriendRequest> getReceivedRequests(UUID receiverId);
}
