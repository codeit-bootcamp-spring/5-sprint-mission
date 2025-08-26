package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.ChannelParticipant;

import java.util.List;
import java.util.UUID;

public interface ChannelParticipantService {
    ChannelParticipant addParticipant(UUID channelId, UUID userId);
    void removeParticipant(UUID channelId, UUID userId);
    List<ChannelParticipant> findParticipantsByChannelId(UUID channelId);
    List<ChannelParticipant> findChannelsByUserId(UUID userId);
}
