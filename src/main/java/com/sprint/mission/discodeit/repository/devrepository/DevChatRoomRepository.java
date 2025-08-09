package com.sprint.mission.discodeit.repository.devrepository;

import com.sprint.mission.discodeit.domain.entitydev.DevChatRoom;
import com.sprint.mission.discodeit.repository.BaseRepository;

import java.util.Set;
import java.util.UUID;

public interface DevChatRoomRepository extends BaseRepository<DevChatRoom> {

    boolean isParticipant(UUID chatRoomId, UUID userId);

    boolean existsByParticipants(Set<UUID> participants);
}
