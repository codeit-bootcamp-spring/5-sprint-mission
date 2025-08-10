package com.sprint.mission.discodeit.service.dev;

import java.util.List;
import java.util.UUID;

public interface DevChatRoomService {

    void addMessage(UUID roomId, UUID messageId);

    List<UUID> getMessages(UUID roomId);

    void addParticipant(UUID roomId, UUID userId);

    void removeParticipant(UUID roomId, UUID userId);

    void printMessages(UUID roomId);

    List<String> getParticipantNames(UUID roomId);
}
