package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("test")
public class JcfFriendRequestRepository extends JcfBaseRepository<FriendRequest> implements FriendRequestRepository {

    @Override
    protected String getEntityTypeName() {
        return "FriendRequest";
    }

    @Override
    public boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId) {
        if (senderId == null || receiverId == null || senderId == receiverId) return false;
        return data.values().stream()
                .filter(fr -> !fr.isDeleted())
                .anyMatch(fr -> senderId.equals(fr.getSenderId()) && receiverId.equals(fr.getReceiverId()));
    }

    @Override
    public void clear(UUID userId) {
        if (userId == null) return;
        Set<UUID> ids = findAll().stream()
                .filter(fr -> userId.equals(fr.getSenderId()) || userId.equals(fr.getReceiverId()))
                .map(FriendRequest::getId)
                .collect(Collectors.toSet());
        deleteAllByIds(ids);
    }

    @Override
    public List<FriendRequest> getSentRequests(UUID sender) {
        if (sender == null) return List.of();
        return findAll().stream()
                .filter(fr -> sender.equals(fr.getSenderId()))
                .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<FriendRequest> getReceivedRequests(UUID receiver) {
        return data.values().stream()
                .filter(fr -> fr.getReceiverId().equals(receiver))
                .collect(Collectors.toList());
    }
}
