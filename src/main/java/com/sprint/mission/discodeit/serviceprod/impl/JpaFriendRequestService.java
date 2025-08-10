package com.sprint.mission.discodeit.serviceprod.impl;

import com.sprint.mission.discodeit.domain.entityprod.ProdFriendRequest;
import com.sprint.mission.discodeit.serviceprod.ProdFriendRequestService;
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
public class JpaFriendRequestService implements ProdFriendRequestService {
    @Override
    public List<ProdFriendRequest> listSent(UUID senderId) {
        return List.of();
    }

    @Override
    public List<ProdFriendRequest> listReceived(UUID receiverId) {
        return List.of();
    }

    @Override
    public List<ProdFriendRequest> listAllMine(UUID userId) {
        return List.of();
    }

    @Override
    public ProdFriendRequest send(UUID senderId, UUID receiverId) {
        return null;
    }

    @Override
    public void accept(UUID requestId) {

    }

    @Override
    public void reject(UUID requestId) {

    }

    @Override
    public void clear(UUID userId) {

    }
}
