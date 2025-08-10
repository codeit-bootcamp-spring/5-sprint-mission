package com.sprint.mission.discodeit.serviceprod;

import com.sprint.mission.discodeit.domain.entityprod.ProdChatRoom;

import java.util.Set;
import java.util.UUID;

public interface ProdChatRoomService {

    ProdChatRoom create(Set<UUID> participants);

    Set<UUID> getMessages(UUID roomId);

    void addParticipant(UUID roomId, UUID userId);

    void removeParticipant(UUID roomId, UUID userId);
}
