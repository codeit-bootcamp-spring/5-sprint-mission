package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.domain.entity.BaseEntity;
import com.sprint.mission.discodeit.domain.entity.ChatRoom;
import com.sprint.mission.discodeit.repository.ChatRoomRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("prod")
public class JpaChatRoomRepository extends JpaBaseRepository<ChatRoom> implements ChatRoomRepository {

    protected JpaChatRoomRepository() {
        super(ChatRoom.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isParticipant(UUID chatRoomId, UUID userId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c) FROM ChatRoom c JOIN c.participants p "
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
        int hash = ChatRoom.computeParticipantsHashcodeById(participantIds);

        List<ChatRoom> candidates = entityManager.createQuery(
                        "SELECT c FROM ChatRoom c JOIN c.participants p "
                                + "WHERE c.participantsHashcode = :hash AND c.deleted = false "
                                + "GROUP BY c "
                                + "HAVING COUNT(p) = :size", ChatRoom.class)
                .setParameter("hash", hash)
                .setParameter("size", (long) participantIds.size())
                .getResultList();

        return candidates.stream()
                .anyMatch(chatRoom ->
                        chatRoom.getParticipants().stream()
                                .map(BaseEntity::getId)
                                .collect(Collectors.toSet())
                                .equals(participantIds));
    }
}
