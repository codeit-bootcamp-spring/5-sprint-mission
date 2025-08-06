package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ChatRoom;
import com.sprint.mission.discodeit.repository.ChatRoomRepository;

public class JcfChatRoomRepository extends BaseJcfRepository<ChatRoom> implements ChatRoomRepository {
    @Override
    public boolean existsByParticipants(int participantsHashcode) {
        return data.values().stream()
                .filter(cr -> !cr.isDeleted())
                .anyMatch(cr -> cr.participantsHashcode() == participantsHashcode);
    }
}
