package com.sprint.mission.discodeit.repositoryprod;

import com.sprint.mission.discodeit.domain.entityprod.ProdChatRoom;

import java.util.Set;
import java.util.UUID;

public interface ProdChatRoomRepository extends ProdBaseRepository<ProdChatRoom> {

    boolean isParticipant(UUID chatRoomId, UUID userId);

    boolean existsByParticipants(Set<UUID> participants);
}
