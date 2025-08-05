package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChatRoom;

import java.util.List;
import java.util.UUID;

public interface ChatRoomRepository extends BaseRepository<ChatRoom> {
    boolean existsByParticipants(int participantsHashcode);

    void addMessage(UUID chatRoomId, UUID messageId);

    List<UUID> getMessages(UUID chatRoomId);

    void addParticipant(UUID chatRoomId, UUID userId);

    void removeParticipant(UUID chatRoomId, UUID userId);
}
