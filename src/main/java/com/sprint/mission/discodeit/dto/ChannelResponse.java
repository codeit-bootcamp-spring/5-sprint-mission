package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChannelResponse {
    private UUID id;
    private String channelName;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastMessageAt;
    private List<UUID> participantIds;
}
