package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.FriendRequest;
import java.util.List;
import java.util.UUID;

public interface FriendRequestService extends Service<FriendRequest> {
  FriendRequest sendFriendRequest(UUID senderId, UUID receiverId);

  void acceptFriendRequest(UUID requestId);

  void declineFriendRequest(UUID requestId);

  void cancelFriendRequest(UUID requestId);

  List<FriendRequest> getPendingRequestsForUser(UUID userId);
}
