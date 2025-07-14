package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface ChatService {

    boolean joinChannel(UUID userId, UUID channelId);
    boolean leaveChannel(UUID userId, UUID channelId);
    boolean sendMessage(UUID userId, UUID channelId, String content);
    void viewChannel(UUID channelId);
}
