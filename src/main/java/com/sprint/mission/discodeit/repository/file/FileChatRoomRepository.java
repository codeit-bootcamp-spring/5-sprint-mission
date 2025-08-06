package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ChatRoom;
import com.sprint.mission.discodeit.repository.ChatRoomRepository;

public class FileChatRoomRepository extends BaseFileRepository<ChatRoom> implements ChatRoomRepository {
    public FileChatRoomRepository() {
        super(ChatRoom.class);
    }

    @Override
    public boolean existsByParticipants(int participantsHashcode) {
        return false;
    }
}