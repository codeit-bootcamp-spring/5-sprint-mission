package com.sprint.mission.discodeit.repositoryprod.impl;

import com.sprint.mission.discodeit.domain.entityprod.ProdBaseEntity;
import com.sprint.mission.discodeit.domain.entityprod.ProdChatRoom;
import com.sprint.mission.discodeit.repositoryprod.ProdChatRoomRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("prod")
public class JpaChatRoomRepository extends JpaBaseRepository<ProdChatRoom> implements ProdChatRoomRepository {

    protected JpaChatRoomRepository() {
        super(ProdChatRoom.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isParticipant(UUID chatRoomId, UUID userId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c) FROM ProdChatRoom c JOIN c.participants p "
                                + "WHERE c.id = :chatRoomId AND p.id = :userId AND c.deleted = false",
                        Long.class)
                .setParameter("chatRoomId", chatRoomId)
                .setParameter("userId", userId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByParticipants(Set<UUID> participantIds) {
        int hash = ProdChatRoom.computeParticipantsHashcodeById(participantIds);

        List<ProdChatRoom> candidates = entityManager.createQuery(
                        "SELECT c FROM ProdChatRoom c JOIN c.participants p "
                                + "WHERE c.participantsHashcode = :hash AND c.deleted = false "
                                + "GROUP BY c "
                                + "HAVING COUNT(p) = :size", ProdChatRoom.class)
                .setParameter("hash", hash)
                .setParameter("size", (long) participantIds.size())
                .getResultList();

        return candidates.stream()
                .anyMatch(chatRoom ->
                        chatRoom.getParticipants().stream()
                                .map(ProdBaseEntity::getId)
                                .collect(Collectors.toSet())
                                .equals(participantIds));
    }
}
