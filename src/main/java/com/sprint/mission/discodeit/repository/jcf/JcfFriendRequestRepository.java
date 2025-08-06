package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.FriendRequest;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JcfFriendRequestRepository extends BaseJcfRepository<FriendRequest> implements FriendRequestRepository {
    @Override
    public List<FriendRequest> getSentRequests(UUID senderId) {
        return data.values().stream()
                .filter(fr -> fr.getSenderId().equals(senderId))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequest> getReceivedRequests(UUID receiverId) {
        return data.values().stream()
                .filter(fr -> fr.getReceiverId().equals(receiverId))
                .collect(Collectors.toList());
    }
}
