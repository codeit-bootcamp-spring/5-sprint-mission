package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.ChatRoom;
import java.util.List;
import java.util.UUID;

public interface ChatRoomService extends BaseService<ChatRoom> {
  void addMessage(UUID roomId, UUID messageId);

  List<UUID> getMessages(UUID roomId);

  void addParticipant(UUID roomId, UUID userId);

  void removeParticipant(UUID roomId, UUID userId);

  boolean isParticipant(UUID roomId, UUID userId);
}
