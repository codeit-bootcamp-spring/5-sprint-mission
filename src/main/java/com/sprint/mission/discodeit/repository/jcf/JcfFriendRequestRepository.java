package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.deventity.DevFriendRequest;
import com.sprint.mission.discodeit.repository.devrepository.DevFriendRequestRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

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
    public void clear(UUID userId) {
        if (userId == null) return;
        Set<UUID> ids = data.values().stream()
                .filter(fr -> !fr.isDeleted())
                .filter(fr -> userId.equals(fr.getSender()) || userId.equals(fr.getReceiver()))
                .map(DevFriendRequest::getId)
                .collect(Collectors.toSet());
        deleteAllByIds(ids);
    }

    @Override
    public boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId) {
        if (senderId == null || receiverId == null || senderId == receiverId) return false;
        DevFriendRequest probe = new DevFriendRequest(senderId, receiverId);
        return data.values().stream()
                .filter(fr -> !fr.isDeleted())
                .anyMatch(probe::equals);
    }

    @Override
    public List<DevFriendRequest> getSentRequests(UUID sender) {
        return data.values().stream()
                .filter(fr -> fr.getSender().equals(sender))
                .collect(Collectors.toList());
    }

    @Override
    public List<DevFriendRequest> getReceivedRequests(UUID receiver) {
        return data.values().stream()
                .filter(fr -> fr.getReceiver().equals(receiver))
                .collect(Collectors.toList());
    }
}
