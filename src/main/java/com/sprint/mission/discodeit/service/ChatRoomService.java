package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

public interface ChatRoomService {
    void addMessage(UUID roomId, UUID messageId);

    List<UUID> getMessages(UUID roomId);

    void addParticipant(UUID roomId, UUID userId);

    void removeParticipant(UUID roomId, UUID userId);

    void printMessages(UUID roomId);

    List<String> getParticipantNames(UUID roomId);
}
