package com.sprint.mission.discodeit.serviceprod;

import com.sprint.mission.discodeit.domain.entityprod.ProdFriendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Profile("prod")
public class JpaFriendRequestService {
    public List<ProdFriendRequest> listSent(UUID senderId) {
        return List.of();
    }

    public List<ProdFriendRequest> listReceived(UUID receiverId) {
        return List.of();
    }

    public List<ProdFriendRequest> listAllMine(UUID userId) {
        return List.of();
    }

    public ProdFriendRequest send(UUID senderId, UUID receiverId) {
        return null;
    }

    public void accept(UUID requestId) {

    }

    public void reject(UUID requestId) {

    }

    public void clear(UUID userId) {

    }
}
