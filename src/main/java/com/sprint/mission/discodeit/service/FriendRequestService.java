package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;

import java.util.List;
import java.util.UUID;

public interface FriendRequestService {

    List<FriendRequest> listSent(UUID senderId);

    List<FriendRequest> listReceived(UUID receiverId);

    List<FriendRequest> listAllMine(UUID userId);

    UUID send(UUID senderId, UUID receiverId);

    void accept(UUID requestId);

    void reject(UUID requestId);

    void clear(UUID userId);
}
