package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.FriendRequest;
import java.util.List;
import java.util.UUID;

public interface FriendRequestService extends Service<FriendRequest> {
  void sendFriendRequest(UUID senderId, UUID receiverId);

  void acceptFriendRequest(UUID requestId);

  void declineFriendRequest(UUID requestId);

  void deleteAllRequestsOfUser(UUID userId);

  List<FriendRequest> getSentRequests(UUID senderId);

  List<FriendRequest> getReceivedRequests(UUID receiverId);
}
