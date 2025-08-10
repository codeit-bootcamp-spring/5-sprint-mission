package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import com.sprint.mission.discodeit.service.FriendRequestService;
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
public class JpaFriendRequestService implements FriendRequestService {
    @Override
    public List<FriendRequest> listSent(UUID senderId) {
        return List.of();
    }

    @Override
    public List<FriendRequest> listReceived(UUID receiverId) {
        return List.of();
    }

    @Override
    public List<FriendRequest> listAllMine(UUID userId) {
        return List.of();
    }

    @Override
    public FriendRequest send(UUID senderId, UUID receiverId) {
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
