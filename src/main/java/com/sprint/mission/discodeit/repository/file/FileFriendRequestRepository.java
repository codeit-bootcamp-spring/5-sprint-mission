package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@Profile("dev")
public class FileFriendRequestRepository extends FileBaseRepository<FriendRequest> implements FriendRequestRepository {

    public FileFriendRequestRepository(AppProperties appProperties) {
        super(FriendRequest.class, appProperties.storage());
    }

    @Override
    public boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId) {
        Objects.requireNonNull(senderId, "senderId must not be null");
        Objects.requireNonNull(receiverId, "receiverId must not be null");
        if (senderId.equals(receiverId)) return false;
        return findAll().stream()
                .anyMatch(fr -> senderId.equals(fr.getSenderId()) && receiverId.equals(fr.getReceiverId()));
    }

    @Override
    public boolean existsBetween(UUID userA, UUID userB) {
        Objects.requireNonNull(userA, "userA must not be null");
        Objects.requireNonNull(userB, "userB must not be null");
        if (userA.equals(userB)) return false;
        return findAll().stream().anyMatch(fr ->
                (userA.equals(fr.getSenderId()) && userB.equals(fr.getReceiverId()))
                        || (userB.equals(fr.getSenderId()) && userA.equals(fr.getReceiverId()))
        );
    }

    @Override
    public List<FriendRequest> findAllBySenderId(UUID senderId) {
        Objects.requireNonNull(senderId, "senderId must not be null");
        return findAll().stream()
                .filter(fr -> senderId.equals(fr.getSenderId()))
                .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<FriendRequest> findAllByReceiverId(UUID receiverId) {
        Objects.requireNonNull(receiverId, "receiverId must not be null");
        return findAll().stream()
                .filter(fr -> receiverId.equals(fr.getReceiverId()))
                .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public int softDeleteAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Set<UUID> ids = new HashSet<>();
        findAll().stream()
                .filter(fr -> userId.equals(fr.getSenderId()) || userId.equals(fr.getReceiverId()))
                .forEach(fr -> ids.add(fr.getId()));
        return softDeleteAllByIds(ids);
    }

    @Override
    public boolean softDeleteBySenderAndReceiver(UUID senderId, UUID receiverId) {
        Objects.requireNonNull(senderId, "senderId must not be null");
        Objects.requireNonNull(receiverId, "receiverId must not be null");
        Optional<FriendRequest> fr = findAll().stream()
                .filter(e -> senderId.equals(e.getSenderId()) && receiverId.equals(e.getReceiverId()))
                .findFirst();
        return fr.map(e -> softDeleteById(e.getId())).orElse(false);
    }
}
