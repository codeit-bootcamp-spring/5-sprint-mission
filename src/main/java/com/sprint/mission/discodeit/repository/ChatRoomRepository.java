package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ChatRoom;

public interface ChatRoomRepository extends BaseRepository<ChatRoom> {
    boolean existsByParticipants(int participantsHashcode);
}
