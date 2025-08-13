package com.sprint.mission.discodeit.repositoryprod;

import com.sprint.mission.discodeit.domain.entityprod.ProdFriendRequest;

import java.util.List;
import java.util.UUID;

public interface ProdFriendRequestRepository extends ProdBaseRepository<ProdFriendRequest> {

    List<ProdFriendRequest> getSentRequests(UUID senderId);

    List<ProdFriendRequest> getReceivedRequests(UUID receiverId);
}
