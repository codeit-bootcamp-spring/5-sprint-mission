package com.sprint.mission.discodeit.service.dev;

import com.sprint.mission.discodeit.domain.entitydev.DevChatRoom;

import java.util.Set;
import java.util.UUID;

public interface DevChatRoomService {

    DevChatRoom create(Set<UUID> participants);

    Set<UUID> getMessages(UUID roomId);

    void addParticipant(UUID roomId, UUID userId);

    void removeParticipant(UUID roomId, UUID userId);
}