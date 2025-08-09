package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.domain.entity.FriendRequest;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("prod")
public class JpaFriendRequestRepository extends JpaBaseRepository<FriendRequest> implements FriendRequestRepository {

    protected JpaFriendRequestRepository() {
        super(FriendRequest.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequest> getSentRequests(UUID senderId) {
        return entityManager.createQuery(
                        "SELECT fr FROM FriendRequest fr WHERE fr.sender.id = :senderId AND fr.deleted = false",
                        FriendRequest.class)
                .setParameter("senderId", senderId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequest> getReceivedRequests(UUID receiverId) {
        return entityManager.createQuery(
                        "SELECT fr FROM FriendRequest fr WHERE fr.receiver.id = :receiverId AND fr.deleted = false",
                        FriendRequest.class)
                .setParameter("receiverId", receiverId)
                .getResultList();
    }
}
