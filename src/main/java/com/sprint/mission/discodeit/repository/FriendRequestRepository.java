package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import java.util.List;
import java.util.UUID;

public interface FriendRequestRepository extends AbstractRepository<FriendRequest> {

  List<FriendRequest> findAllBySenderId(UUID senderId);

  List<FriendRequest> findAllByReceiverId(UUID receiverId);

  boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId);

  boolean existsBetween(UUID userA, UUID userB);

  void softDeleteAllByUserId(UUID userId);

  boolean softDeleteBySenderAndReceiver(UUID senderId, UUID receiverId);

  boolean hardDeleteBySenderAndReceiver(UUID senderId, UUID receiverId);
}
