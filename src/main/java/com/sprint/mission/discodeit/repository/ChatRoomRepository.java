package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.ChatRoom;

import java.util.Set;
import java.util.UUID;

public interface ChatRoomRepository extends BaseRepository<ChatRoom> {

    boolean isParticipant(UUID chatRoomId, UUID userId);

    boolean existsByParticipants(Set<UUID> participants);
}
