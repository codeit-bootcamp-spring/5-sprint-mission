package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.FriendRequest;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;

import java.util.List;
import java.util.UUID;

public class FileFriendRequestRepository extends BaseFileRepository<FriendRequest> implements FriendRequestRepository {
    public FileFriendRequestRepository() {
        super(FriendRequest.class);
    }

    @Override
    public List<FriendRequest> getSentRequests(UUID senderId) {
        return List.of();
    }

    @Override
    public List<FriendRequest> getReceivedRequests(UUID receiverId) {
        return List.of();
    }
}