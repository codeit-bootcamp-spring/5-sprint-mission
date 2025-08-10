package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entitydev.DevFriendRequest;
import com.sprint.mission.discodeit.repository.devrepository.DevFriendRequestRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("test")
public class JcfFriendRequestRepository extends JcfBaseRepository<DevFriendRequest> implements DevFriendRequestRepository {

    @Override
    protected String getEntityTypeName() {
        return "FriendRequest";
    }

    @Override
    public boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId) {
        if (senderId == null || receiverId == null || senderId == receiverId) return false;
        return data.values().stream()
                .filter(fr -> !fr.isDeleted())
                .anyMatch(fr -> senderId.equals(fr.getSender()) && receiverId.equals(fr.getReceiver()));
    }

    @Override
    public void clear(UUID userId) {
        if (userId == null) return;
        Set<UUID> ids = findAll().stream()
                .filter(fr -> userId.equals(fr.getSender()) || userId.equals(fr.getReceiver()))
                .map(DevFriendRequest::getId)
                .collect(Collectors.toSet());
        deleteAllByIds(ids);
    }

    @Override
    public List<DevFriendRequest> getSentRequests(UUID sender) {
        if (sender == null) return List.of();
        return findAll().stream()
                .filter(fr -> sender.equals(fr.getSender()))
                .sorted(Comparator.comparing(DevFriendRequest::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<DevFriendRequest> getReceivedRequests(UUID receiver) {
        return data.values().stream()
                .filter(fr -> fr.getReceiver().equals(receiver))
                .collect(Collectors.toList());
    }
}
