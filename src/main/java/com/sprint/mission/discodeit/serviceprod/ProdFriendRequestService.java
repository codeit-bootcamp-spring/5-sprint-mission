package com.sprint.mission.discodeit.serviceprod;

import com.sprint.mission.discodeit.domain.entityprod.ProdFriendRequest;

import java.util.List;
import java.util.UUID;

public interface ProdFriendRequestService {

    List<ProdFriendRequest> listSent(UUID senderId);

    List<ProdFriendRequest> listReceived(UUID receiverId);

    List<ProdFriendRequest> listAllMine(UUID userId);

    ProdFriendRequest send(UUID senderId, UUID receiverId);

    void accept(UUID requestId);

    void reject(UUID requestId);

    void clear(UUID userId);
}
