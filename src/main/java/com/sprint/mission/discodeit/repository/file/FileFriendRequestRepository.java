package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.entitydev.DevFriendRequest;
import com.sprint.mission.discodeit.repository.devrepository.DevFriendRequestRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
@Profile("dev")
public class FileFriendRequestRepository extends FileBaseRepository<DevFriendRequest> implements DevFriendRequestRepository {

    public FileFriendRequestRepository(AppStorageProperties storageProperties) {
        super(DevFriendRequest.class, storageProperties);
    }

    @Override
    public boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId) {
        if (senderId == null || receiverId == null || senderId == receiverId) return false;
        return findAll().stream()
                .anyMatch(fr -> senderId.equals(fr.getSender()) && receiverId.equals(fr.getReceiver()));
    }

    @Override
    public void clear(UUID userId) {
        if (userId == null) return;
        Set<UUID> ids = new HashSet<>();
        getReceivedRequests(userId).forEach(fr -> ids.add(fr.getId()));
        getSentRequests(userId).forEach(fr -> ids.add(fr.getId()));
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
        if (receiver == null) return List.of();
        return findAll().stream()
                .filter(fr -> receiver.equals(fr.getReceiver()))
                .sorted(Comparator.comparing(DevFriendRequest::getCreatedAt).reversed())
                .toList();
    }
}
