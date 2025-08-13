package com.sprint.mission.discodeit.repositoryprod.impl;

import com.sprint.mission.discodeit.domain.entityprod.ProdFriendRequest;
import com.sprint.mission.discodeit.repositoryprod.ProdFriendRequestRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("prod")
public class JpaFriendRequestRepository extends JpaBaseRepository<ProdFriendRequest> implements ProdFriendRequestRepository {

    protected JpaFriendRequestRepository() {
        super(ProdFriendRequest.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdFriendRequest> getSentRequests(UUID senderId) {
        return entityManager.createQuery(
                        "SELECT fr FROM ProdFriendRequest fr WHERE fr.sender.id = :senderId AND fr.deleted = false",
                        ProdFriendRequest.class)
                .setParameter("senderId", senderId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdFriendRequest> getReceivedRequests(UUID receiverId) {
        return entityManager.createQuery(
                        "SELECT fr FROM ProdFriendRequest fr WHERE fr.receiver.id = :receiverId AND fr.deleted = false",
                        ProdFriendRequest.class)
                .setParameter("receiverId", receiverId)
                .getResultList();
    }
}
