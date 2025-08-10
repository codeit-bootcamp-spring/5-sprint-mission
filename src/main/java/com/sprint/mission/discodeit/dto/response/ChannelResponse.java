package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelResponse {
    private UUID id;
    private ChannelType type;
    private String name;
    private String description;
    private Instant latestMessageTime;
    private List<UUID> participantIds;
}
