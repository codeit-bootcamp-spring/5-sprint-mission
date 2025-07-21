package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.FriendRequest;
import java.util.List;
import java.util.UUID;

public interface FriendRequestService extends BaseService<FriendRequest> {
  boolean acceptFriendRequest(UUID requestId);

  boolean declineFriendRequest(UUID requestId);

  void clearFriendRequests(UUID userId);

  List<FriendRequest> getSentRequests(UUID senderId);

  List<FriendRequest> getReceivedRequests(UUID receiverId);
}
