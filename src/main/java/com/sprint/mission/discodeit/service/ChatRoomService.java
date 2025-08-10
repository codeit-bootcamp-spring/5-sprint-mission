package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.ChatRoom;

import java.util.Set;
import java.util.UUID;

public interface ChatRoomService {

    ChatRoom create(Set<UUID> participants);

    Set<UUID> getMessages(UUID roomId);

    void addParticipant(UUID roomId, UUID userId);

    void removeParticipant(UUID roomId, UUID userId);
}